/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.common.NamedNode;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import com.mycompany.umlspnp.models.deploymentdiagram.State;
import com.mycompany.umlspnp.models.deploymentdiagram.StateTransition;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.arcs.ArcDirection;
import cz.muni.fi.spnp.core.models.arcs.StandardArc;
import cz.muni.fi.spnp.core.models.places.Place;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import cz.muni.fi.spnp.core.models.transitions.TimedTransition;
import cz.muni.fi.spnp.core.transformators.spnp.*;
import cz.muni.fi.spnp.core.transformators.spnp.code.SPNPCode;
import cz.muni.fi.spnp.core.transformators.spnp.distributions.ExponentialTransitionDistribution;
import cz.muni.fi.spnp.core.transformators.spnp.options.Option;
import cz.muni.fi.spnp.core.transformators.spnp.options.SPNPOptions;
import cz.muni.fi.spnp.core.transformators.spnp.parameters.InputParameter;
import java.util.Collection;
import java.util.HashSet;
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
    
    private String prepareName(String name) {
        var result = name.replaceAll("\\s+", "");
        if(result.length() > 8) {
            result = result.substring(0, 8);
        }
        return result;
    }
    
    private String createPlaceName(String nodeName, String placeName) {
        return "PL_" + prepareName(nodeName) + "_" + prepareName(placeName);
    }
    
    private String createTransitionName(String nodeName, String transitionName) {
        return "TR_" + prepareName(nodeName) + "_" + prepareName(transitionName);
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

    public void transform() {
        var deploymentDiagram = model.getDeploymentDiagram();
        var elements = deploymentDiagram.getElementContainer();
        transformNodes(elements.getNodes().values());
    }

    public String getOutput(){
        return transformator.transform(petriNet);
    }
}
