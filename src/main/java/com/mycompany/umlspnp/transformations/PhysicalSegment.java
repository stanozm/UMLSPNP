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

    public PhysicalSegment(PetriNet petriNet, DeploymentTarget node) {
        super(petriNet);
        
        this.node = node;
    }
    
    public DeploymentTarget getNode() {
        return node;
    }
    
    public Map<State, StandardPlace> getStatePlaces() {
        return statePlaces;
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
    }

    private void transformServiceGuard() {
        // TODO when properly specified
    }
    
    private void transformParentFail(String nodeName, StandardPlace place, StandardPlace downStatePlace, StandardPlace parentDownStatePlace) {
        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_parent_down", SPNPUtils.prepareName(nodeName, 15)));
        String guardBody = String.format("return mark(\"%s\");", parentDownStatePlace.getName());
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody, Integer.class);

        var transitionName = SPNPUtils.createTransitionName(nodeName, "parent");
        var parentFailTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, transitionName, 1, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(parentFailTransition);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, downStatePlace, parentFailTransition);
        petriNet.addArc(outputArc);

        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, place, parentFailTransition);
        petriNet.addArc(flushInputArc);
    }
    
    public void transform() {
        var nodeName = node.getNameProperty().getValue();
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
                        transformParentFail(nodeName, place, downPlace, parentDownPlace);
                });
            }
        }
    }
}
