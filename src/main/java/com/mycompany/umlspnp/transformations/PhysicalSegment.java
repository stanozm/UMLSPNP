/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import com.mycompany.umlspnp.models.deploymentdiagram.State;
import com.mycompany.umlspnp.models.deploymentdiagram.StateTransition;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.TimedTransition;
import cz.muni.fi.spnp.core.transformators.spnp.distributions.ExponentialTransitionDistribution;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author 10ondr
 */
public class PhysicalSegment extends Segment {
    protected final Artifact node;
    protected Map<State, StandardPlace> statePlaces = new HashMap<>();

    public PhysicalSegment(PetriNet petriNet, DeploymentDiagram deploymentDiagram, SequenceDiagram sequenceDiagram, Artifact node) {
        super(petriNet, deploymentDiagram, sequenceDiagram);
        
        this.node = node;
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
    
    public void transform() {
        DeploymentTarget dt;
        if(node instanceof DeploymentTarget)
            dt = (DeploymentTarget) node;
        else
            dt = node.getParent();

        var nodeName = dt.getNameProperty().getValue();
        dt.getStates().forEach(state -> {
            transformState(nodeName, state);
        });

        dt.getStateTransitions().forEach(transition -> {
            transformTransition(nodeName, transition);
        });
    }
}
