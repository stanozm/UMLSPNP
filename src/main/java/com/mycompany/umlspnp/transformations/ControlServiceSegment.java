/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.sequencediagram.Loop;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author 10ondr
 */
public class ControlServiceSegment extends Segment {
    private final List<PhysicalSegment> physicalSegments;
    private final List<CommunicationSegment> communicationSegments;
    private final Collection<Loop> loops;

    protected final ServiceCallNode treeRoot;

    protected final List<Pair<ImmediateTransition, ServiceCall>> controlServiceCalls = new ArrayList<>();
    protected ImmediateTransition initialTransition = null;
    protected StandardPlace endPlace = null;
    
    private final List<LoopSegment> loopSegments = new ArrayList<>();

    public ControlServiceSegment(PetriNet petriNet,
                            List<PhysicalSegment> physicalSegments,
                            List<CommunicationSegment> communicationSegments,
                            Collection<Loop> loops,
                            ServiceCallNode treeRoot) {
        super(petriNet, 5);

        this.physicalSegments = physicalSegments;
        this.communicationSegments = communicationSegments;
        this.loops = loops;
        this.treeRoot = treeRoot;
    }

    public List<ServiceCall> getControlServiceCalls(Message message) {
        List<ServiceCall> result = new ArrayList<>();
        for(var pair : controlServiceCalls) {
            var serviceCall = pair.getValue();
            if(message == serviceCall.getMessage()) {
                result.add(serviceCall);
            }
        }
        return result;
    }
    
    public ServiceCall getHighestControlServiceCall(ServiceCallNode serviceCallNode) {
        for(var pair : controlServiceCalls) {
            var serviceCall = pair.getValue();
            var message = serviceCall.getMessage();
            var node = serviceCallNode.getNodeWithMessage(message);
            if(node != null) {
                return serviceCall;
            }
        }
        return null;
    }

    public ActionServiceSegment transformExecutionServiceSegment(ServiceCall serviceCall, ServiceCallNode serviceCallNode) {
        var executionServiceSegment = new ServiceLeafSegment(petriNet, physicalSegments, communicationSegments, serviceCallNode, serviceCall);
        executionServiceSegment.transform();
        return executionServiceSegment;
    }
    
    private ActionServiceSegment getCommunicationSegment(Message message) {
        var communicationLink = SPNPUtils.getMessageCommunicationLink(message);
        for (var segment : communicationSegments) {
            if(communicationLink == segment.getCommunicationLink())
                return segment;
        }
        return null;
    }

    private void transformControlServiceCall(ServiceCallNode serviceCallNode, boolean isExecutionCall) {
        var artifact = serviceCallNode.getArtifact();
        var message = serviceCallNode.getMessage();
        String prefix = "C_";
        if(isExecutionCall)
            prefix = "L_";
        var messageName = prefix + message.nameProperty().getValue();

        var serviceCallPlaceName = SPNPUtils.createPlaceName(messageName, artifact.getNameProperty().getValue());
        var serviceCallPlace = new StandardPlace(SPNPUtils.placeCounter++, serviceCallPlaceName);
        petriNet.addPlace(serviceCallPlace);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, serviceCallPlace, getPreviousTransition());
        petriNet.addArc(outputArc);

        var serviceCall = new ServiceCall(message, serviceCallPlace);
        ActionServiceSegment actionServiceSegment;
        if(isExecutionCall)
            actionServiceSegment = transformExecutionServiceSegment(serviceCall, serviceCallNode);
        else
            actionServiceSegment = getCommunicationSegment(message);
        serviceCall.setActionSegment(actionServiceSegment);
        
        var serviceCallTransitionName = SPNPUtils.createTransitionName(messageName);
        var serviceCallTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, serviceCallTransitionName,
                                    this.transitionPriority, null, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(serviceCallTransition);
        
        // Arc from the execution/communication segment end place to the control segment transition
        var actionSegmentEndPlace = actionServiceSegment.getEndPlace();
        var endPlaceInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, actionSegmentEndPlace, serviceCallTransition);
        petriNet.addArc(endPlaceInputArc);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, serviceCallPlace, serviceCallTransition);
        petriNet.addArc(inputArc);

        controlServiceCalls.add(new Pair(serviceCallTransition, serviceCall));
    }
    
    private void transformCommunicationControlServiceCall(ServiceCallNode treeNode) {
        var message = treeNode.getMessage();
        if(message == null)
            return;
        
        var communicationLink = SPNPUtils.getMessageCommunicationLink(message);
        if(communicationLink == null)
            return;
        
        transformControlServiceCall(treeNode, false);
    }

    private void transformExecutionControlServiceCall(ServiceCallNode treeNode) {
        var message = treeNode.getMessage();
        if(message == null)
            return;

        if(treeNode.isLeaf())
            transformControlServiceCall(treeNode, true);
    }
    
    private void transformServiceCalls(ServiceCallNode treeNode) {
        for(var serviceCallNode : treeNode.getChildren()) {
            transformCommunicationControlServiceCall(serviceCallNode);
            transformExecutionControlServiceCall(serviceCallNode);

            transformServiceCalls(serviceCallNode);
        }
    }

    private void transformInitialTransition(String lifelineName) {
        var initTransitionName = SPNPUtils.createTransitionName(lifelineName, "start");
        initialTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, initTransitionName,
                            this.transitionPriority, null, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(initialTransition);
    }

    private void transformInitialTransitionGuard(String lifelineName) {
        var startGuardBody = new StringBuilder();

        var tokenStrings = getTokenStrings();

        if(tokenStrings.size() < 1){
            startGuardBody.append("return 1;");
        }
        else{
            // TODO also add loop places
            startGuardBody.append("return !(");
            tokenStrings.forEach(tokenString -> {
                startGuardBody.append(tokenString);
                if(tokenStrings.indexOf(tokenString) < tokenStrings.size() - 1)
                    startGuardBody.append(" || ");
            });
            startGuardBody.append(");");
        }
        String guardName = SPNPUtils.createFunctionName(String.format("guard_%s_control_start", SPNPUtils.prepareName(lifelineName, 15)));
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>(guardName, FunctionType.Guard,
                                                              startGuardBody.toString(), Integer.class);
        petriNet.addFunction(startGuard);
        initialTransition.setGuardFunction(startGuard);
    }

    private void transformEnd(String lifelineName) {
        var endPlaceName = SPNPUtils.createPlaceName(lifelineName, "end");
        endPlace = new StandardPlace(SPNPUtils.placeCounter++, endPlaceName);
        petriNet.addPlace(endPlace);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, endPlace, getPreviousTransition());
        petriNet.addArc(outputArc);
    }

    private void transformEndPlaceHaltingFunction(String lifelineName) {
        String functionName = SPNPUtils.createFunctionName(String.format("halting_%s", SPNPUtils.prepareName(lifelineName, 15)));
        FunctionSPNP<Integer> haltingFunction = new FunctionSPNP<>(functionName,
                                                                   FunctionType.Halting, String.format("return !mark(\"%s\");", endPlace.getName()),
                                                                   Integer.class);
        petriNet.addFunction(haltingFunction);
    }
    
    private void transformActionSegmentFlushTransitionGuard(ServiceCall controlServiceCall) {
        var actionSegment = controlServiceCall.getActionSegment();
        loopSegments.forEach(loopSegment -> {
            if(loopSegment.containsControlServiceCall(controlServiceCall)) {
                actionSegment.setFlushTransitionGuardDependentPlace(loopSegment.getFlushPlace());
            }
        });
    }
    
    private boolean validateLoop(List<ServiceCallNode> highestServiceCallNodes) {
        if(highestServiceCallNodes.size() < 1) {
            System.err.println("Loop transformation error: unable to find the highest tree node");
            return false;
        }
        else if(highestServiceCallNodes.size() > 1) {
            var res = new StringBuilder();
            highestServiceCallNodes.forEach(serviceCall -> {
                res.append(serviceCall.getMessage().nameProperty().getValue());
                res.append("   ");
            });
            System.err.println(String.format("Loop transformation error: loop contains unrelated messages with the following highest messages: %s", res.toString()));
            return false;
        }
        return true;
    }
    
    private void transformLoop(Loop loop) {
        var highestServiceCallNodes = SPNPUtils.getLoopHighestControlServiceCall(this, treeRoot, loop);
        if(!validateLoop(highestServiceCallNodes))
            return;

        var loopSegment = new LoopSegment(petriNet, this, highestServiceCallNodes.get(0), loop);
        loopSegment.transform();
        loopSegments.add(loopSegment);
    }
    
    private ImmediateTransition getPreviousTransition() {
        if(controlServiceCalls.size() < 1)
            return initialTransition;
        return controlServiceCalls.get(controlServiceCalls.size() - 1).getKey();
    }

    public List<StandardPlace> getPlaces() {
        List<StandardPlace> places = new ArrayList<>();
        controlServiceCalls.forEach(serviceCallPair -> {
            places.add(serviceCallPair.getValue().getPlace());
        });
        return places;
    }

    public List<String> getTokenStrings() {
        List<String> tokenStrings = new ArrayList<>();
        var places = getPlaces();

        places.forEach(place -> {
            tokenStrings.add(String.format("mark(\"%s\")", place.getName()));
        });
        return tokenStrings;
    }
    
    public List<Pair<ImmediateTransition, ServiceCall>> getControlServiceCalls() {
        return controlServiceCalls;
    }
    
    public ImmediateTransition getInitialTransition() {
        return initialTransition;
    }
    
    public StandardPlace getEndPlace() {
        return endPlace;
    }

    public void transform() {
        var lifelineName = treeRoot.getArtifact().getNameProperty().getValue();
        
        // Initial transition
        transformInitialTransition(lifelineName);
        
        // Control service calls
        transformServiceCalls(treeRoot);

        // Initial tranision guard
        transformInitialTransitionGuard(lifelineName);
        
        // End place and transition
        transformEnd(lifelineName);
        transformEndPlaceHaltingFunction(lifelineName);
        
        // Loops
        loops.forEach(loop -> {
            transformLoop(loop);
        });
        
        // Action segments flush transitions (depend on loop segments)
        controlServiceCalls.forEach(serviceCallPair -> {
            var serviceCall = serviceCallPair.getValue();
            transformActionSegmentFlushTransitionGuard(serviceCall);
        });
    }
    
    @Override
    public String toString() {
        var result = new StringBuilder();
        result.append(String.format("Control Service Segment:%n"));
        result.append(String.format("[InitialTransition %s]", this.initialTransition.getName()));
        controlServiceCalls.forEach(pair -> {
            result.append(String.format(" -> (%s) -> [%s]", pair.getValue().getPlace().getName(), pair.getKey().getName()));
        });
        result.append(String.format(" -> (EndPlace %s)", this.endPlace.getName()));
        // TODO add loops ?
        return result.toString();
    }
}
