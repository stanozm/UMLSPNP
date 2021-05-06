package cz.muni.fi.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import java.util.List;

/**
 *  Generates a debug segment which has no effect on the solution, it just prints
 * relevant information for every marking.
 * The print statements are in a guard function which will always evaluate to zero.
 * This guard function is attached to a transition with highest priority (it calls
 * the guard in every marking).
 *
 */
public class DebugPrintSegment extends Segment {
    private final ControlServiceSegment controlServiceSegment;
    private final List<PhysicalSegment> physicalSegments;
    private final List<CommunicationSegment> communicationSegments;
    
    public DebugPrintSegment(PetriNet petriNet,
                             ControlServiceSegment controlServiceSegment,
                             List<PhysicalSegment> physicalSegments,
                             List<CommunicationSegment> communicationSegments) {
        super(petriNet);
        
        this.controlServiceSegment = controlServiceSegment;
        this.physicalSegments = physicalSegments;
        this.communicationSegments = communicationSegments;
    }
    
    public void transform() {
        var guardBody = new StringBuilder();
        generateControlSegmentDebugPrint(guardBody);
        generatePhysicalSegmentsDebugPrint(guardBody);
        generateCommunicationSegmentsDebugPrint(guardBody);
        generateExecutionSegmentsDebugPrint(guardBody);
        
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
        var t1 = new ImmediateTransition(SPNPUtils.transitionCounter++, t1_name,
                        SPNPUtils.TR_PRIORITY_DEBUG_PRINT, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(t1);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, p1, t1);
        petriNet.addArc(inputArc);
        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, p2, t1);
        petriNet.addArc(outputArc);
    }
    
    private void generateControlSegmentDebugPrint(StringBuilder guardBody) {
        var control_s = controlServiceSegment;
        guardBody.append(String.format("if(mark(\"%s\"))%n", control_s.getInitialPlace().getName()));
        guardBody.append(String.format("    fprintf(stderr, \"\\n\\n\\n\\n\");%n%n"));

        guardBody.append(String.format("/* CONTROL SEGMENT */%n"));
        guardBody.append(String.format("fprintf(stderr, \"CONTROL SEGMENT: "));
        guardBody.append(String.format("(%%d) -> [%%d]"));
        control_s.controlServiceCalls.forEach(_unused -> {
            guardBody.append(String.format(" -> (%%d) -> [%%d]"));
        });
        guardBody.append(String.format(" -> (%%d)\\n\""));
        guardBody.append(String.format(", mark(\"%s\")", control_s.getInitialPlace().getName()));
        guardBody.append(String.format(", enabled(\"%s\")", control_s.getInitialTransition().getName()));
        control_s.controlServiceCalls.forEach(servicePair -> {
            var transition = servicePair.getKey();
            var place = servicePair.getValue().getPlace();
            guardBody.append(String.format(", mark(\"%s\")", place.getName()));
            guardBody.append(String.format(", enabled(\"%s\")", transition.getName()));
        });
        guardBody.append(String.format(", mark(\"%s\")", control_s.getEndPlace().getName()));
        guardBody.append(String.format(");%n"));
    }

    private void generatePhysicalSegmentsDebugPrint(StringBuilder guardBody) {
        var phys_s = physicalSegments;
        
        phys_s.forEach(ps -> {
            var nodeName = ps.getNode().getNameProperty().getValue();
            guardBody.append(String.format("%n/* STRUCTURE SEGMENT of \"%s\" */%n", nodeName));
            guardBody.append(String.format("fprintf(stderr, \"STRUCTURE SEGMENT of %s: ", nodeName));
            var marks = new StringBuilder();
            ps.getStatePlaces().forEach((state, place) -> {
                guardBody.append(String.format("(%s %%d)  ", state.nameProperty().getValue()));
                marks.append(String.format(", mark(\"%s\")", place.getName()));
            });
            guardBody.append(String.format("\\n\""));
            guardBody.append(marks.toString());
            guardBody.append(String.format(");%n"));
        });
    }

    private void generateCommunicationSegmentsDebugPrint(StringBuilder guardBody) {
        var comm_s = communicationSegments;

        comm_s.forEach(cs -> {
            var commString = new StringBuilder();
            var conditionString = new StringBuilder();
            conditionString.append(String.format("if(enabled(\"%s\") || enabled(\"%s\") || mark(\"%s\") || mark(\"%s\") || mark(\"%s\")",
                                                cs.getInitialTransition().getName(), cs.getFlushTransition().getName(),
                                                cs.getStartPlace().getName(), cs.getEndPlace().getName(),
                                                cs.getFailHWPlace().getName()));
            
            var linkTypeName = cs.getCommunicationLink().getLinkType().nameProperty().getValue();
            guardBody.append(String.format("%n/* COMMUNICATION SEGMENT of \"%s\" */%n", linkTypeName));
            commString.append(String.format("fprintf(stderr, \"COMMUNICATION SEGMENT of %s: ", linkTypeName));
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
            commString.append(String.format("                    [tr_hw_fail %%d] -> (pl_hw_fail %%d)"));
            commString.append(String.format("\\n\""));
            
            commString.append(String.format(", enabled(\"%s\")", cs.getInitialTransition().getName()));
            commString.append(String.format(", mark(\"%s\")", cs.getStartPlace().getName()));
            
            commString.append(String.format(", enabled(\"%s\")", cs.getEndTransition().getName()));
            commString.append(String.format(", mark(\"%s\")", cs.getEndPlace().getName()));
            commString.append(marks.toString());

            commString.append(String.format(", enabled(\"%s\")", cs.getFailHWTransition().getName()));
            commString.append(String.format(", mark(\"%s\")", cs.getFailHWPlace().getName()));
            
            commString.append(String.format(");%n"));
            
            conditionString.append(String.format(")%n"));
            commString.insert(0, conditionString.toString());
            guardBody.append(commString.toString());
        });
    }
    
    private void generateExecutionSegmentsDebugPrint(StringBuilder guardBody) {
        var control_s = controlServiceSegment;

        control_s.getControlServiceCalls().forEach(pair -> {
            var serviceCall = pair.getValue();
            var action_s = serviceCall.getActionSegment();
            if(action_s instanceof ServiceLeafSegment){
                var leaf_s = (ServiceLeafSegment) action_s;
                var leafString = new StringBuilder();
                var conditionString = new StringBuilder();
                conditionString.append(String.format("if(enabled(\"%s\") || enabled(\"%s\") || mark(\"%s\") || mark(\"%s\") || mark(\"%s\")",
                                    leaf_s.getInitialTransition().getName(), leaf_s.getFlushTransition().getName(),
                                    leaf_s.getStartPlace().getName(), leaf_s.getEndPlace().getName(),
                                    leaf_s.getFailHWPlace().getName()));

                var messageName = serviceCall.getMessage().nameProperty().getValue();
                guardBody.append(String.format("%n/* EXECUTION SEGMENT of \"%s\" */%n", messageName));
                leafString.append(String.format("fprintf(stderr, \"EXECUTION SEGMENT of %s: ", messageName));
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

                leafString.append(String.format(", enabled(\"%s\")", leaf_s.getInitialTransition().getName()));
                leafString.append(String.format(", mark(\"%s\")", leaf_s.getStartPlace().getName()));
                leafString.append(String.format(", enabled(\"%s\")", leaf_s.getEndTransition().getName()));
                leafString.append(String.format(", mark(\"%s\")", leaf_s.getEndPlace().getName()));
                leafString.append(marks.toString());

                leafString.append(String.format(", enabled(\"%s\")", leaf_s.getFailHWTransition().getName()));
                leafString.append(String.format(", mark(\"%s\")", leaf_s.getFailHWPlace().getName()));

                leafString.append(String.format(");%n"));
                
                conditionString.append(String.format(")%n"));
                leafString.insert(0, conditionString.toString());
                guardBody.append(leafString.toString());
            }
        });
    }
}
