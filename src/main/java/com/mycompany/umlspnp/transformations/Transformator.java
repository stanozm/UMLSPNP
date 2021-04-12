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

        /* Any global net alterations here */

        return net;
    }

    public void transform() {
        var deploymentDiagram = model.getDeploymentDiagram();
        var sequenceDiagram = model.getSequenceDiagram();

        // Physical segment
        var physicalSegment = new PhysicalSegment(petriNet, deploymentDiagram, sequenceDiagram);
        physicalSegment.transform();

        // Usage segment
        var usageSegment = new UsageSegment(petriNet, deploymentDiagram, sequenceDiagram);
        usageSegment.transform();
        
        // Communication segment
        deploymentDiagram.getCommunicationLinks().forEach(communicationLink -> {
            var communicationSegment = new CommunicationSegment(petriNet, deploymentDiagram, sequenceDiagram, usageSegment, communicationLink);
            communicationSegment.transform();
        });
    }

    public String getOutput(){
        return transformator.transform(petriNet);
    }
}
