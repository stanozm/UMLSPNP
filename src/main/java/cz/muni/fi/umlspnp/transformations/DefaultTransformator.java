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
    
    private void generatePrintingSegment() {
        var control_s = controlServiceSegment;
        var phys_s = physicalSegments;
        var comm_s = communicationSegments;
        var guardBody = new StringBuilder();
        
        guardBody.append(String.format("if(mark(\"%s\"))%n", control_s.initialPlace.getName()));
        guardBody.append(String.format("    fprintf(stderr, \"\\n\\n\\n\\n\");%n%n"));
        
        guardBody.append(String.format("/* CONTROL SEGMENT */%n"));
        guardBody.append(String.format("fprintf(stderr, \"CONTROL SEGMENT: "));
        guardBody.append(String.format("(%%d) -> [%%d]"));
        control_s.controlServiceCalls.forEach(servicePair -> {
            guardBody.append(String.format(" -> (%%d) -> [%%d]"));
        });
        guardBody.append(String.format(" -> (%%d)\\n\""));
        guardBody.append(String.format(", mark(\"%s\")", control_s.initialPlace.getName()));
        guardBody.append(String.format(", enabled(\"%s\")", control_s.getInitialTransition().getName()));
        control_s.controlServiceCalls.forEach(servicePair -> {
            var transition = servicePair.getKey();
            var place = servicePair.getValue().getPlace();
            guardBody.append(String.format(", mark(\"%s\")", place.getName()));
            guardBody.append(String.format(", enabled(\"%s\")", transition.getName()));
        });
        guardBody.append(String.format(", mark(\"%s\")", control_s.getEndPlace().getName()));
        guardBody.append(String.format(");%n"));
        
        phys_s.forEach(ps -> {
            guardBody.append(String.format("%n/* STRUCTURE SEGMENT of \"%s\" */%n", ps.getNode().getNameProperty().getValue()));
            guardBody.append(String.format("fprintf(stderr, \"STRUCTURE SEGMENT of %s: ", ps.getNode().getNameProperty().getValue()));
            var marks = new StringBuilder();
            ps.statePlaces.forEach((state, place) -> {
                guardBody.append(String.format("(%s %%d)  ", state.nameProperty().getValue()));
                marks.append(String.format(", mark(\"%s\")", place.getName()));
            });
            guardBody.append(String.format("\\n\""));
            guardBody.append(marks.toString());
            guardBody.append(String.format(");%n"));
        });
        
        comm_s.forEach(cs -> {
            var commString = new StringBuilder();
            var conditionString = new StringBuilder();
            conditionString.append(String.format("if(enabled(\"%s\") || enabled(\"%s\") || mark(\"%s\") || mark(\"%s\") || mark(\"%s\") || mark(\"%s\")",
                                                cs.getInitialTransition().getName(), cs.flushTransition.getName(),
                                                cs.startPlace.getName(), cs.endPlace.getName(),
                                                cs.failHWPlaceFirst.getName(), cs.failHWPlaceSecond.getName()));
            
            guardBody.append(String.format("%n/* COMMUNICATION SEGMENT of \"%s\" */%n", cs.getCommunicationLink().getLinkType().nameProperty().getValue()));
            commString.append(String.format("fprintf(stderr, \"COMMUNICATION SEGMENT of %s: ", cs.getCommunicationLink().getLinkType().nameProperty().getValue()));
            var marks = new StringBuilder();
            commString.append(String.format("[tr_start %%d] -> (pl_start %%d) -> [tr_end %%d] -> (pl_end %%d)%n"));
            int counter = 1;
            for(var tr : cs.getFailTypes().keySet()) {
                var pl = cs.getFailTypes().get(tr);
                commString.append(String.format("                    [tr_fail%d %%d] -> (pl_fail%d %%d)%n", counter, counter));
                counter++;
                marks.append(String.format(", enabled(\"%s\")", tr.getName()));
                marks.append(String.format(", mark(\"%s\")", pl.getName()));
                conditionString.append(String.format(" || mark(\"%s\")", pl.getName()));
            }
            commString.append(String.format("                    [tr_hw_fail_st %%d] -> (pl_hw_fail_st %%d)%n"));
            commString.append(String.format("                    [tr_hw_fail_nd %%d] -> (pl_hw_fail_nd %%d)"));
            commString.append(String.format("\\n\""));
            
            commString.append(String.format(", enabled(\"%s\")", cs.initialTransition.getName()));
            commString.append(String.format(", mark(\"%s\")", cs.startPlace.getName()));
            
            commString.append(String.format(", enabled(\"%s\")", cs.endTransition.getName()));
            commString.append(String.format(", mark(\"%s\")", cs.endPlace.getName()));
            commString.append(marks.toString());
            
            commString.append(String.format(", enabled(\"%s\")", cs.failHWTransitionFirst.getName()));
            commString.append(String.format(", mark(\"%s\")", cs.failHWPlaceFirst.getName()));
            
            commString.append(String.format(", enabled(\"%s\")", cs.failHWTransitionSecond.getName()));
            commString.append(String.format(", mark(\"%s\")", cs.failHWPlaceSecond.getName()));
            
            commString.append(String.format(");%n"));
            
            conditionString.append(String.format(")%n"));
            commString.insert(0, conditionString.toString());
            guardBody.append(commString.toString());
        });
        
        control_s.getControlServiceCalls().forEach(pair -> {
            var serviceCall = pair.getValue();
            var action_s = serviceCall.getActionSegment();
            if(action_s instanceof ServiceLeafSegment){
                var leaf_s = (ServiceLeafSegment) action_s;
                var leafString = new StringBuilder();
                var conditionString = new StringBuilder();
                conditionString.append(String.format("if(enabled(\"%s\") || enabled(\"%s\") || mark(\"%s\") || mark(\"%s\") || mark(\"%s\")",
                                    leaf_s.initialTransition.getName(), leaf_s.flushTransition.getName(),
                                    leaf_s.startPlace.getName(), leaf_s.endPlace.getName(),
                                    leaf_s.failHWPlace.getName()));

                guardBody.append(String.format("%n/* EXECUTION SEGMENT of \"%s\" */%n", serviceCall.getMessage().nameProperty().getValue()));
                leafString.append(String.format("fprintf(stderr, \"EXECUTION SEGMENT of %s: ", serviceCall.getMessage().nameProperty().getValue()));
                var marks = new StringBuilder();
                leafString.append(String.format("[tr_start %%d] -> (pl_start %%d) -> [tr_end %%d] -> (pl_end %%d)%n"));
                int counter = 1;
                for(var tr : leaf_s.getFailTypes().keySet()) {
                    var pl = leaf_s.getFailTypes().get(tr).getKey();
                    leafString.append(String.format("                    [tr_fail%d %%d] -> (pl_fail%d %%d)%n", counter, counter));
                    counter++;
                    marks.append(String.format(", enabled(\"%s\")", tr.getName()));
                    marks.append(String.format(", mark(\"%s\")", pl.getName()));
                    conditionString.append(String.format(" || mark(\"%s\")", pl.getName()));
                }
                leafString.append(String.format("                    [tr_hw_fail %%d] -> (pl_hw_fail %%d)"));
                leafString.append(String.format("\\n\""));

                leafString.append(String.format(", enabled(\"%s\")", leaf_s.initialTransition.getName()));
                leafString.append(String.format(", mark(\"%s\")", leaf_s.startPlace.getName()));
                leafString.append(String.format(", enabled(\"%s\")", leaf_s.endTransition.getName()));
                leafString.append(String.format(", mark(\"%s\")", leaf_s.endPlace.getName()));
                leafString.append(marks.toString());

                leafString.append(String.format(", enabled(\"%s\")", leaf_s.failHWTransition.getName()));
                leafString.append(String.format(", mark(\"%s\")", leaf_s.failHWPlace.getName()));

                leafString.append(String.format(");%n"));
                
                conditionString.append(String.format(")%n"));
                leafString.insert(0, conditionString.toString());
                guardBody.append(leafString.toString());
            }
        });

        
        guardBody.append(String.format("%nfprintf(stderr, \"\\n\");%n"));
        guardBody.append(String.format("%nreturn 0;"));
        
        var p1_name = SPNPUtils.createPlaceName("PRINT", "P1");
        var p1 = new StandardPlace(SPNPUtils.placeCounter++, p1_name);
        p1.setNumberOfTokens(1);
        petriNet.addPlace(p1);
        var p2_name = SPNPUtils.createPlaceName("PRINT", "P2");
        var p2 = new StandardPlace(SPNPUtils.placeCounter++, p2_name);
        petriNet.addPlace(p2);
        
        var t1_name = SPNPUtils.createTransitionName("PRINT", "T1");
        var guard = new FunctionSPNP<>("__PRINT_GUARD", FunctionType.Guard, guardBody.toString(), Integer.class);
        var t1 = new ImmediateTransition(SPNPUtils.transitionCounter++, t1_name, 1000, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(t1);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, p1, t1);
        petriNet.addArc(inputArc);
        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, p2, t1);
        petriNet.addArc(outputArc);
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

        // Communication segments
        deploymentDiagram.getCommunicationLinks().forEach(communicationLink -> {
            var communicationSegment = new CommunicationSegment(petriNet, treeRoot, communicationLink);
            communicationSegment.transform();
            communicationSegment.transformPhysicalSegmentDependencies(physicalSegments);
            communicationSegments.add(communicationSegment);
        });

        // Control service segment
        var loops = sequenceDiagram.getLoops();
        controlServiceSegment = new ControlServiceSegment(petriNet, physicalSegments, communicationSegments, loops, treeRoot);
        controlServiceSegment.transform();

        // Physical segment dependency transformations
        physicalSegments.forEach(physicalSegment -> {
            physicalSegment.transformControlServiceSegmentDependencies(physicalSegments, controlServiceSegment);
        });
        
        // Communictaion segment finish Control Service Segment dependent transformations
        communicationSegments.forEach(communicationSegment -> {
            communicationSegment.transformControlServiceSegmentDependencies(controlServiceSegment);
        });
        
        printDebugInfo();
        generatePrintingSegment();
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
