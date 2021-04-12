/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.Activation;
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
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author 10ondr
 */
public class HighLevelSegment extends Segment {
    protected final Activation activation;
    
    protected final List<Pair<ImmediateTransition, ServiceCall>> pairs = new ArrayList<>();
    protected ImmediateTransition initialTransition = null;
    protected StandardPlace endPlace = null;
    
    protected final List<ServiceSegment> serviceSegments = new ArrayList<>();
    
    public HighLevelSegment(PetriNet petriNet, DeploymentDiagram deploymentDiagram, SequenceDiagram sequenceDiagram, Activation activation) {
        super(petriNet, deploymentDiagram, sequenceDiagram);
        
        this.activation = activation;
    }

    public ServiceSegment transformServiceCall(ServiceCall serviceCall) {
        ServiceSegment serviceSegment = null;
        if(serviceCall.getMessage().isLeafMessage()) {
            serviceSegment = new ServiceLeafSegment(petriNet, deploymentDiagram, sequenceDiagram, serviceCall);
            serviceSegment.transform();
        }
        else{
            serviceSegment = new ServiceIntermediateSegment(petriNet, deploymentDiagram, sequenceDiagram, serviceCall);
            serviceSegment.transform();
        }
        return serviceSegment;
    }

    private Pair<ImmediateTransition, ServiceCall> transformHighLevelMessage(ImmediateTransition previousTransition, Message message) {
        var messageName = message.nameProperty().getValue();

        var serviceCallName = SPNPUtils.createPlaceName(messageName, "call");
        var serviceCallPlace = new StandardPlace(SPNPUtils.placeCounter++, serviceCallName);
        petriNet.addPlace(serviceCallPlace);
        
        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, serviceCallPlace, previousTransition);
        petriNet.addArc(outputArc);

        var serviceCall = new ServiceCall(message, serviceCallPlace);
        var serviceSegment = transformServiceCall(serviceCall);
        serviceSegments.add(serviceSegment);

        var guardName = "guard_" + SPNPUtils.prepareName(messageName, 15) + "_ok";
        var guardBody = String.format("return mark(\"%s\");", serviceSegment.getEndPlace().getName());
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody, Integer.class);

        var serviceCallTransitionName = SPNPUtils.prepareName("TR_" + messageName, 15);
        var serviceCallTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, serviceCallTransitionName, 1, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(serviceCallTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, serviceCallPlace, serviceCallTransition);
        petriNet.addArc(inputArc);

        return new Pair(serviceCallTransition, serviceCall);
    }
    
    private void transformInitialTransition(String lifelineName) {
        var initTransitionName = SPNPUtils.createTransitionName(lifelineName, "start");
        initialTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, initTransitionName, 1, null, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(initialTransition);
    }
    
    private void transformEnd(String lifelineName, ImmediateTransition previousTransition) {
        var endPlaceName = SPNPUtils.createPlaceName(lifelineName, "end");
        endPlace = new StandardPlace(SPNPUtils.placeCounter++, endPlaceName);
        petriNet.addPlace(endPlace);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, endPlace, previousTransition);
        petriNet.addArc(outputArc);
    }

    public List<StandardPlace> getPlaces() {
        List<StandardPlace> places = new ArrayList<>();
        pairs.forEach(pair -> {
            places.add(pair.getValue().getPlace());
        });
        return places;
    }
    
    public List<String> getTokenStrings() {
        List<String> tokenStrings = new ArrayList<>();
        var places = getPlaces();

        places.forEach(place -> {
            tokenStrings.add(String.format("mark(\"%s\")", place.getName()));
        });
        return tokenStrings;
    }

    public Activation getActivation() {
        return activation;
    }
    
    public List<Pair<ImmediateTransition, ServiceCall>> getPairs() {
        return pairs;
    }
    
    public ImmediateTransition getInitialTransition() {
        return initialTransition;
    }
    
    public StandardPlace getEndPlace() {
        return endPlace;
    }
    
    public List<ServiceSegment> getServiceSegments() {
        return serviceSegments;
    }

    public void transform() {
        var lifeline = activation.getLifeline();
        var lifelineName = lifeline.nameProperty().getValue();
        var sortedMessages = activation.getSortedMessages();
        
        // Initial transition
        transformInitialTransition(lifelineName);

        // Individual service calls
        var previousTransition = initialTransition;
        for(var message : sortedMessages){
            if(lifeline == message.getFrom().getLifeline()) { // Only outgoing messages
                var transitionPlacePair = transformHighLevelMessage(previousTransition, message);
                pairs.add(transitionPlacePair);
                previousTransition = transitionPlacePair.getKey();
            }
        }

        // End place and transition
        transformEnd(lifelineName, previousTransition);
    }
}
