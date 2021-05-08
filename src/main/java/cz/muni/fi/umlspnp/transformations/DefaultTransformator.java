package cz.muni.fi.umlspnp.transformations;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentTarget;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.*;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import cz.muni.fi.spnp.core.transformators.spnp.code.Include;
import cz.muni.fi.spnp.core.transformators.spnp.code.SPNPCode;
import cz.muni.fi.spnp.core.transformators.spnp.options.ConstantTypeOption;
import cz.muni.fi.spnp.core.transformators.spnp.options.ConstantValue;
import cz.muni.fi.spnp.core.transformators.spnp.options.DoubleTypeOption;
import cz.muni.fi.spnp.core.transformators.spnp.options.IntegerTypeOption;
import cz.muni.fi.spnp.core.transformators.spnp.options.Option;
import cz.muni.fi.spnp.core.transformators.spnp.options.OptionKey;
import cz.muni.fi.spnp.core.transformators.spnp.options.SPNPOptions;
import cz.muni.fi.spnp.core.transformators.spnp.parameters.InputParameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *  Transforms the deployment and sequence models into the appropriate SPNP code.
 *
 */
public class DefaultTransformator implements Transformator{
    MainModel model = null;

    private final SPNPTransformator transformator;
    private final SPNPCode code;
    private final SPNPOptions options;
    private final PetriNet petriNet;

    private final List<PhysicalSegment> physicalSegments = new ArrayList<>();
    private final List<CommunicationSegment> communicationSegments = new ArrayList<>();
    private ControlServiceSegment controlServiceSegment = null;

    private final ServiceCallTree serviceCallTree;

    public DefaultTransformator(MainModel mainModel){
        this.model = mainModel;

        this.code = createCode();
        this.options = createOptions();
        this.petriNet = createPetriNet();
        this.transformator = new SPNPTransformator(code, options);
        
        this.serviceCallTree = new ServiceCallTree(mainModel.getSequenceDiagram());
    }

    protected final SPNPCode createCode() {
        var spnpCode = new SPNPCode();
        spnpCode.addInclude(new Include("\"user.h\""));
        
        var acFinalBody = String.format("solve(INFINITY);%nprint_qcol();%nprint_rgraph();%nprint_qrow();%npr_mc_info();%npr_std_average();");
        var acFinalFunction = new FunctionSPNP<>("ac_final", FunctionType.Other, acFinalBody, Void.class);
        spnpCode.setAcFinalFunction(acFinalFunction);
        
        return spnpCode;
    }

    protected final SPNPOptions createOptions() {
        var parametersSet = new HashSet<InputParameter>();
        var optionsSet = new HashSet<Option>();
        return new SPNPOptions(parametersSet, optionsSet);
    }
    
    public void createSPNPOptionConstant(String key, String value) {
        var option = OptionKey.valueOf(key);
        var optionValue = ConstantValue.valueOf(value);
        addSPNPOption(new ConstantTypeOption(option, optionValue));
    }

    public void createSPNPOptionInteger(String key, int value) {
        var option = OptionKey.valueOf(key);
        addSPNPOption(new IntegerTypeOption(option, value));
    }

    public void createSPNPOptionDouble(String key, double value) {
        var option = OptionKey.valueOf(key);
        addSPNPOption(new DoubleTypeOption(option, value));
    }
    
    private void addSPNPOption(Option option) {
        options.getOptions().add(option);
    }

    protected final PetriNet createPetriNet() {
        var net = new PetriNet();

        /* Any global net alterations here */

        return net;
    }

    public void printDebugInfo() {
        System.err.println(String.format("Service Call Tree:%n------------------"));
        System.err.println(serviceCallTree);
        
        System.err.println(String.format("%nPetri net:%n----------"));

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
    
    public void generatePrintingSegment() {
        var debugPrintSegment = new DebugPrintSegment(petriNet,
                                                      controlServiceSegment,
                                                      physicalSegments,
                                                      communicationSegments);
        debugPrintSegment.transform();
    }
    
    /**
     * Performs the transformation into the intermediate Petri net model.
     * Several segments are transformed in phases because they depend on 
     * some other segment which needs to be transformed first
     */
    public void transform(boolean generateComments) {
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
                var physicalSegment = new PhysicalSegment(petriNet, generateComments, (DeploymentTarget) node);
                physicalSegment.transform();
                physicalSegments.add(physicalSegment);
            }
        });

        // Communication segments
        deploymentDiagram.getCommunicationLinks().forEach(communicationLink -> {
            var communicationSegment = new CommunicationSegment(petriNet, generateComments, treeRoot, communicationLink);
            communicationSegment.transform();
            communicationSegment.transformPhysicalSegmentDependencies(physicalSegments);
            communicationSegments.add(communicationSegment);
        });

        // Control service segment
        var loops = sequenceDiagram.getLoops();
        controlServiceSegment = new ControlServiceSegment(petriNet, generateComments, physicalSegments, communicationSegments, loops, treeRoot);
        controlServiceSegment.transform();

        // Physical segment dependency transformations
        physicalSegments.forEach(physicalSegment -> {
            physicalSegment.transformControlServiceSegmentDependencies(physicalSegments, controlServiceSegment);
        });
        
        // Communictaion segment finish Control Service Segment dependent transformations
        communicationSegments.forEach(communicationSegment -> {
            communicationSegment.transformControlServiceSegmentDependencies(controlServiceSegment);
        });
    }

    /**
     * Transforms the intermediate Petri net model into SPNP code
     * @return Final SPNP code representing the modeled system.
     */
    @Override
    public String getOutput(){
        return transformator.transform(petriNet);
    }

}
