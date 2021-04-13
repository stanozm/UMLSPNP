/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import java.util.List;

/**
 *
 * @author 10ondr
 */
public class UsageSegment extends HighLevelSegment {
    private final ServiceCallNode treeRoot;
    
    public UsageSegment(PetriNet petriNet,
                        ServiceCallNode treeRoot,
                        List<CommunicationSegment> communicationSegments) {
        super(petriNet, communicationSegments, treeRoot);
        
        this.treeRoot = treeRoot;
    }

    private void transformInitialTransitionGuard(String lifelineName) {
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
        String guardName = SPNPUtils.createFunctionName(String.format("guard_%s_usage_start", SPNPUtils.prepareName(lifelineName, 15)));
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>(guardName, FunctionType.Guard,
                                                              startGuardBody.toString(), Integer.class);
        petriNet.addFunction(startGuard);
        initialTransition.setGuardFunction(startGuard);
    }

    private void transformEndPlaceHaltingFunction(String lifelineName) {
        String functionName = SPNPUtils.createFunctionName(String.format("halting_%s", SPNPUtils.prepareName(lifelineName, 15)));
        FunctionSPNP<Integer> haltingFunction = new FunctionSPNP<>(functionName,
                                                                   FunctionType.Halting, String.format("return !mark(\"%s\");", endPlace.getName()),
                                                                   Integer.class);
        petriNet.addFunction(haltingFunction);
    }
    
    @Override
    public void transform() {
        super.transform();

        var lifelineName = treeRoot.getArtifact().getNameProperty().getValue();
        
        transformInitialTransitionGuard(lifelineName);
        transformEndPlaceHaltingFunction(lifelineName);
    }
}
