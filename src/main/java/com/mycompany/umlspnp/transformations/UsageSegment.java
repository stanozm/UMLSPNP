/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.Activation;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import java.util.List;

/**
 *
 * @author 10ondr
 */
public class UsageSegment extends HighLevelSegment {
    public UsageSegment(PetriNet petriNet, DeploymentDiagram deploymentDiagram, SequenceDiagram sequenceDiagram, List<CommunicationSegment> communicationSegments) {
        super(petriNet, deploymentDiagram, sequenceDiagram, communicationSegments, getHighestActivation(sequenceDiagram));
    }

    private static Activation getHighestActivation(SequenceDiagram sequenceDiagram) {
        var highestLevelLifeline = sequenceDiagram.getHighestLevelLifeline();
        if(highestLevelLifeline != null){
            var sortedActivations = highestLevelLifeline.getSortedActivations();
            if(sortedActivations.size() > 0) {
                return sortedActivations.get(0);
            }
        }
        return null;
    }

    private void transformInitialTransitionGuard() {
        var lifelineName = activation.getLifeline().nameProperty().getValue();
        var startGuardBody = new StringBuilder();

        var tokenStrings = getTokenStrings();

        if(tokenStrings.size() < 1){
            startGuardBody.append("return 1;");
        }
        else{
            startGuardBody.append("return !(");
            tokenStrings.forEach(tokenString -> {
                startGuardBody.append(tokenString);
                if(tokenStrings.indexOf(tokenString) < tokenStrings.size() - 1)
                    startGuardBody.append(" || ");
            });
            startGuardBody.append(");");
        }
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>("guard_" + SPNPUtils.prepareName(lifelineName, 15) + "_usage_start", FunctionType.Guard,
                                                              startGuardBody.toString(), Integer.class);
        petriNet.addFunction(startGuard);
        initialTransition.setGuardFunction(startGuard);
    }

    private void transformEndPlaceHaltingFunction() {
        var lifelineName = activation.getLifeline().nameProperty().getValue();
        FunctionSPNP<Integer> haltingFunction = new FunctionSPNP<>("halting_" + SPNPUtils.prepareName(lifelineName, 15),
                                                                   FunctionType.Halting, String.format("return !mark(\"%s\");", endPlace.getName()),
                                                                   Integer.class);
        petriNet.addFunction(haltingFunction);
    }
    
    @Override
    public void transform() {
        super.transform();

        transformInitialTransitionGuard();
        transformEndPlaceHaltingFunction();
    }
}
