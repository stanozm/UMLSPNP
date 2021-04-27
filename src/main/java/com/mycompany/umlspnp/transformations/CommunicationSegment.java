package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.CommunicationLink;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 *  Communication segment of the net which is modeled once for each communication link.
 *
 */
public class CommunicationSegment extends Segment implements ActionServiceSegment {
    protected ControlServiceSegment controlServiceSegment = null;
    protected List<PhysicalSegment> physicalSegments = null;

    protected final ServiceCallTreeNode treeRoot;
    protected final CommunicationLink communicationLink;
    
    protected List<ServiceCall> controlServiceCalls = new ArrayList<>();
    
    protected ImmediateTransition initialTransition = null;
    protected StandardPlace startPlace = null;
    
    protected TimedTransition endTransition = null;
    protected StandardPlace endPlace = null;

    protected ImmediateTransition failHWTransitionFirst = null;
    protected StandardPlace failHWPlaceFirst = null;

    protected ImmediateTransition failHWTransitionSecond = null;
    protected StandardPlace failHWPlaceSecond = null;
    
    protected Map<TimedTransition, StandardPlace> failTypes = new HashMap<>();

    protected ImmediateTransition flushTransition = null;
    protected List<StandardPlace> flushDependentPlaces = new ArrayList<>();

    public CommunicationSegment(PetriNet petriNet,
                                ServiceCallTreeNode treeRoot,
                                CommunicationLink communicationLink) {
        super(petriNet, 1);

        this.treeRoot = treeRoot;
        this.communicationLink = communicationLink;
    }
    
    public CommunicationLink getCommunicationLink() {
        return communicationLink;
    }

    @Override
    public StandardPlace getEndPlace() {
        return endPlace;
    }
    
    @Override
    public Collection<StandardPlace> getFailPlaces() {
        return this.failTypes.values();
    }

    private void resolveControlServiceCalls() {
        controlServiceSegment.getControlServiceCalls().forEach(controlPair -> {
            var controlServiceCall = controlPair.getValue();
            if(controlServiceCall.isCommunicationServiceCall()) {
                var controlCommunicationLink = SPNPUtils.getMessageCommunicationLink(controlServiceCall.getMessage());
                if(communicationLink == controlCommunicationLink) {
                    controlServiceCalls.add(controlServiceCall);
                }
            }
        });
    }

    private void transformInitialTransition(String communicationLinkName) {
        var initialTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "comStart");
        initialTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, initialTransitionName,
                            this.transitionPriority, null, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(initialTransition);
    }

    private String getTopLevelServicePlacesString() {
        var result = new StringBuilder();
        if(controlServiceCalls.size() > 0) {
            controlServiceCalls.forEach(topLevelServiceCall -> {
                result.append(String.format("mark(\"%s\")", topLevelServiceCall.getPlace().getName()));
                if(controlServiceCalls.indexOf(topLevelServiceCall) < controlServiceCalls.size() - 1)
                    result.append(" || ");
            });
        }
        return result.toString();
    }
    
    private void createInitialTransitionGuard(String communicationLinkName) {
        var guardBody = new StringBuilder();
        guardBody.append("return ");

        if(controlServiceCalls.size() > 0)
            guardBody.append(String.format("(%s) && ", getTopLevelServicePlacesString()));

        guardBody.append(String.format("!(mark(\"%s\")", startPlace.getName()));
        guardBody.append(String.format(" || mark(\"%s\")", endPlace.getName()));
        failTypes.values().forEach(failTypePlace -> {
            guardBody.append(String.format(" || mark(\"%s\")", failTypePlace.getName()));
        });
        guardBody.append(String.format(" || mark(\"%s\")", failHWPlaceFirst.getName()));
        guardBody.append(String.format(" || mark(\"%s\"));", failHWPlaceSecond.getName()));

        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_comm_start", SPNPUtils.prepareName(communicationLinkName, 15)));
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
        flushTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, flushTransitionName,
                            this.transitionPriority, null, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(flushTransition);
    }

    private void createFlushTransitionGuard(String communicationLinkName, StandardPlace dependentPlace) {
        if(dependentPlace != null) {
            if(flushDependentPlaces.contains(dependentPlace))
                return; // This place is already contained in the flush transition guard
            flushDependentPlaces.add(dependentPlace);
        }

        var existingGuard = flushTransition.getGuardFunction();
        if(existingGuard != null)
            petriNet.removeFunction(existingGuard);

        var guardBody = new StringBuilder("return ");
        if(dependentPlace == null) {
            guardBody.append("0");
        }
        else{
            flushDependentPlaces.forEach(flushDependentPlace -> {
                if(flushDependentPlaces.indexOf(flushDependentPlace) > 0)
                    guardBody.append(" || ");
                guardBody.append(String.format("mark(\"%s\")", flushDependentPlace.getName()));
            });
        }
        guardBody.append(";");
        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_comm_flush", SPNPUtils.prepareName(communicationLinkName, 15)));
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody.toString(), Integer.class);
        petriNet.addFunction(guard);
        flushTransition.setGuardFunction(guard);
    }

    private String createGuardBody(DeploymentTarget targetNode) {
        var downPlace = SPNPUtils.getDownPlace(physicalSegments, targetNode);
        if(downPlace != null)
            return String.format("return mark(\"%s\");", downPlace.getName());
        return "return 0";
    }

    private Pair<ImmediateTransition, StandardPlace> transformFailHW(DeploymentTarget targetNode, String communicationLinkName) {
        String failHWPlaceName;
        String failHWTransitionName;
        String guardNameFormatString;
        if(targetNode == communicationLink.getFirst()){
            failHWPlaceName = SPNPUtils.createPlaceName(communicationLinkName, "HWf_st");
            failHWTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "HWf_st");
            guardNameFormatString = "guard_%s_HW_fail_first";
        }
        else {
            failHWPlaceName = SPNPUtils.createPlaceName(communicationLinkName, "HWf_nd");
            failHWTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "HWf_nd");
            guardNameFormatString = "guard_%s_HW_fail_second";
        }
        var failHWPlace = new StandardPlace(SPNPUtils.placeCounter++, failHWPlaceName);
        petriNet.addPlace(failHWPlace);

        var guardName = SPNPUtils.createFunctionName(String.format(guardNameFormatString, SPNPUtils.prepareName(communicationLinkName, 15)));
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, createGuardBody(targetNode), Integer.class);

        var failHWTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, failHWTransitionName,
                                this.transitionPriority, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(failHWTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, failHWTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, failHWPlace, failHWTransition);
        petriNet.addArc(outputArc);

        var cardinalityFunctionName = SPNPUtils.createFunctionName(String.format("cardinality_%s_HWf_nd_to_flush", SPNPUtils.prepareName(communicationLinkName, 15)));
        var cardinalityFunctionBody = String.format("return mark(\"%s\");", failHWPlace.getName());
        var cardinalityFunction = new FunctionSPNP<Integer>(cardinalityFunctionName, FunctionType.ArcCardinality, cardinalityFunctionBody, Integer.class);
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, failHWPlace, flushTransition, cardinalityFunction);
        petriNet.addArc(flushInputArc);
        
        return new Pair<>(failHWTransition, failHWPlace);
    }
    
    private FunctionSPNP<Double> createDistributionFunction(String communicationLinkName) {
        var distributionFunctionName = SPNPUtils.createFunctionName(String.format("comm_trans_dist_func__%s", SPNPUtils.prepareName(communicationLinkName, 15)));
        StringBuilder distributionValues = new StringBuilder();

        double transferRate = communicationLink.getLinkType().rateProperty().getValue();

        controlServiceCalls.forEach(controlServiceCall -> {
            var messageSizeObj = controlServiceCall.getMessage().getMessageSize();
            if(messageSizeObj == null)
                return;
            int messageSize = messageSizeObj.messageSizeProperty().getValue();
            double rate;
            if(messageSize <= 0)
                rate = 0;
            else
                rate = 1.0 / (messageSize / transferRate);

            if(distributionValues.length() > 0)
                distributionValues.append(" + ");
            distributionValues.append(String.format("mark(\"%s\") * %f", controlServiceCall.getPlace().getName(), rate));
        });

        if(distributionValues.length() < 1)
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
    
    private void transformEndTransition(String communicationLinkName) {
        var endTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "trEnd");
  
        var distribution = new ExponentialTransitionDistribution(createDistributionFunction(communicationLinkName));
        endTransition = new TimedTransition(SPNPUtils.transitionCounter++, endTransitionName,
                        this.transitionPriority, null, distribution);
        petriNet.addTransition(endTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, endTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, endPlace, endTransition);
        petriNet.addArc(outputArc);

        var cardinalityFunctionName = SPNPUtils.createFunctionName(String.format("cardinality_%s_trEnd_to_flush", SPNPUtils.prepareName(communicationLinkName, 15)));
        var cardinalityFunctionBody = String.format("return mark(\"%s\");", endPlace.getName());
        var cardinalityFunction = new FunctionSPNP<Integer>(cardinalityFunctionName, FunctionType.ArcCardinality, cardinalityFunctionBody, Integer.class);
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, endPlace, flushTransition, cardinalityFunction);
        petriNet.addArc(flushInputArc);
    }
    
    private void transformFailType(String failTypeName, double failTypeRate) {
        var failTypePlaceName = SPNPUtils.createPlaceName(failTypeName, "trFail");
        var failTypePlace = new StandardPlace(SPNPUtils.placeCounter++, failTypePlaceName);
        petriNet.addPlace(failTypePlace);

        var failTypeTransitionName = SPNPUtils.createTransitionName(failTypeName, "trFail");       
        var distribution = new ExponentialTransitionDistribution(failTypeRate);
        var failTypeTransition = new TimedTransition(SPNPUtils.transitionCounter++, failTypeTransitionName,
                                    this.transitionPriority, null, distribution);
        petriNet.addTransition(failTypeTransition);

        failTypes.put(failTypeTransition, failTypePlace);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, failTypeTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, failTypePlace, failTypeTransition);
        petriNet.addArc(outputArc);
        
        var cardinalityFunctionName = SPNPUtils.createFunctionName(String.format("cardinality_%s_trFail_to_flush", SPNPUtils.prepareName(failTypeName, 15)));
        var cardinalityFunctionBody = String.format("return mark(\"%s\");", failTypePlace.getName());
        var cardinalityFunction = new FunctionSPNP<Integer>(cardinalityFunctionName, FunctionType.ArcCardinality, cardinalityFunctionBody, Integer.class);
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, failTypePlace, flushTransition, cardinalityFunction);
        petriNet.addArc(flushInputArc);
    }
    
    private String getCommunicationLinkNameSPNP() {
        return SPNPUtils.prepareName(communicationLink.getLinkType().nameProperty().getValue(), 8);
    }

    public void transformControlServiceSegmentDependencies(ControlServiceSegment controlServiceSegment) {
        this.controlServiceSegment = controlServiceSegment;
        
        String communicationLinkName = getCommunicationLinkNameSPNP();

        this.resolveControlServiceCalls();

        // End transition
        transformEndTransition(communicationLinkName);
    }
    
    public void transformPhysicalSegmentDependencies(List<PhysicalSegment> physicalSegments) {
        this.physicalSegments = physicalSegments;

        String communicationLinkName = getCommunicationLinkNameSPNP();

        // HW fail place and transition
        var firstPair = transformFailHW(this.communicationLink.getFirst(), communicationLinkName);
        failHWPlaceFirst = firstPair.getValue();
        failHWTransitionFirst = firstPair.getKey();
        
        // HW fail place and transition
        var secondPair = transformFailHW(this.communicationLink.getSecond(), communicationLinkName);
        failHWPlaceSecond = secondPair.getValue();
        failHWTransitionSecond = secondPair.getKey();

        // Initial transition guard function
        createInitialTransitionGuard(communicationLinkName);
    }
    
    @Override
    public void transform() {
        String communicationLinkName = getCommunicationLinkNameSPNP();

        // Initial transition
        transformInitialTransition(communicationLinkName);

        // Start place
        transformStartPlace(communicationLinkName);

        // Flush transition
        transformFlushTransition(communicationLinkName);
        
        // Flush guard
        // This guard may be altered later after the loops segments are transformed
        createFlushTransitionGuard(communicationLinkName, null);

        // Fail type places and transitions
        communicationLink.getLinkFailures().forEach(failType -> {
            transformFailType(failType.nameProperty().getValue(), failType.rateProperty().getValue());
        });

        // End place
        transformEndPlace(communicationLinkName);
    }
    
    @Override
    public String toString() {
        var result = new StringBuilder();
        result.append(String.format("[InitialTransition %s]", initialTransition.getName()));
        result.append(String.format(" -> (StartPlace %s)", startPlace.getName()));
        int offset = result.toString().length();
        result.append(String.format(" -> [EndTransition %s]", endTransition.getName()));
        result.append(String.format(" -> (EndPlace %s)", endPlace.getName()));
        result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));

        failTypes.keySet().forEach(failTransition -> {
            var failPlace = failTypes.get(failTransition);
            result.append(System.lineSeparator());
            for(int i = 0; i < offset; i++)
                result.append(" ");
            result.append(String.format(" -> [FailTransition %s]", failTransition.getName()));
            result.append(String.format(" -> (FailPlace %s)", failPlace.getName()));
            result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));
        });

        result.append(System.lineSeparator());
        for(int i = 0; i < offset; i++)
            result.append(" ");
        result.append(String.format(" -> [FailHWTransitionFirst %s]", this.failHWTransitionFirst.getName()));
        result.append(String.format(" -> (FailHWPlaceFirst %s)", failHWPlaceFirst.getName()));
        result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));

        result.append(System.lineSeparator());
        for(int i = 0; i < offset; i++)
            result.append(" ");
        result.append(String.format(" -> [FailHWTransitionSecond %s]", this.failHWTransitionSecond.getName()));
        result.append(String.format(" -> (FailHWPlaceSecond %s)", failHWPlaceSecond.getName()));
        result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));

        result.insert(0, String.format("Communication Segment - communication link \"%s\":%n", communicationLink.getLinkType().nameProperty().getValue()));
        return result.toString();
    }

    @Override
    public void setFlushTransitionGuardDependentPlace(StandardPlace dependentPlace) {
        String communicationLinkName = getCommunicationLinkNameSPNP();
        createFlushTransitionGuard(communicationLinkName, dependentPlace);
    }
}
