package com.mycompany.umlspnp.transformations;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.transformators.spnp.*;
import cz.muni.fi.spnp.core.transformators.spnp.code.Include;
import cz.muni.fi.spnp.core.transformators.spnp.code.SPNPCode;
import cz.muni.fi.spnp.core.transformators.spnp.options.Option;
import cz.muni.fi.spnp.core.transformators.spnp.options.SPNPOptions;
import cz.muni.fi.spnp.core.transformators.spnp.parameters.InputParameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 *  Transforms the deployment and sequence models into the appropriate SPNP code.
 *
 */
public class Transformator {
    MainModel model = null;

    private final SPNPTransformator transformator;
    private final SPNPCode code;
    private final SPNPOptions options;
    private final PetriNet petriNet;

    private final List<PhysicalSegment> physicalSegments = new ArrayList<>();
    private final List<CommunicationSegment> communicationSegments = new ArrayList<>();
    private ControlServiceSegment controlServiceSegment = null;

    private final ServiceCallTree serviceCallTree;

    public Transformator(MainModel mainModel){
        this.model = mainModel;

        this.code = createCode();
        this.options = createOptions();
        this.petriNet = createPetriNet();
        this.transformator = new SPNPTransformator(code, options);
        
        this.serviceCallTree = new ServiceCallTree(mainModel.getSequenceDiagram());
        
        // TODO: remove prints when not needed
        System.err.println(String.format("Service Call Tree:%n"));
        System.err.println(serviceCallTree);
    }

    protected final SPNPCode createCode() {
        var spnpCode = new SPNPCode();
        spnpCode.addInclude(new Include("\"user.h\""));
        return spnpCode;
    }

    protected final SPNPOptions createOptions() {
        var parametersSet = new HashSet<InputParameter>();

        var optionsSet = new HashSet<Option>();

        return new SPNPOptions(parametersSet, optionsSet);
    }

    protected final PetriNet createPetriNet() {
        var net = new PetriNet();

        /* Any global net alterations here */

        return net;
    }

    /**
     * Performs the transformation into the intermediate Petri net model.
     * Several segments are transformed in phases because they depend on 
     * some other segment which needs to be transformed first
     */
    public void transform() {
        SPNPUtils.resetCounters();
        
        var treeRoot = serviceCallTree.getRoot();
        if(treeRoot == null) {
            System.err.println("Transformator error: Service call tree is empty (no highest lifeline activation found)");
            return;
        }
        var deploymentDiagram = model.getDeploymentDiagram();
        var sequenceDiagram = model.getSequenceDiagram();

        // Physical segments
        var elements = deploymentDiagram.getElementContainer();
        elements.getNodes().values().forEach(node -> {
            if(node instanceof DeploymentTarget) {
                var physicalSegment = new PhysicalSegment(petriNet, (DeploymentTarget) node);
                physicalSegment.transform();
                physicalSegments.add(physicalSegment);
            }
        });

        // Physical segment dependency transformations
        physicalSegments.forEach(physicalSegment -> {
            physicalSegment.transformPhysicalSegmentDependencies(physicalSegments);
        });

        // Communication segments
        deploymentDiagram.getCommunicationLinks().forEach(communicationLink -> {
            var communicationSegment = new CommunicationSegment(petriNet, treeRoot, communicationLink);
            communicationSegment.transform();
            communicationSegments.add(communicationSegment);
        });

        // Control service segment
        var loops = sequenceDiagram.getLoops();
        controlServiceSegment = new ControlServiceSegment(petriNet, physicalSegments, communicationSegments, loops, treeRoot);
        controlServiceSegment.transform();

        // Communictaion segment finish Control Service Segment dependent transformations
        communicationSegments.forEach(communicationSegment -> {
            communicationSegment.transformControlServiceSegmentDependencies(controlServiceSegment);
            communicationSegment.transformPhysicalSegmentDependencies(physicalSegments);
        });
        
        printDebugInfo();
    }
    
    private void printDebugInfo() {
        System.err.println(String.format("Debug info%n------------"));

        // Physical Segments
        physicalSegments.forEach(physicalSegment -> {
            System.err.println(physicalSegment.toString());
            System.err.println(System.lineSeparator());
        });
        
        // Control Service Segment
        System.err.println(controlServiceSegment.toString());
        System.err.println(System.lineSeparator());

        // Execution Segments
        controlServiceSegment.getControlServiceCalls().forEach(controlSegmentPair -> {
            var serviceCall = controlSegmentPair.getValue();
            if(serviceCall.isExecutionServiceCall()) {
                var executionSegment = (ServiceLeafSegment) serviceCall.getActionSegment();
                System.err.println(executionSegment.toString());
                System.err.println(System.lineSeparator());
            }
        });

        // Communication Segments
        communicationSegments.forEach(communicationSegment -> {
            System.err.println(communicationSegment.toString());
            System.err.println(System.lineSeparator());
        });
    }

    /**
     * Transforms the intermediate Petri net model into SPNP code
     * @return Final SPNP code representing the modeled system.
     */
    public String getOutput(){
        return transformator.transform(petriNet);
    }
}
