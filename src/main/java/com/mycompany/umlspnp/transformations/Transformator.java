/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;
import com.mycompany.umlspnp.models.MainModel;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.transformators.spnp.*;
import cz.muni.fi.spnp.core.transformators.spnp.code.SPNPCode;
import cz.muni.fi.spnp.core.transformators.spnp.options.Option;
import cz.muni.fi.spnp.core.transformators.spnp.options.SPNPOptions;
import cz.muni.fi.spnp.core.transformators.spnp.parameters.InputParameter;
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

    public void transform() {
        var deploymentDiagram = model.getDeploymentDiagram();
        var sequenceDiagram = model.getSequenceDiagram();

        // Physical segment
        var physicalSegment = new PhysicalSegment(petriNet, deploymentDiagram, sequenceDiagram);
        physicalSegment.transform();

        // Highest-level usage segment
        var usageSegment = new PhysicalSegment(petriNet, deploymentDiagram, sequenceDiagram);
        usageSegment.transform();
    }

    public String getOutput(){
        return transformator.transform(petriNet);
    }
}
