/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.Lifeline;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author 10ondr
 */
public class UsageSegment extends Segment {

    public UsageSegment(PetriNet petriNet, DeploymentDiagram deploymentDiagram, SequenceDiagram sequenceDiagram) {
        super(petriNet, deploymentDiagram, sequenceDiagram);
    }

    private Pair<ImmediateTransition, StandardPlace> transformUsageLevelMessage(ImmediateTransition previousTransition, Message message) {
        var messageName = message.nameProperty().getValue();

        var serviceCallName = SPNPUtils.createPlaceName(messageName, "call");
        var serviceCallPlace = new StandardPlace(SPNPUtils.placeCounter++, serviceCallName);
        petriNet.addPlace(serviceCallPlace);
        
        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, serviceCallPlace, previousTransition);
        petriNet.addArc(outputArc);

        // TODO when next service segment is implemented
        var guardName = "guard_" + SPNPUtils.prepareName(messageName, 15) + "_ok";
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, "mark();", Integer.class);

        var serviceCallTransitionName = SPNPUtils.prepareName("TR_" + messageName, 15);
        var serviceCallTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, serviceCallTransitionName, 1, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(serviceCallTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, serviceCallPlace, serviceCallTransition);
        petriNet.addArc(inputArc);

        return new Pair(serviceCallTransition, serviceCallPlace);
    }
    
    private void transformHighestLevelLifeline(Lifeline highestLevelLifeline) {
        var lifelineName = highestLevelLifeline.nameProperty().getValue();
        var sortedMessages = highestLevelLifeline.getSortedMessages();

        // Initial transition
        var initTransitionName = SPNPUtils.createTransitionName(lifelineName, "start");
        var initialTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, initTransitionName, 1, null, new ConstantTransitionProbability(1.0));

        // Individual service calls
        var usagePlaces = new ArrayList<StandardPlace>();
        var previousTransition = initialTransition;
        for(var message : sortedMessages){
            if(highestLevelLifeline == message.getFrom()) { // Only outgoing messages
                var transitionPlacePair = transformUsageLevelMessage(previousTransition, message);
                previousTransition = transitionPlacePair.getKey();
                usagePlaces.add(transitionPlacePair.getValue());
            }
        }
        
        // Initial transition guard
        var startGuardBody = new StringBuilder();
        if(usagePlaces.size() < 1){
            startGuardBody.append("return 1;");
        }
        else{
            startGuardBody.append("return !(");
            for(var place : usagePlaces){
                startGuardBody.append(String.format("mark(\"%s\")", place.getName()));
                if(usagePlaces.indexOf(place) < usagePlaces.size() - 1)
                    startGuardBody.append(" || ");
            }
            startGuardBody.append(");");
        }
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>("guard_" + SPNPUtils.prepareName(lifelineName, 15) + "_usage_start", FunctionType.Guard,
                                                              startGuardBody.toString(), Integer.class);
        initialTransition.setGuardFunction(startGuard);
        petriNet.addTransition(initialTransition);

        // End usage place
        var usageEndPlaceName = SPNPUtils.createPlaceName(lifelineName, "end");
        var usageEndPlace = new StandardPlace(SPNPUtils.placeCounter++, usageEndPlaceName);
        petriNet.addPlace(usageEndPlace);

        FunctionSPNP<Integer> haltingFunction = new FunctionSPNP<>("halting_" + SPNPUtils.prepareName(lifelineName, 15),
                                                                   FunctionType.Halting, String.format("return mark(\"%s\") < 1;", usageEndPlaceName),
                                                                   Integer.class);
        petriNet.addFunction(haltingFunction);
        
        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, usageEndPlace, previousTransition);
        petriNet.addArc(outputArc);
    }

    public void transform() {
        var highestLevelLifeline = sequenceDiagram.getHighestLevelLifeline();
        if(highestLevelLifeline != null){
            transformHighestLevelLifeline(highestLevelLifeline);
        }
    }
}
