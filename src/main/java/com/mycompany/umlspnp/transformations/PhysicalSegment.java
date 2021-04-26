/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import com.mycompany.umlspnp.models.deploymentdiagram.State;
import com.mycompany.umlspnp.models.deploymentdiagram.StateTransition;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 10ondr
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

    private void transformServiceGuard() {
        // TODO when properly specified
    }
    
    private void transformParentFail(String nodeName, State state, StandardPlace parentDownStatePlace) {
        var place = statePlaces.get(state);
        var downStatePlace = this.getDownStatePlace();

        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_parent_down", SPNPUtils.prepareName(nodeName, 15)));
        String guardBody = String.format("return mark(\"%s\");", parentDownStatePlace.getName());
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody, Integer.class);

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
    
    public void transformPhysicalSegmentDependencies(List<PhysicalSegment> physicalSegments) {
        var nodeName = node.getNameProperty().getValue();
        var parentNode = this.node.getParent();

        if(parentNode != null) {
            var downPlace = this.getDownStatePlace();
            var parentDownPlace = SPNPUtils.getDownPlace(physicalSegments, parentNode);
            if(downPlace != null && parentDownPlace != null) {
                statePlaces.forEach((state, place) -> {
                    if(!state.isStateDOWN())
                        transformParentFail(nodeName, state, parentDownPlace);
                });
            }
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
