package cz.muni.fi.umlspnp.transformations;

import cz.muni.fi.umlspnp.models.OperationType;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentTarget;
import cz.muni.fi.umlspnp.models.deploymentdiagram.State;
import cz.muni.fi.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.TimedTransition;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import cz.muni.fi.spnp.core.transformators.spnp.distributions.ExponentialTransitionDistribution;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;

/**
 *  The execution (leaf) service segment which represents a call to a leaf message.
 *
 */
public class ServiceLeafSegment extends Segment implements ActionServiceSegment {
    private final String commentPrefix;
    private final List<PhysicalSegment> physicalSegments;
    private final ServiceCallTreeNode serviceCallNode;
    private final ServiceCall serviceCall;
    
    protected ImmediateTransition initialTransition = null;
    protected ImmediateTransition flushTransition = null;

    protected StandardPlace startPlace = null;
    
    protected TimedTransition endTransition = null;
    protected StandardPlace endPlace = null;
    
    protected ImmediateTransition failHWTransition = null;
    protected StandardPlace failHWPlace = null;
    
    protected Map<TimedTransition, Pair<StandardPlace, Boolean>> failTypes = new HashMap<>();

    
    public ServiceLeafSegment(PetriNet petriNet,
                              boolean generateComments,
                              List<PhysicalSegment> physicalSegments,
                              ServiceCallTreeNode serviceCallNode,
                              ServiceCall serviceCall) {
        super(petriNet, generateComments);
        
        this.physicalSegments = physicalSegments;
        this.serviceCallNode = serviceCallNode;
        this.serviceCall = serviceCall;
        
        this.commentPrefix = String.format("Execution service segment [\"%s\"]", serviceCall.getMessage().nameProperty().getValue());
    }
    
    public StandardPlace getStartPlace() {
        return startPlace;
    }
    
    public StandardPlace getFailHWPlace() {
        return failHWPlace;
    }
    
    public ImmediateTransition getInitialTransition() {
        return initialTransition;
    }
    
    public ImmediateTransition getFailHWTransition() {
        return failHWTransition;
    }
    
    public ImmediateTransition getFlushTransition() {
        return flushTransition;
    }
    
    public TimedTransition getEndTransition() {
        return endTransition;
    }
    
    @Override
    public StandardPlace getEndPlace() {
        return endPlace;
    }
    
    @Override
    public Collection<StandardPlace> getFailPlaces() {
        var places = new ArrayList<StandardPlace>();
        failTypes.values().forEach(failType -> {
            places.add(failType.getKey());
        });
        places.add(failHWPlace);
        return places;
    }
    
    public Map<TimedTransition, Pair<StandardPlace, Boolean>> getFailTypes() {
        return failTypes;
    }
    
    private void transformInitialTransition(String messageName) {
        var initialTransitionName = SPNPUtils.createTransitionName(messageName, "start");
        initialTransition = new ImmediateTransition(SPNPUtils.transitionCounter++,
                                                    initialTransitionName,
                                                    SPNPUtils.TR_PRIORTY_DEFAULT,
                                                    null,
                                                    new ConstantTransitionProbability(1.0));
        if(generateComments)
            initialTransition.setCommentary(String.format("%s - Initial transition", commentPrefix));
        petriNet.addTransition(initialTransition);
    }
    
    private void createInitialTransitionGuard(String messageName) {
        var guardBody = new StringBuilder();
        guardBody.append(String.format("return mark(\"%s\") && !(", serviceCall.getPlace().getName()));

        guardBody.append(String.format("mark(\"%s\") || ", startPlace.getName()));
        guardBody.append(String.format("mark(\"%s\") || ", endPlace.getName()));
        failTypes.values().forEach(failType -> {
            var failTypePlace = failType.getKey();
            guardBody.append(String.format("mark(\"%s\") || ", failTypePlace.getName()));
        });
        guardBody.append(String.format("mark(\"%s\"));", failHWPlace.getName()));
        
        var startGuardName = SPNPUtils.createFunctionName(String.format("guard_%s_leaf_start", SPNPUtils.prepareName(messageName, 15)));
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>(startGuardName, FunctionType.Guard, guardBody.toString(), Integer.class);

        petriNet.addFunction(startGuard);
        initialTransition.setGuardFunction(startGuard);
    }
    
    private void transformStartPlace(String messageName) {
        var startPlaceName = SPNPUtils.createPlaceName(messageName, "start");
        startPlace = new StandardPlace(SPNPUtils.placeCounter++, startPlaceName);
        if(generateComments)
            startPlace.setCommentary(String.format("%s - Start place", commentPrefix));
        petriNet.addPlace(startPlace);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, startPlace, initialTransition);
        petriNet.addArc(outputArc);
    }

    private void transformFlushTransition(String messageName) {
        var flushTransitionName = SPNPUtils.createTransitionName(messageName, "flush");
        flushTransition = new ImmediateTransition(SPNPUtils.transitionCounter++,
                                                  flushTransitionName,
                                                  SPNPUtils.TR_PRIORTY_ACTION_FLUSH,
                                                  null,
                                                  new ConstantTransitionProbability(1.0));
        if(generateComments)
            flushTransition.setCommentary(String.format("%s - Flush transition", commentPrefix));
        petriNet.addTransition(flushTransition);
    }

    private void createFlushTransitionGuard(String messageName, StandardPlace dependentPlace) {
        var existingGuard = flushTransition.getGuardFunction();
        if(existingGuard != null)
            petriNet.removeFunction(existingGuard);
        
        var guardBody = new StringBuilder("return (");
        guardBody.append(String.format("mark(\"%s\")", endPlace.getName()));
        guardBody.append(String.format(" || mark(\"%s\")", failHWPlace.getName()));
        failTypes.values().forEach(failTypePlace -> {
            guardBody.append(String.format(" || mark(\"%s\")", failTypePlace.getKey().getName()));
        });
        guardBody.append(String.format(") &&%n       ("));
        
        if(dependentPlace != null)
            guardBody.append(String.format("mark(\"%s\")", dependentPlace.getName()));
        else
            guardBody.append("0");
        guardBody.append(");");

        var flushGuardName = SPNPUtils.createFunctionName(String.format("guard_%s_leaf_flush", SPNPUtils.prepareName(messageName, 15)));
        FunctionSPNP<Integer> flushGuard = new FunctionSPNP<>(flushGuardName, FunctionType.Guard, guardBody.toString(), Integer.class);

        petriNet.addFunction(flushGuard);
        flushTransition.setGuardFunction(flushGuard);
    }
    
    private double getOperationSpeedLimit(OperationType operationType, DeploymentTarget dt, State state) {
        if(operationType != null) {
            for(var operation : dt.getStateOperations()) {
                if(state == operation.getState()) {
                    for(var operationEntry : operation.getOperationEntries()) {
                        if(operationEntry.getOperationType() == operationType && operationEntry.getSpeedLimit() >= 0) {
                            return (((double) operationEntry.getSpeedLimit()) / 100);
                        }
                    }
                    break;
                }
            }
        }
        return 1.0;
    }
    
    private FunctionSPNP<Double> createEndRateDistributionFunction(String messageName) {
        var message = serviceCall.getMessage();
        var activation = message.getTo();
        var artifact = activation.getLifeline().getArtifact();
        var dt = SPNPUtils.getDeploymentTargetFromArtifact(artifact);

        var executionTime = (double) serviceCall.getMessage().getExecutionTimeValue();
        executionTime = executionTime > 0 ? executionTime : 1.0;
        var operationType = message.getOperationType();

        var functionBody = new StringBuilder();
        for(var state : dt.getStates()) {
            if(!state.isStateDOWN()) {
                double speedCoefficient = getOperationSpeedLimit(operationType, dt, state);

                var statePlace = SPNPUtils.getStatePlace(physicalSegments, dt, state);
                if(statePlace != null){
                    if(functionBody.length() > 0)
                        functionBody.append(" + ");
                    functionBody.append(String.format("(mark(\"%s\") * %f * %f)", statePlace.getName(), executionTime, speedCoefficient));
                }
                else{
                    System.err.println(String.format("Leaf service segment transformation error: hw place for state %s not found.",
                                        state.nameProperty().getValue()));
                }
            }
        }
        if(functionBody.length() < 1)
            functionBody.append("1");
        functionBody.insert(0, "return 1 / (");
        functionBody.append(");");
        
        var functionName = SPNPUtils.createFunctionName(String.format("leaf_trans_dist_func__%s", SPNPUtils.prepareName(messageName, 15)));
        return new FunctionSPNP<>(functionName, FunctionType.Distribution, functionBody.toString(), Double.class);
    }

    private void transformEnd(String messageName) {
        var endPlaceName = SPNPUtils.createPlaceName(messageName, "end");
        endPlace = new StandardPlace(SPNPUtils.placeCounter++, endPlaceName);
        if(generateComments)
            endPlace.setCommentary(String.format("%s - End place", commentPrefix));
        petriNet.addPlace(endPlace);

        var endTransitionName = SPNPUtils.createTransitionName(messageName, "end");
        var distributionFunction = createEndRateDistributionFunction(messageName);
        var distribution = new ExponentialTransitionDistribution(distributionFunction);
        endTransition = new TimedTransition(SPNPUtils.transitionCounter++, endTransitionName, SPNPUtils.TR_PRIORTY_DEFAULT, null, distribution);
        if(generateComments)
            endTransition.setCommentary(String.format("%s - End transition", commentPrefix));
        petriNet.addTransition(endTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, endTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, endPlace, endTransition);
        petriNet.addArc(outputArc);
        
        var cardinalityFunctionName = SPNPUtils.createFunctionName(String.format("cardinality_%s_end_to_flush", SPNPUtils.prepareName(messageName, 15)));
        var cardinalityFunctionBody = String.format("return mark(\"%s\");", endPlace.getName());
        var cardinalityFunction = new FunctionSPNP<Integer>(cardinalityFunctionName, FunctionType.ArcCardinality, cardinalityFunctionBody, Integer.class);
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, endPlace, flushTransition, cardinalityFunction);
        petriNet.addArc(flushInputArc);
    }

    private Set<ServiceCallTreeNode> getMarkedNodesInTree() {
        var result = new HashSet<ServiceCallTreeNode>();
        result.add(serviceCallNode);
        serviceCallNode.setMarkedForLabelCheck(true);

        var node = serviceCallNode;
        while(!node.isRoot() && !node.isProcessed()) {
            node.setProcessed(true);
            node = node.getParent();
            node.setMarkedForLabelCheck(true);
            result.add(node);
        }
        if(node != serviceCallNode)
            node.setMarkedForLabelCheck(false);
        return result;
    }
    
    private boolean shouldGenerateLabelGuardCondition(DeploymentTarget dt, State state, OperationType operationType) {
        if(state.isStateDOWN())
            return false;

        for(var stateOperation : dt.getStateOperations()) {
            if(stateOperation.getState() == state) {
                for(var opEntry : stateOperation.getOperationEntries()){
                    if(opEntry.getOperationType() == operationType)
                        return false;
                }
            }
        }
        return true;
    }
    
    private String getMessageOperationTypesString(DeploymentTarget dt, Message message) {
        var result = new StringBuilder();
        if(message != null) {
            var operationType = message.getOperationType();
            
            if(operationType != null) {
                dt.getStates().forEach(state -> {
                    if(shouldGenerateLabelGuardCondition(dt, state, operationType)) {
                        var statePlace = SPNPUtils.getStatePlace(physicalSegments, dt, state);
                        if(statePlace != null) {
                            result.append(String.format(" || mark(\"%s\")", statePlace.getName()));
                        }
                    }
                });
            }
        }
        return result.toString();
    }
    
    private String getNodeRedundancyGroupString(DeploymentTarget dt) {
        var downPlace = SPNPUtils.getDownPlace(physicalSegments, dt);
        var downPlaceString = String.format("mark(\"%s\")", downPlace.getName());

        var redundancyGroup = dt.getRedundancyGroup();
        if(redundancyGroup == null)
            return downPlaceString;
        
        var result = new StringBuilder("(");
        result.append(downPlaceString);
        
        redundancyGroup.getNodes().forEach(node -> {
            if(node != dt) {
                var nodeDownPlace = SPNPUtils.getDownPlace(physicalSegments, node);
                if(nodeDownPlace != null)
                    result.append(String.format(" && mark(\"%s\")", nodeDownPlace.getName()));
            }
        });
        result.append(")");
        return result.toString();
    }
    
    private FunctionSPNP<Integer> createHWFailGuard(String messageName) {
        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_HW_fail", SPNPUtils.prepareName(messageName, 15)));
        var guardBody = new StringBuilder("return ");

        var hwFailNodes = getMarkedNodesInTree();
        var controlSet = new HashSet<DeploymentTarget>();
        for(var treeNode : hwFailNodes) {
            var dt = SPNPUtils.getDeploymentTargetFromArtifact(treeNode.getArtifact());
            if(!controlSet.contains(dt)) {
                if(!controlSet.isEmpty())
                    guardBody.append(" || ");
                guardBody.append(getNodeRedundancyGroupString(dt));
            }
            var message = treeNode.getMessage();
            if(message != null && treeNode.isMarkedForLabelCheck()) {
                  // NOTE: Uncomment if both sending and receiving nodes should be checked for operation types
//                guardBody.append(getMessageOperationTypesString(getDeploymentTargetFromArtifact(message.getFrom().getLifeline().getArtifact()), message));
//                if(!message.isSelfMessage())
                guardBody.append(getMessageOperationTypesString(SPNPUtils.getDeploymentTargetFromArtifact(message.getTo().getLifeline().getArtifact()), message));
            }
            controlSet.add(dt);
        }

        if(hwFailNodes.size() < 1)
            guardBody.append("0");
        guardBody.append(";");
        return new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody.toString(), Integer.class);
    }
    
    private void transformFailHW(String messageName) {
        var failHWPlaceName = SPNPUtils.createPlaceName(messageName, "HW_fail");
        failHWPlace = new StandardPlace(SPNPUtils.placeCounter++, failHWPlaceName);
        if(generateComments)
            failHWPlace.setCommentary(String.format("%s - Hardware failure place", commentPrefix));
        petriNet.addPlace(failHWPlace);

        var failHWTransitionName = SPNPUtils.createTransitionName(messageName, "HW_fail");
        failHWTransition = new ImmediateTransition(SPNPUtils.transitionCounter++,
                                                   failHWTransitionName,
                                                   SPNPUtils.TR_PRIORTY_DEFAULT, 
                                                   createHWFailGuard(messageName),
                                                   new ConstantTransitionProbability(1.0));
        if(generateComments)
            failHWTransition.setCommentary(String.format("%s - Hardware failure transition", commentPrefix));
        petriNet.addTransition(failHWTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, failHWTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, failHWPlace, failHWTransition);
        petriNet.addArc(outputArc);
        
        var cardinalityFunctionName = SPNPUtils.createFunctionName(String.format("cardinality_%s_HW_fail_to_flush", SPNPUtils.prepareName(messageName, 15)));
        var cardinalityFunctionBody = String.format("return mark(\"%s\");", failHWPlace.getName());
        var cardinalityFunction = new FunctionSPNP<>(cardinalityFunctionName, FunctionType.ArcCardinality, cardinalityFunctionBody, Integer.class);
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, failHWPlace, flushTransition, cardinalityFunction);
        petriNet.addArc(flushInputArc);
    }
    
    private void transformFailType(String messageName, String failureName, double failureRate, boolean causeHWfailure) {
        var failTypePlaceName = SPNPUtils.createPlaceName(messageName, "FT_" + failureName);
        var failTypePlace = new StandardPlace(SPNPUtils.placeCounter++, failTypePlaceName);
        if(generateComments)
            failTypePlace.setCommentary(String.format("%s - Failure place (\"%s\")", commentPrefix, failureName));
        petriNet.addPlace(failTypePlace);

        var failTypeTransitionName = SPNPUtils.createTransitionName(messageName, "FT_" + failureName);
        var distribution = new ExponentialTransitionDistribution(failureRate);
        var failTypeTransition = new TimedTransition(SPNPUtils.transitionCounter++, failTypeTransitionName, SPNPUtils.TR_PRIORTY_DEFAULT, null, distribution);
        if(generateComments)
            failTypeTransition.setCommentary(String.format("%s - Failure transition (\"%s\")", commentPrefix, failureName));
        petriNet.addTransition(failTypeTransition);

        failTypes.put(failTypeTransition, new Pair(failTypePlace, causeHWfailure));
        
        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, failTypeTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, failTypePlace, failTypeTransition);
        petriNet.addArc(outputArc);
        
        var cardinalityFunctionName = SPNPUtils.createFunctionName(String.format("cardinality_%s_FT_to_flush", SPNPUtils.prepareName(messageName, 15)));
        var cardinalityFunctionBody = String.format("return mark(\"%s\");", failTypePlace.getName());
        var cardinalityFunction = new FunctionSPNP<Integer>(cardinalityFunctionName, FunctionType.ArcCardinality, cardinalityFunctionBody, Integer.class);
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, failTypePlace, flushTransition, cardinalityFunction);
        petriNet.addArc(flushInputArc);
    }

    @Override
    public void transform() {
        var message = serviceCall.getMessage();
        var messageName = message.nameProperty().getValue();

        // Initial transition
        transformInitialTransition(messageName);

        // Start place
        transformStartPlace(messageName);

        // Flush transition
        transformFlushTransition(messageName);

        // End place and transition
        transformEnd(messageName);

        // HW Fail place and transition
        transformFailHW(messageName);

        // Service call fail types - places and transitions
        message.getMessageFailures().forEach(messageFailure -> {
            transformFailType(messageName,
                              messageFailure.nameProperty().getValue(),
                              messageFailure.rateProperty().getValue(),
                              messageFailure.causeHWfailProperty().getValue());
        });

        // Initial transition guard function
        createInitialTransitionGuard(messageName);
        
        // Flush transition guard function
        // This guard may be altered later after the loops segments are transformed
        createFlushTransitionGuard(messageName, null);
    }
    
    @Override
    public String toString() {
        var result = new StringBuilder();
        result.append(String.format("[InitialTransition %s]", initialTransition.getName()));
        result.append(String.format(" -> (StartPlace %s)", startPlace.getName()));
        int offset = result.toString().length();
        result.append(String.format(" -> [EndTransition %s]", endTransition.getName()));
        result.append(String.format(" -> (EndPlace %s)", endPlace.getName()));
        result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));

        failTypes.keySet().forEach(failTransition -> {
            var failPlace = failTypes.get(failTransition);
            result.append(System.lineSeparator());
            for(int i = 0; i < offset; i++)
                result.append(" ");
            result.append(String.format(" -> [FailTransition %s]", failTransition.getName()));
            result.append(String.format(" -> (FailPlace %s)", failPlace.getKey().getName()));
            result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));
        });

        result.append(System.lineSeparator());
        for(int i = 0; i < offset; i++)
            result.append(" ");
        result.append(String.format(" -> [FailHWTransition %s]", this.failHWTransition.getName()));
        result.append(String.format(" -> (FailHWPlace %s)", failHWPlace.getName()));
        result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));

        result.insert(0, String.format("Execution Service Segment - message \"%s\":%n", serviceCall.getMessage().nameProperty().getValue()));
        return result.toString();
    }

    @Override
    public void setFlushTransitionGuardDependentPlace(StandardPlace dependentPlace) {
        var message = serviceCall.getMessage();
        var messageName = message.nameProperty().getValue();
        createFlushTransitionGuard(messageName, dependentPlace);
    }
    
}
