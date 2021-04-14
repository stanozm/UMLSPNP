/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import java.util.List;

/**
 *
 * @author 10ondr
 */
public class ServiceIntermediateSegment extends HighLevelSegment implements ServiceSegment {
    private final ServiceCallNode serviceCallNode;
    private final ServiceCall serviceCall;
    
    public ServiceIntermediateSegment(PetriNet petriNet,
                                      List<PhysicalSegment> physicalSegments,
                                      List<CommunicationSegment> communicationSegments,
                                      ServiceCallNode serviceCallNode,
                                      ServiceCall serviceCall) {
        super(petriNet, physicalSegments, communicationSegments, serviceCallNode);

        this.serviceCallNode = serviceCallNode;
        this.serviceCall = serviceCall;
    }

    private void transformInitialTransitionGuard(String lifelineName) {
        var startGuardBody = new StringBuilder();

        var tokenStrings = getTokenStrings();
        var serviceCallTokenString = String.format("mark(\"%s\")", serviceCall.getPlace().getName());

        if(tokenStrings.size() < 1){
            startGuardBody.append(String.format("return %s", serviceCallTokenString));
        }
        else{
            startGuardBody.append(String.format("return %s && !(", serviceCallTokenString));
            tokenStrings.forEach(tokenString -> {
                startGuardBody.append(tokenString);
                if(tokenStrings.indexOf(tokenString) < tokenStrings.size() - 1)
                    startGuardBody.append(" || ");
            });
            startGuardBody.append(");");
        }
        String guardName = SPNPUtils.createFunctionName(String.format("guard_%s_usage_start", SPNPUtils.prepareName(lifelineName, 15)));
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>(guardName, FunctionType.Guard, startGuardBody.toString(), Integer.class);
        petriNet.addFunction(startGuard);
        initialTransition.setGuardFunction(startGuard);
    }

    @Override
    public void transform() {
        super.transform();
        
        var lifelineName = serviceCallNode.getArtifact().getNameProperty().getValue();
        
        transformInitialTransitionGuard(lifelineName);
    }

    @Override
    public ServiceCall getServiceCall() {
        return serviceCall;
    }
    
    @Override
    public StandardPlace getEndPlace() {
        return endPlace;
    }
}
