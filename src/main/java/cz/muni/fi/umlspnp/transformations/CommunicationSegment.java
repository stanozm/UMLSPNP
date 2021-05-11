package cz.muni.fi.umlspnp.transformations;

import cz.muni.fi.umlspnp.models.deploymentdiagram.CommunicationLink;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentTarget;
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

/**
 *  Communication segment of the net which is modeled once for each communication link.
 *
 */
public class CommunicationSegment extends Segment implements ActionServiceSegment {
    private final String commentPrefix;
    protected ControlServiceSegment controlServiceSegment = null;
    protected List<PhysicalSegment> physicalSegments = null;

    protected final ServiceCallTreeNode treeRoot;
    protected final CommunicationLink communicationLink;
    
    protected List<ServiceCall> controlServiceCalls = new ArrayList<>();
    
    protected ImmediateTransition initialTransition = null;
    protected StandardPlace startPlace = null;
    
    protected TimedTransition endTransition = null;
    protected StandardPlace endPlace = null;

    protected ImmediateTransition failHWTransition = null;
    protected StandardPlace failHWPlace = null;
    
    protected Map<TimedTransition, StandardPlace> failTypes = new HashMap<>();

    protected ImmediateTransition flushTransition = null;
    protected List<StandardPlace> flushDependentPlaces = new ArrayList<>();

    public CommunicationSegment(PetriNet petriNet,
                                boolean generateComments,
                                ServiceCallTreeNode treeRoot,
                                CommunicationLink communicationLink) {
        super(petriNet, generateComments);

        this.treeRoot = treeRoot;
        this.communicationLink = communicationLink;
        var linkName = communicationLink.getLinkType().nameProperty().getValue();
        var firstNodeName = communicationLink.getFirst().getNameProperty().getValue();
        var secondNodeName = communicationLink.getSecond().getNameProperty().getValue();
        this.commentPrefix = String.format("Communication segment \"%s\" [%s - %s]", linkName, firstNodeName, secondNodeName);
    }
    
    public CommunicationLink getCommunicationLink() {
        return communicationLink;
    }
    
    public StandardPlace getStartPlace() {
        return startPlace;
    }

    public StandardPlace getFailHWPlace() {
        return failHWPlace;
    }
    
    public ImmediateTransition getFailHWTransition() {
        return failHWTransition;
    }

    public ImmediateTransition getInitialTransition() {
        return initialTransition;
    }

    public TimedTransition getEndTransition() {
        return endTransition;
    }
    
    @Override
    public StandardPlace getEndPlace() {
        return endPlace;
    }
    
    public ImmediateTransition getFlushTransition() {
        return flushTransition;
    }
    
    @Override
    public Collection<StandardPlace> getFailPlaces() {
        var places = new ArrayList<StandardPlace>();
        failTypes.values().forEach(failTypePlace -> {
            places.add(failTypePlace);
        });
        places.add(failHWPlace);
        return places;
    }
    
    public Map<TimedTransition, StandardPlace> getFailTypes() {
        return failTypes;
    } 

    private void resolveControlServiceCalls() {
        controlServiceSegment.getControlServiceCalls().forEach(controlPair -> {
            var controlServiceCall = controlPair.getValue();
            if(controlServiceCall.isCommunicationServiceCall()) {
                var controlCommunicationLink = controlServiceCall.getMessage().getCommunicationLink();
                if(communicationLink == controlCommunicationLink) {
                    controlServiceCalls.add(controlServiceCall);
                }
            }
        });
    }

    private void transformInitialTransition(String communicationLinkName) {
        var initialTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "comStart");
        initialTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, initialTransitionName,
                            SPNPUtils.TR_PRIORTY_DEFAULT_IMMEDIATE, null, new ConstantTransitionProbability(1.0));
        if(generateComments)
            initialTransition.setCommentary(String.format("%s - Initial transition", commentPrefix));
        petriNet.addTransition(initialTransition);
    }

    private String getTopLevelServicePlacesString() {
        var places = new ArrayList<StandardPlace>();
        if(controlServiceCalls.size() > 0) {
            controlServiceCalls.forEach(topLevelServiceCall -> {
                places.add(topLevelServiceCall.getPlace());
            });
        }
        return SPNPUtils.getPlacesString(places);
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
        guardBody.append(String.format(" || mark(\"%s\"));", failHWPlace.getName()));

        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_comm_start", SPNPUtils.prepareName(communicationLinkName, 15)));
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody.toString(), Integer.class);

        petriNet.addFunction(guard);
        initialTransition.setGuardFunction(guard);
    }
    
    private void transformStartPlace(String communicationLinkName) {
        var startPlaceName = SPNPUtils.createPlaceName(communicationLinkName, "trStart");
        startPlace = new StandardPlace(SPNPUtils.placeCounter++, startPlaceName);
        if(generateComments)
            startPlace.setCommentary(String.format("%s - Start place", commentPrefix));
        petriNet.addPlace(startPlace);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, startPlace, initialTransition);
        petriNet.addArc(outputArc);
    }
    
    private void transformFlushTransition(String communicationLinkName) {
        var flushTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "comFlush");
        flushTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, flushTransitionName,
                            SPNPUtils.TR_PRIORTY_ACTION_FLUSH, null, new ConstantTransitionProbability(1.0));
        if(generateComments)
            flushTransition.setCommentary(String.format("%s - Flush transition", commentPrefix));
        petriNet.addTransition(flushTransition);
    }

    private void createFlushTransitionGuard(String communicationLinkName) {
        var existingGuard = flushTransition.getGuardFunction();
        if(existingGuard != null)
            petriNet.removeFunction(existingGuard);

        var guardBody = new StringBuilder("return (");
        guardBody.append(String.format("mark(\"%s\")", endPlace.getName()));
        guardBody.append(String.format(" || mark(\"%s\")", failHWPlace.getName()));
        failTypes.values().forEach(failTypePlace -> {
            guardBody.append(String.format(" || mark(\"%s\")", failTypePlace.getName()));
        });
        guardBody.append(String.format(") &&%n       ("));
        
        if(flushDependentPlaces.size() < 1) {
            guardBody.append("0");
        }
        else{
            flushDependentPlaces.forEach(flushDependentPlace -> {
                if(flushDependentPlaces.indexOf(flushDependentPlace) > 0)
                    guardBody.append(" || ");
                guardBody.append(String.format("mark(\"%s\")", flushDependentPlace.getName()));
            });
        }
        guardBody.append(");");
        var guardName = SPNPUtils.createFunctionName(String.format("guard_%s_comm_flush", SPNPUtils.prepareName(communicationLinkName, 15)));
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, guardBody.toString(), Integer.class);
        petriNet.addFunction(guard);
        flushTransition.setGuardFunction(guard);
    }

    private String createFailHWGuardBody(DeploymentTarget targetNodeFirst, DeploymentTarget targetNodeSecond) {
        var downPlaceFrom = SPNPUtils.getDownPlace(physicalSegments, targetNodeFirst);
        var downPlaceTo = SPNPUtils.getDownPlace(physicalSegments, targetNodeSecond);
        if(downPlaceFrom != null && downPlaceTo != null)
            return String.format("return mark(\"%s\") || mark(\"%s\");",
                                 downPlaceFrom.getName(), downPlaceTo.getName());
        return "return 0";
    }

    private void transformFailHW(DeploymentTarget targetNodeFirst, DeploymentTarget targetNodeSecond, String communicationLinkName) {
        String failHWPlaceName = SPNPUtils.createPlaceName(communicationLinkName, "HWf");
        failHWPlace = new StandardPlace(SPNPUtils.placeCounter++, failHWPlaceName);
        if(generateComments)
            failHWPlace.setCommentary(String.format("%s - Hardware failure place", commentPrefix));
        petriNet.addPlace(failHWPlace);

        String guardNameFormatString = "guard_%s_HW_fail";
        var guardName = SPNPUtils.createFunctionName(String.format(guardNameFormatString, SPNPUtils.prepareName(communicationLinkName, 15)));
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName,
                                                         FunctionType.Guard,
                                                         createFailHWGuardBody(targetNodeFirst, targetNodeSecond),
                                                         Integer.class);

        String failHWTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "HWf");
        failHWTransition = new ImmediateTransition(SPNPUtils.transitionCounter++, failHWTransitionName,
                                SPNPUtils.TR_PRIORTY_DEFAULT_IMMEDIATE, guard, new ConstantTransitionProbability(1.0));
        if(generateComments)
            failHWTransition.setCommentary(String.format("%s - Hardware failure transition", commentPrefix));
        petriNet.addTransition(failHWTransition);

        var inputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, startPlace, failHWTransition);
        petriNet.addArc(inputArc);

        var outputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Output, failHWPlace, failHWTransition);
        petriNet.addArc(outputArc);

        var cardinalityFunctionName = SPNPUtils.createFunctionName(String.format("cardinality_%s_HWf_to_flush", SPNPUtils.prepareName(communicationLinkName, 15)));
        var cardinalityFunctionBody = String.format("return mark(\"%s\");", failHWPlace.getName());
        var cardinalityFunction = new FunctionSPNP<Integer>(cardinalityFunctionName, FunctionType.ArcCardinality, cardinalityFunctionBody, Integer.class);
        var flushInputArc = new StandardArc(SPNPUtils.arcCounter++, ArcDirection.Input, failHWPlace, flushTransition, cardinalityFunction);
        petriNet.addArc(flushInputArc);
    }
    
    private FunctionSPNP<Double> createDistributionFunction(String communicationLinkName) {
        var distributionFunctionName = SPNPUtils.createFunctionName(String.format("comm_trans_dist_func__%s", SPNPUtils.prepareName(communicationLinkName, 15)));
        StringBuilder distributionValues = new StringBuilder();

        double transferRate = communicationLink.getLinkType().rateProperty().getValue();

        controlServiceCalls.forEach(controlServiceCall -> {
            var messageSizeObj = controlServiceCall.getMessage().getMessageSize();
            double messageSize = 0;
            if(messageSizeObj != null)
                messageSize = messageSizeObj.messageSizeProperty().getValue();

            double transmitTime = transferRate > 0 ? messageSize / transferRate : 0;
            double rate = transmitTime > 0 ? 1.0 / transmitTime : 1.0;

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
        if(generateComments)
            endPlace.setCommentary(String.format("%s - End place", commentPrefix));
        petriNet.addPlace(endPlace);
    }
    
    private void transformEndTransition(String communicationLinkName) {
        var endTransitionName = SPNPUtils.createTransitionName(communicationLinkName, "trEnd");
  
        var distribution = new ExponentialTransitionDistribution(createDistributionFunction(communicationLinkName));
        endTransition = new TimedTransition(SPNPUtils.transitionCounter++, endTransitionName,
                        SPNPUtils.TR_PRIORTY_DEFAULT, null, distribution);
        if(generateComments)
            endTransition.setCommentary(String.format("%s - End transition", commentPrefix));
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
        if(generateComments)
            failTypePlace.setCommentary(String.format("%s - Failure place (\"%s\")", commentPrefix, failTypeName));
        petriNet.addPlace(failTypePlace);

        var failTypeTransitionName = SPNPUtils.createTransitionName(failTypeName, "trFail");       
        var distribution = new ExponentialTransitionDistribution(failTypeRate);
        var failTypeTransition = new TimedTransition(SPNPUtils.transitionCounter++, failTypeTransitionName,
                                    SPNPUtils.TR_PRIORTY_DEFAULT, null, distribution);
        if(generateComments)
            failTypeTransition.setCommentary(String.format("%s - Failure transition (\"%s\")", commentPrefix, failTypeName));
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
        
        // Initial transition guard function
        createInitialTransitionGuard(communicationLinkName);
    }
    
    public void transformPhysicalSegmentDependencies(List<PhysicalSegment> physicalSegments) {
        this.physicalSegments = physicalSegments;

        String communicationLinkName = getCommunicationLinkNameSPNP();

        // HW fail place and transition
        transformFailHW(communicationLink.getFirst(), communicationLink.getSecond(), communicationLinkName);

        // Flush guard
        // This guard may be altered later after the loops segments are transformed
        createFlushTransitionGuard(communicationLinkName);
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
        result.append(String.format(" -> [FailHWTransition %s]", failHWTransition.getName()));
        result.append(String.format(" -> (FailHWPlace %s)", failHWPlace.getName()));
        result.append(String.format(" -> [FlushTransition %s]", flushTransition.getName()));

        result.insert(0, String.format("Communication Segment - communication link \"%s\":%n", communicationLink.getLinkType().nameProperty().getValue()));
        return result.toString();
    }

    @Override
    public void setFlushTransitionGuardDependentPlace(StandardPlace dependentPlace) {
        if(dependentPlace != null) {
            if(flushDependentPlaces.contains(dependentPlace))
                return; // This place is already contained in the flush transition guard
            flushDependentPlaces.add(dependentPlace);
            createFlushTransitionGuard(getCommunicationLinkNameSPNP());
        }
    }
}
