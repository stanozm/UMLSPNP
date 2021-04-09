/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.common.NamedNode;
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
import java.util.Collection;

/**
 *
 * @author 10ondr
 */
public class PhysicalSegment extends Segment {

    public PhysicalSegment(PetriNet petriNet, DeploymentDiagram deploymentDiagram, SequenceDiagram sequenceDiagram) {
        super(petriNet, deploymentDiagram, sequenceDiagram);
    }
    
    private void transformNodes(Collection<NamedNode> nodes) {
        nodes.forEach(node -> {
            if(node instanceof DeploymentTarget) {
                var deploymentTarget = (DeploymentTarget) node;
                transformStates(deploymentTarget, deploymentTarget.getStates());
                transformTransitions(deploymentTarget, deploymentTarget.getStateTransitions());
            }
            else if(node instanceof Artifact) {
            
            }
        });
    }

    private void transformStates(DeploymentTarget deploymentTarget, Collection<State> states) {
        states.forEach(state -> {
            var placeName = SPNPUtils.createPlaceName(deploymentTarget.getNameProperty().getValue(), state.nameProperty().getValue());
            var statePlace = new StandardPlace(SPNPUtils.placeCounter++, placeName);
            if(state.isDefaultProperty().getValue()) {
                statePlace.setNumberOfTokens(1);
            }
            petriNet.addPlace(statePlace);
        });
    }

    private void transformTransitions(DeploymentTarget deploymentTarget, Collection<StateTransition> transitions) {
        transitions.forEach(transition -> {
            var name = SPNPUtils.createTransitionName(deploymentTarget.getNameProperty().getValue(), transition.nameProperty().getValue());
            var rate = transition.rateProperty().getValue();
            var stateTransition = new TimedTransition(SPNPUtils.transitionCounter++, name, new ExponentialTransitionDistribution(rate));
            petriNet.addTransition(stateTransition);
            
            var stateFrom = transition.getStateFrom();
            var placeFrom = SPNPUtils.getPlaceFromNet(petriNet, SPNPUtils.createPlaceName(deploymentTarget.getNameProperty().getValue(), stateFrom.nameProperty().getValue()));
            var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, placeFrom, stateTransition);
            petriNet.addArc(inputArc);

            var stateTo = transition.getStateTo();
            var placeTo = SPNPUtils.getPlaceFromNet(petriNet, SPNPUtils.createPlaceName(deploymentTarget.getNameProperty().getValue(), stateTo.nameProperty().getValue()));
            var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, placeTo, stateTransition);
            petriNet.addArc(outputArc);
        });
    }

    public void transform() {
        var elements = deploymentDiagram.getElementContainer();

        transformNodes(elements.getNodes().values());
    }
}
