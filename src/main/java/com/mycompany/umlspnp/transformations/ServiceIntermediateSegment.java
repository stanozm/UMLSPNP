/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
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
    private final ServiceCall serviceCall;
    
    public ServiceIntermediateSegment(PetriNet petriNet,
                                      DeploymentDiagram deploymentDiagram,
                                      SequenceDiagram sequenceDiagram,
                                      List<CommunicationSegment> communicationSegments,
                                      ServiceCall serviceCall) {
        super(petriNet, deploymentDiagram, sequenceDiagram, communicationSegments, serviceCall.getMessage().getTo());

        this.serviceCall = serviceCall;
    }

    private void transformInitialTransitionGuard() {
        var lifelineName = activation.getLifeline().nameProperty().getValue();
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
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>("guard_" + SPNPUtils.prepareName(lifelineName, 15) + "_usage_start", FunctionType.Guard,
                                                              startGuardBody.toString(), Integer.class);
        petriNet.addFunction(startGuard);
        initialTransition.setGuardFunction(startGuard);
    }

    @Override
    public void transform() {
        super.transform();

        transformInitialTransitionGuard();
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
