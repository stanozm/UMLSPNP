/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author 10ondr
 */
public class HighLevelSegment extends Segment {
    private final List<PhysicalSegment> physicalSegments;
    private final List<CommunicationSegment> communicationSegments;
    
    protected final ServiceCallNode treeNode;
    
    protected final Map<ImmediateTransition, ServiceCall> serviceCalls = new HashMap<>();
    protected ImmediateTransition initialTransition = null;
    protected StandardPlace endPlace = null;
    
    protected final List<ServiceSegment> serviceSegments = new ArrayList<>();
    
    public HighLevelSegment(PetriNet petriNet,
                            List<PhysicalSegment> physicalSegments,
                            List<CommunicationSegment> communicationSegments,
                            ServiceCallNode treeNode) {
        super(petriNet);

        this.physicalSegments = physicalSegments;
        this.communicationSegments = communicationSegments;
        this.treeNode = treeNode;
    }

    public ServiceSegment transformServiceCall(ServiceCall serviceCall, ServiceCallNode serviceCallNode) {
        ServiceSegment serviceSegment;

        if(serviceCallNode.isLeaf()) {
            serviceSegment = new ServiceLeafSegment(petriNet, physicalSegments, communicationSegments, serviceCallNode, serviceCall);
            serviceSegment.transform();
        }
        else{
            serviceSegment = new ServiceIntermediateSegment(petriNet, physicalSegments, communicationSegments, serviceCallNode, serviceCall);
            serviceSegment.transform();
        }
        return serviceSegment;
    }

    private Pair<ImmediateTransition, ServiceCall> transformHighLevelMessage(ImmediateTransition previousTransition, ServiceCallNode serviceCallNode) {
        var message = serviceCallNode.getMessage();
        var messageName = message.nameProperty().getValue();

        var serviceCallName = SPNPUtils.createPlaceName(messageName, "call");
        var serviceCallPlace = new StandardPlace(SPNPUtils.placeCounter++, serviceCallName);
        petriNet.addPlace(serviceCallPlace);
        
        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, serviceCallPlace, previousTransition);
        petriNet.addArc(outputArc);

        var serviceCall = new ServiceCall(message, serviceCallPlace);
        var serviceSegment = transformServiceCall(serviceCall, serviceCallNode);
        serviceSegments.add(serviceSegment);

        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_ok", SPNPUtils.prepareName(messageName, 15)));
        var guardBody = String.format("return mark(\"%s\");", serviceSegment.getEndPlace().getName());
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody, Integer.class);

        var serviceCallTransitionName = SPNPUtils.createTransitionName(messageName);
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
        serviceCalls.values().forEach(serviceCall -> {
            places.add(serviceCall.getPlace());
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
    
    public Map<ImmediateTransition, ServiceCall> getServiceCalls() {
        return serviceCalls;
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
        var lifelineName = treeNode.getArtifact().getNameProperty().getValue();

        // Initial transition
        transformInitialTransition(lifelineName);

        // Individual service calls
        var previousTransition = initialTransition;
        for(var serviceCallNode : treeNode.getChildren()) {
            var transitionPlacePair = transformHighLevelMessage(previousTransition, serviceCallNode);
            serviceCalls.put(transitionPlacePair.getKey(), transitionPlacePair.getValue());
            previousTransition = transitionPlacePair.getKey();
        }

        // End place and transition
        transformEnd(lifelineName, previousTransition);
    }
}
