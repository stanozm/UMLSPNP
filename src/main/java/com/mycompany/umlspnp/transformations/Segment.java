/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
import cz.muni.fi.spnp.core.models.PetriNet;

/**
 *
 * @author 10ondr
 */
public class Segment {
    protected final DeploymentDiagram deploymentDiagram;
    protected final SequenceDiagram sequenceDiagram;
    protected final PetriNet petriNet;
    
    public Segment(PetriNet petriNet, DeploymentDiagram deploymentDiagram, SequenceDiagram sequenceDiagram) {
        this.deploymentDiagram = deploymentDiagram;
        this.sequenceDiagram = sequenceDiagram;
        this.petriNet = petriNet;
    }
}
