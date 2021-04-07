/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;
import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.common.NamedNode;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import com.mycompany.umlspnp.models.deploymentdiagram.State;
import com.mycompany.umlspnp.models.deploymentdiagram.StateTransition;
import com.mycompany.umlspnp.models.sequencediagram.Lifeline;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.functions.FunctionType;
import cz.muni.fi.spnp.core.models.places.Place;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.ImmediateTransition;
import cz.muni.fi.spnp.core.models.transitions.TimedTransition;
import cz.muni.fi.spnp.core.models.transitions.probabilities.ConstantTransitionProbability;
import cz.muni.fi.spnp.core.transformators.spnp.*;
import cz.muni.fi.spnp.core.transformators.spnp.code.FunctionSPNP;
import cz.muni.fi.spnp.core.transformators.spnp.code.SPNPCode;
import cz.muni.fi.spnp.core.transformators.spnp.distributions.ExponentialTransitionDistribution;
import cz.muni.fi.spnp.core.transformators.spnp.options.Option;
import cz.muni.fi.spnp.core.transformators.spnp.options.SPNPOptions;
import cz.muni.fi.spnp.core.transformators.spnp.parameters.InputParameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javafx.util.Pair;
/**
 *
 * @author 10ondr
 */
public class Transformator {
    MainModel model = null;

    private final SPNPTransformator transformator;
    private final SPNPCode code;
    private final SPNPOptions options;
    private final PetriNet petriNet;
    
    private int placeCounter = 0;
    private int transitionCounter = 0;
    private int arcCounter = 0;

    public Transformator(MainModel mainModel){
        this.model = mainModel;

        this.code = createCode();
        this.options = createOptions();
        this.petriNet = createPetriNet();
        this.transformator = new SPNPTransformator(code, options);
    }

    protected final SPNPCode createCode() {
        return new SPNPCode();
    }

    protected final SPNPOptions createOptions() {
        var parametersSet = new HashSet<InputParameter>();
//        parametersSet.add(new IntegerInputParameter("param1", "prompt1"));
//        parametersSet.add(new IntegerInputParameter("param2", "prompt2"));
//        parametersSet.add(new DoubleInputParameter("param3", "prompt3"));
//        parametersSet.add(new DoubleInputParameter("param4", "prompt4"));

        var optionsSet = new HashSet<Option>();
//        optionsSet.add(new IntegerTypeOption(OptionKey.IOP_OK_VANLOOP, 5));
//        optionsSet.add(new IntegerTypeOption(OptionKey.IOP_ELIMINATION, 125));
//        optionsSet.add(new DoubleTypeOption(OptionKey.FOP_SIM_ERROR, 55.46));
//        optionsSet.add(new IntegerTypeOption(OptionKey.IOP_SSMETHOD, 666666666));
//        optionsSet.add(new ConstantTypeOption(OptionKey.FOP_ABS_RET_M0, ConstantValue.VAL_SPLIT));
//        optionsSet.add(new ConstantTypeOption(OptionKey.FOP_SSPRES, ConstantValue.VAL_REPL));

        return new SPNPOptions(parametersSet, optionsSet);
    }

    protected final PetriNet createPetriNet() {
        var net = new PetriNet();
                
//        var stdPlace1 = new StandardPlace(0, "StdPlace1");
//        var stdPlace2 = new StandardPlace(1, "StdPlace2");
//        var stdPlace3 = new StandardPlace(2, "StdPlace3");
//        var stdPlace4 = new StandardPlace(3, "StdPlace4");
//        var stdPlace5 = new StandardPlace(4, "StdPlace5");
//        
//        stdPlace3.setNumberOfTokens(5);
//        stdPlace5.setNumberOfTokens(99999999);
//        net.addPlace(stdPlace1);
//        net.addPlace(stdPlace2);
//        net.addPlace(stdPlace3);
//        net.addPlace(stdPlace4);
//        net.addPlace(stdPlace5);
//        
//        var fluidPlace1 = new FluidPlace(0, "FluidPlace1");
//        var fluidPlace2 = new FluidPlace(1, "FluidPlace2");
//        var fluidPlace3 = new FluidPlace(2, "FluidPlace3");
//        var fluidPlace4 = new FluidPlace(3, "FluidPlace4");
//        var fluidPlace5 = new FluidPlace(4, "FluidPlace5");
//        
//        fluidPlace1.setInitialValue(0.123);
//        fluidPlace4.setInitialValue(9849.2615);
//        fluidPlace5.setInitialValue(546.00001);
//
//        fluidPlace3.setBoundValue(0.5654);
//        fluidPlace4.setBoundValue(165.5654);
//        fluidPlace5.setBoundValue(99999.00001);
//        
//        fluidPlace4.addBreakValue(123.01);
//        fluidPlace4.addBreakValue(456.02);
//        fluidPlace4.addBreakValue(789.03);
//        fluidPlace4.addBreakValue(999.99);
//        fluidPlace4.addBreakValue(987.789);
//        fluidPlace5.addBreakValue(1.1);
//        fluidPlace5.addBreakValue(7.7);
//
//        net.addPlace(fluidPlace1);
//        net.addPlace(fluidPlace2);
//        net.addPlace(fluidPlace3);
//        net.addPlace(fluidPlace4);
//        net.addPlace(fluidPlace5);
//
//        var constantTransitionProbability = new ConstantTransitionProbability(0.75);
//        FunctionSPNP<Integer> guard = new FunctionSPNP<>("ImmediateGuard", FunctionType.Guard, "return 4;", Integer.class);
//        var immediateTransition1 = new ImmediateTransition(1, "ImmediateTransition1", 1, guard, constantTransitionProbability);
//        net.addTransition(immediateTransition1);
//
//        FunctionSPNP<Integer> guard2 = new FunctionSPNP<>("ImmediateGuard2", FunctionType.Guard,
//                                                            String.format("int a = 4;%nint b = 6;%nreturn a + b;"), Integer.class);
//        var immediateTransition2 = new ImmediateTransition(2, "ImmediateTransition2", 1, guard2, constantTransitionProbability);
//        net.addTransition(immediateTransition2);
//
//        net.addArc(new StandardArc(1, ArcDirection.Input, stdPlace1, immediateTransition1));

        return net;
    }

    private Place getPlaceFromNet(String placeName) {
        for(Place place : petriNet.getPlaces()) {
            if(place.getName().equals(placeName))
                return place;
        }
        return null;
    }
    
    private String prepareName(String name, int maxLength) {
        var result = name.replaceAll("\\s+", "").replaceAll(Utils.SPNP_NAME_RESTRICTION_REPLACE_REGEX, "");
        if(result.length() > maxLength) {
            result = result.substring(0, maxLength);
        }
        return result;
    }
    
    private String createPlaceName(String nodeName, String placeName) {
        return "PL_" + prepareName(nodeName, 8) + "_" + prepareName(placeName, 8);
    }
    
    private String createTransitionName(String nodeName, String transitionName) {
        return "TR_" + prepareName(nodeName, 8) + "_" + prepareName(transitionName, 8);
    }
    
    private void transformNodes(Collection<NamedNode> nodes) {
        nodes.forEach(node -> {
            if(node instanceof DeploymentTarget) {
                var deploymentTarget = (DeploymentTarget) node;
                transformStates(deploymentTarget, deploymentTarget.getStates());
                transformTransitions(deploymentTarget, deploymentTarget.getStateTransitions());
            }
            else if(node instanceof Artifact) {
            
            }
        });
    }

    private void transformStates(DeploymentTarget deploymentTarget, Collection<State> states) {
        states.forEach(state -> {
            var placeName = createPlaceName(deploymentTarget.getNameProperty().getValue(), state.nameProperty().getValue());
            var statePlace = new StandardPlace(placeCounter++, placeName);
            if(state.isDefaultProperty().getValue()) {
                statePlace.setNumberOfTokens(1);
            }
            petriNet.addPlace(statePlace);
        });
    }

    private void transformTransitions(DeploymentTarget deploymentTarget, Collection<StateTransition> transitions) {
        transitions.forEach(transition -> {
            var name = createTransitionName(deploymentTarget.getNameProperty().getValue(), transition.nameProperty().getValue());
            var rate = transition.rateProperty().getValue();
            var stateTransition = new TimedTransition(transitionCounter++, name, new ExponentialTransitionDistribution(rate));
            petriNet.addTransition(stateTransition);
            
            var stateFrom = transition.getStateFrom();
            var placeFrom = getPlaceFromNet(createPlaceName(deploymentTarget.getNameProperty().getValue(), stateFrom.nameProperty().getValue()));
            var inputArc = new StandardArc(arcCounter++, ArcDirection.Input, placeFrom, stateTransition);
            petriNet.addArc(inputArc);

            var stateTo = transition.getStateTo();
            var placeTo = getPlaceFromNet(createPlaceName(deploymentTarget.getNameProperty().getValue(), stateTo.nameProperty().getValue()));
            var outputArc = new StandardArc(arcCounter++, ArcDirection.Output, placeTo, stateTransition);
            petriNet.addArc(outputArc);
        });
    }

    private Pair<ImmediateTransition, StandardPlace> transformUsageLevelMessage(ImmediateTransition previousTransition, Message message) {
        var messageName = message.nameProperty().getValue();

        var serviceCallName = createPlaceName(messageName, "call");
        var serviceCallPlace = new StandardPlace(placeCounter++, serviceCallName);
        petriNet.addPlace(serviceCallPlace);
        
        var outputArc = new StandardArc(arcCounter++, ArcDirection.Output, serviceCallPlace, previousTransition);
        petriNet.addArc(outputArc);

        // TODO when next service segment is implemented
        var guardName = "guard_" + prepareName(messageName, 15) + "_ok";
        FunctionSPNP<Integer> guard = new FunctionSPNP<>(guardName, FunctionType.Guard, "mark();", Integer.class);

        var serviceCallTransitionName = prepareName("TR_" + messageName, 15);
        var serviceCallTransition = new ImmediateTransition(transitionCounter++, serviceCallTransitionName, 1, guard, new ConstantTransitionProbability(1.0));
        petriNet.addTransition(serviceCallTransition);

        var inputArc = new StandardArc(arcCounter++, ArcDirection.Input, serviceCallPlace, serviceCallTransition);
        petriNet.addArc(inputArc);

        return new Pair(serviceCallTransition, serviceCallPlace);
    }
    
    private void transformHighestLevelLifeline(Lifeline highestLevelLifeline) {
        var lifelineName = highestLevelLifeline.nameProperty().getValue();
        var sortedMessages = highestLevelLifeline.getSortedMessages();

        // Initial transition
        var initTransitionName = createTransitionName(lifelineName, "start");
        var initialTransition = new ImmediateTransition(transitionCounter++, initTransitionName, 1, null, new ConstantTransitionProbability(1.0));

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
        FunctionSPNP<Integer> startGuard = new FunctionSPNP<>("guard_" + prepareName(lifelineName, 15) + "_usage_start", FunctionType.Guard,
                                                              startGuardBody.toString(), Integer.class);
        initialTransition.setGuardFunction(startGuard);
        petriNet.addTransition(initialTransition);

        // End usage place
        var usageEndPlaceName = createPlaceName(lifelineName, "end");
        var usageEndPlace = new StandardPlace(placeCounter++, usageEndPlaceName);
        petriNet.addPlace(usageEndPlace);

        FunctionSPNP<Integer> haltingFunction = new FunctionSPNP<>("halting_" + prepareName(lifelineName, 15),
                                                                   FunctionType.Halting, String.format("return mark(\"%s\") < 1;", usageEndPlaceName),
                                                                   Integer.class);
        petriNet.addFunction(haltingFunction);
        
        var outputArc = new StandardArc(arcCounter++, ArcDirection.Output, usageEndPlace, previousTransition);
        petriNet.addArc(outputArc);
    }

    public void transform() {
        var deploymentDiagram = model.getDeploymentDiagram();
        var sequenceDiagram = model.getSequenceDiagram();
 
        var elements = deploymentDiagram.getElementContainer();

        // Physical segment
        transformNodes(elements.getNodes().values());

        // High-level usage segment
        var highestLevelLifeline = sequenceDiagram.getHighestLevelLifeline();
        if(highestLevelLifeline != null){
            transformHighestLevelLifeline(highestLevelLifeline);
        }
    }

    public String getOutput(){
        return transformator.transform(petriNet);
    }
}
