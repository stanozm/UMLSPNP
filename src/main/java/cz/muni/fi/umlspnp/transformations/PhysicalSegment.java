package cz.muni.fi.umlspnp.transformations;

import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentTarget;
import cz.muni.fi.umlspnp.models.deploymentdiagram.State;
import cz.muni.fi.umlspnp.models.deploymentdiagram.StateTransition;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Physical segment of the net which is modeled for each deployment target node.
 *
 */
public class PhysicalSegment extends Segment {
    protected final DeploymentTarget node;
    protected Map<State, StandardPlace> statePlaces = new HashMap<>();
    
    protected Map<StateTransition, TimedTransition> stateTransitions = new HashMap<>();
    protected Map<State, ImmediateTransition> parentFailTransitions = new HashMap<>();

    public PhysicalSegment(PetriNet petriNet, DeploymentTarget node) {
        super(petriNet, 10);
        
        this.node = node;
    }
    
    public DeploymentTarget getNode() {
        return node;
    }
    
    public Map<State, StandardPlace> getStatePlaces() {
        return statePlaces;
    }
    
    public StandardPlace getStatePlace(State wantedState) {
        for(var state : statePlaces.keySet()) {
            if(state == wantedState){
                return statePlaces.get(state);
            }
        }
        return null;
    }

    public StandardPlace getDownStatePlace() {
        for(var state : statePlaces.keySet()) {
            if(state.isStateDOWN()){
                return statePlaces.get(state);
            }
        }
        return null;
    }

    private void transformState(String nodeName, State state) {
        var statePlaceName = SPNPUtils.createPlaceName(nodeName, state.nameProperty().getValue());
        var statePlace = new StandardPlace(SPNPUtils.placeCounter++, statePlaceName);
        if(state.isDefaultProperty().getValue())
            statePlace.setNumberOfTokens(1);
        petriNet.addPlace(statePlace);
        statePlaces.put(state, statePlace);
    }

    private void transformTransition(String nodeName, StateTransition transition) {
        var transitionName = SPNPUtils.createTransitionName(nodeName, transition.nameProperty().getValue());
        var rate = transition.rateProperty().getValue();
        var stateTransition = new TimedTransition(SPNPUtils.transitionCounter++, transitionName, new ExponentialTransitionDistribution(rate));
        stateTransition.setPriority(transitionPriority);
        petriNet.addTransition(stateTransition);

        var stateFrom = transition.getStateFrom();
        var placeFrom = statePlaces.get(stateFrom);
        if(placeFrom == null) {
            System.err.println(String.format("Physical segment: Node \"%s\": Could not find place for source state \"%s\"", nodeName, stateFrom.nameProperty().getValue()));
        }
        else {
            var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, placeFrom, stateTransition);
            petriNet.addArc(inputArc);
        }
        var stateTo = transition.getStateTo();
        var placeTo = statePlaces.get(stateTo);
        if(placeFrom == null) {
            System.err.println(String.format("Physical segment: Node \"%s\": Could not find place for destination state \"%s\"", nodeName, stateTo.nameProperty().getValue()));
        }
        else {
            var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, placeTo, stateTransition);
            petriNet.addArc(outputArc);
        }

        stateTransitions.put(transition, stateTransition);
    }
    
    private FunctionSPNP<Integer> createStateToDownGuard(String nodeName, List<StandardPlace> failPlaces) {
        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_state_to_down", SPNPUtils.prepareName(nodeName, 15)));
        String guardBody = String.format("return %s;", SPNPUtils.getPlacesString(failPlaces));
        return new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody, Integer.class);
    }

    private void transformStateToDown(String nodeName, State state, FunctionSPNP<Integer> guard) {
        var place = statePlaces.get(state);
        var downStatePlace = this.getDownStatePlace();

        var transitionName = SPNPUtils.createTransitionName(nodeName, "parent");
        var parentFailTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, transitionName,
                                    this.transitionPriority, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(parentFailTransition);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, downStatePlace, parentFailTransition);
        petriNet.addArc(outputArc);

        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, place, parentFailTransition);
        petriNet.addArc(flushInputArc);
        
        parentFailTransitions.put(state, parentFailTransition);
    }
    
    public void transform() {
        var nodeName = node.getNameProperty().getValue();
        // TODO should states be generated if they have no transitions?
        node.getStates().forEach(state -> {
            transformState(nodeName, state);
        });

        node.getStateTransitions().forEach(transition -> {
            transformTransition(nodeName, transition);
        });
    }
    
    public StandardPlace getParentDownPlace(List<PhysicalSegment> physicalSegments) {
        var parentNode = node.getParent();
        if(parentNode == null)
            return null;
        return SPNPUtils.getDownPlace(physicalSegments, parentNode);
    }
    
    private List<StandardPlace> getServiceFailPlaces(ControlServiceSegment controlSegment) {
        var serviceFailPlaces = new ArrayList<StandardPlace>();
        
        var serviceCalls = controlSegment.getControlServiceCalls(null);
        serviceCalls.forEach(serviceCall -> {
            var message = serviceCall.getMessage();
            var lifeline = message.getTo().getLifeline();
            var deploymentTarget = SPNPUtils.getDeploymentTargetFromArtifact(lifeline.getArtifact());
            if(deploymentTarget == node) {
                var actionSegment = serviceCall.getActionSegment();
                if(actionSegment instanceof ServiceLeafSegment) {
                    var failTypes = ((ServiceLeafSegment)actionSegment).getFailTypes().values();
                    failTypes.forEach(failType -> {
                        if(failType.getValue())
                            serviceFailPlaces.add(failType.getKey());
                    });
                }
            }
        });
        return serviceFailPlaces;
    }
    
    public void transformControlServiceSegmentDependencies(List<PhysicalSegment> physicalSegments, ControlServiceSegment controlSegment) {
        var failPlaces = getServiceFailPlaces(controlSegment);
        var parentDownPlace = getParentDownPlace(physicalSegments);
        if(parentDownPlace != null)
            failPlaces.add(parentDownPlace);

        var downPlace = getDownStatePlace();
        if(failPlaces.size() > 0 && downPlace != null) {
            var nodeName = node.getNameProperty().getValue();
            var guardFunction = createStateToDownGuard(nodeName, failPlaces);
            statePlaces.forEach((state, _unused) -> {
                if(!state.isStateDOWN())
                    transformStateToDown(nodeName, state, guardFunction);
            });
        }
    }
    
    public String getDebugPlaceString(State state, StandardPlace place) {
        var result = new StringBuilder();
        result.append("(");
        if(state.isStateDOWN())
            result.append("DownState ");
        result.append(String.format("%s)", place.getName()));
        if(place.getNumberOfTokens() > 0)
            result.append("*");
        return result.toString();
    }
    
    @Override
    public String toString() {
        var result = new StringBuilder();
        
        stateTransitions.keySet().forEach(stateTransition -> {
            var stateFrom = stateTransition.getStateFrom();
            var stateFromPlace = statePlaces.get(stateFrom);

            var stateTo = stateTransition.getStateTo();
            var stateToPlace = statePlaces.get(stateTo);

            var transition = stateTransitions.get(stateTransition);
            result.append(getDebugPlaceString(stateFrom, stateFromPlace));
            result.append(String.format(" -> [%s]", transition.getName()));
            result.append(" -> ");
            result.append(getDebugPlaceString(stateTo, stateToPlace));
            result.append(System.lineSeparator());
        });
        
        var parent = node.getParent();
        if(parent != null) {
            result.append(String.format("Parent down transitions for parent \"%s\":%n", parent.getNameProperty().getValue()));
            parentFailTransitions.keySet().forEach(state -> {
                var transition = parentFailTransitions.get(state);
                var place = statePlaces.get(state);
                var downPlace = this.getDownStatePlace();
                result.append(getDebugPlaceString(state, place));
                result.append(String.format(" -> [%s]", transition.getName()));
                result.append(String.format(" -> (DownState %s)", downPlace.getName()));
                result.append(System.lineSeparator());
            });
        }

        result.insert(0, String.format("Physical Segment - node \"%s\":%n", node.getNameProperty().getValue()));
        return result.toString();
    }
}
