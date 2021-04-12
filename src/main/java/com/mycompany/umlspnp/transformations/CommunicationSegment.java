/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.CommunicationLink;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 10ondr
 */
public class CommunicationSegment extends Segment {
    protected final CommunicationLink communicationLink;
    
    protected List<StandardPlace> topLevelServicePlaces = new ArrayList<>();
    
    protected ImmediateTransition initialTransition = null;
    protected StandardPlace startPlace = null;
    
    protected TimedTransition endTransition = null;
    protected StandardPlace endPlace = null;

    protected ImmediateTransition failHWTransition = null;
    protected StandardPlace failHWPlace = null;
    
    protected Map<TimedTransition, StandardPlace> failTypes = new HashMap<>();

    protected ImmediateTransition flushTransition = null;

    public CommunicationSegment(PetriNet petriNet,
                                DeploymentDiagram deploymentDiagram,
                                SequenceDiagram sequenceDiagram,
                                CommunicationLink communicationLink) {
        super(petriNet, deploymentDiagram, sequenceDiagram);

        this.communicationLink = communicationLink;
    }
    
    public CommunicationLink getCommunicationLink() {
        return communicationLink;
    }

    public StandardPlace getEndPlace() {
        return endPlace;
    }
    
    private void transformInitialTransition(String communicationLinkName) {
        var initialTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "comStart");
        initialTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, initialTransitionName, 1, null, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(initialTransition);
    }

    private String getTopLevelServicePlacesString() {
        var result = new StringBuilder();
        if(topLevelServicePlaces.size() > 0) {
            topLevelServicePlaces.forEach(topLevelPlace -> {
                result.append(String.format("mark(\"%s\")", topLevelPlace.getName()));
                if(topLevelServicePlaces.indexOf(topLevelPlace) < topLevelServicePlaces.size() - 1)
                    result.append(" || ");
            });
        }
        return result.toString();
    }
    
    private void createInitialTransitionGuard(String communicationLinkName) {
        var guardBody = new StringBuilder();

        if(topLevelServicePlaces.size() > 0)
            guardBody.append(String.format("return %s;", getTopLevelServicePlacesString()));
        else
            guardBody.append("return 0;");

        var guardName = "guard_" + SPNPUtils.prepareName(communicationLinkName, 15) + "_comm_start";
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody.toString(), Integer.class);

        petriNet.addFunction(guard);
        initialTransition.setGuardFunction(guard);
    }
    
    private void transformStartPlace(String communicationLinkName) {
        var startPlaceName = SPNPUtils.createPlaceName(communicationLinkName, "trStart");
        startPlace = new StandardPlace(SPNPUtils.placeCounter++, startPlaceName);
        petriNet.addPlace(startPlace);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, startPlace, initialTransition);
        petriNet.addArc(outputArc);
    }
    
    private void transformFlushTransition(String communicationLinkName) {
        var flushTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "comFlush");
        flushTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, flushTransitionName, 1, null, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(flushTransition);
    }
    
    private void createFlushTransitionGuard(String communicationLinkName) {
        var guardBody = new StringBuilder();
        guardBody.append("return ");

        if(topLevelServicePlaces.size() > 0) {
            guardBody.append(String.format("(%s) &&", getTopLevelServicePlacesString()));
            guardBody.append(System.lineSeparator());
            guardBody.append("\t");
        }

        guardBody.append(String.format("(mark(\"%s\") || ", endPlace.getName()));
        failTypes.values().forEach(failTypePlace -> {
            guardBody.append(String.format("mark(\"%s\") || ", failTypePlace.getName()));
        });
        guardBody.append(String.format("mark(\"%s\"));", failHWPlace.getName()));

        var guardName = "guard_" + SPNPUtils.prepareName(communicationLinkName, 15) + "_comm_flush";
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody.toString(), Integer.class);

        petriNet.addFunction(guard);
        flushTransition.setGuardFunction(guard);
    }

    private void transformFailHW(String communicationLinkName) {
        var failHWPlaceName = SPNPUtils.createPlaceName(communicationLinkName, "HW_fail");
        failHWPlace = new StandardPlace(SPNPUtils.placeCounter++, failHWPlaceName);
        petriNet.addPlace(failHWPlace);

        // TODO HW fail guard
        var failHWTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "HW_fail");
        var guardBody = new StringBuilder();
        var guardName = "guard_" + SPNPUtils.prepareName(communicationLinkName, 15) + "_HW_fail";
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody.toString(), Integer.class);

        failHWTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, failHWTransitionName, 1, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(failHWTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, failHWTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, failHWPlace, failHWTransition);
        petriNet.addArc(outputArc);

        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, failHWPlace, flushTransition);
        petriNet.addArc(flushInputArc);
    }
    
    private StandardPlace findTopLevelServicePlace(HighLevelSegment segment, Message message) {
        for(var serviceCall : segment.serviceCalls.values()) {
            if(serviceCall.getMessage() == message)
                return serviceCall.getPlace();
        }

        for(var subsegment : segment.getServiceSegments()) {
            if(subsegment instanceof HighLevelSegment)
                return findTopLevelServicePlace((HighLevelSegment) subsegment, message);
        }
        return null;
    }

    private FunctionSPNP<Double> createDistributionFunction(String communicationLinkName, UsageSegment usageSegment) {
        var distributionFunctionName = "comm_trans_dist_func__" + SPNPUtils.prepareName(communicationLinkName, 15);
        var distributionValues = new StringBuilder();

        double transferRate = communicationLink.getLinkType().rateProperty().getValue();

        sequenceDiagram.getSortedMessages().forEach(message -> {
            var messageCommunicationLink = SPNPUtils.getMessageCommunicationLink(message);
            if(communicationLink == messageCommunicationLink) {
                var topLevelServicePlace = findTopLevelServicePlace(usageSegment, message);
                if(topLevelServicePlace != null) {
                    topLevelServicePlaces.add(topLevelServicePlace);

                    var messageSizeObj = message.getMessageSize();
                    if(messageSizeObj == null)
                        return;
                    int messageSize = messageSizeObj.messageSizeProperty().getValue();
                    double rate;
                    if(messageSize <= 0)
                        rate = 0;
                    else
                        rate = 1.0 / (messageSize / transferRate);

                    if(!distributionValues.isEmpty())
                        distributionValues.append(" + ");
                    distributionValues.append(String.format("mark(\"%s\") * %f", topLevelServicePlace.getName(), rate));
                }
            }
        });

        if(distributionValues.isEmpty())
            distributionValues.append("0");
        
        String distributionFunctionBody = String.format("return %s;", distributionValues.toString());
        FunctionSPNP<Double> distributionFunction = new FunctionSPNP<>(distributionFunctionName, FunctionType.Distribution, distributionFunctionBody, Double.class);
        return distributionFunction;
    }
    
    private void transformEndPlace(String communicationLinkName) {
        var endPlaceName = SPNPUtils.createPlaceName(communicationLinkName, "trEnd");
        endPlace = new StandardPlace(SPNPUtils.placeCounter++, endPlaceName);
        petriNet.addPlace(endPlace);
    }
    
    private void transformEndTransition(String communicationLinkName, UsageSegment usageSegment) {
        var endTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "trEnd");
  
        var distribution = new ExponentialTransitionDistribution(createDistributionFunction(communicationLinkName, usageSegment));
        endTransition = new TimedTransition(SPNPUtils.transitionCounter++, endTransitionName, 1, null, distribution);
        petriNet.addTransition(endTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, endTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, endPlace, endTransition);
        petriNet.addArc(outputArc);

        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, endPlace, flushTransition);
        petriNet.addArc(flushInputArc);
    }
    
    private void transformFailType(String failTypeName, double failTypeRate) {
        var failTypePlaceName = SPNPUtils.createPlaceName(failTypeName, "trFail");
        var failTypePlace = new StandardPlace(SPNPUtils.placeCounter++, failTypePlaceName);
        petriNet.addPlace(failTypePlace);

        var failTypeTransitionName = SPNPUtils.createTransitionName(failTypeName, "trFail");       
        var distribution = new ExponentialTransitionDistribution(failTypeRate);
        var failTypeTransition = new TimedTransition(SPNPUtils.transitionCounter++, failTypeTransitionName, 1, null, distribution);
        petriNet.addTransition(failTypeTransition);

        failTypes.put(failTypeTransition, failTypePlace);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, failTypeTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, failTypePlace, failTypeTransition);
        petriNet.addArc(outputArc);
        
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, failTypePlace, flushTransition);
        petriNet.addArc(flushInputArc);
    }
    
    private String getCommunicationLinkNameSPNP() {
        var firstNodeName = communicationLink.getFirst().getNameProperty().getValue();
        var secondNodeName = communicationLink.getSecond().getNameProperty().getValue();
        return SPNPUtils.getCombinedName(firstNodeName, secondNodeName);
    }
    
    public void transform() {
        String communicationLinkName = getCommunicationLinkNameSPNP();

        // Initial transition
        transformInitialTransition(communicationLinkName);

        // Start place
        transformStartPlace(communicationLinkName);

        // Flush transition
        transformFlushTransition(communicationLinkName);
        
        // HW fail place and transition
        transformFailHW(communicationLinkName);

        // End place (Transition needs to be transformed separately due to its cyclic dependency on Service Segment)
        transformEndPlace(communicationLinkName);
        
        // Fail type places and transitions
        communicationLink.getLinkFailures().forEach(failType -> {
            transformFailType(failType.nameProperty().getValue(), failType.rateProperty().getValue());
        });

        // Initial transition guard function
        createInitialTransitionGuard(communicationLinkName);
        
        // Flush transition guard function
        createFlushTransitionGuard(communicationLinkName);
    }
    
    public void transformAfter(UsageSegment usageSegment) {
        String communicationLinkName = getCommunicationLinkNameSPNP();

        // End transition
        transformEndTransition(communicationLinkName, usageSegment);
    }
}
