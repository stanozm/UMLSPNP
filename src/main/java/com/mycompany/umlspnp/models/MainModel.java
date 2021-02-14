/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;

/**
 *
 * @author 10ondr
 */
public class MainModel {
    private final DeploymentDiagram deploymentDiagram;
    private final SequenceDiagram sequenceDiagram;
    
    public MainModel(){
        deploymentDiagram = new DeploymentDiagram();
        sequenceDiagram = new SequenceDiagram();
    }
    
    public DeploymentDiagram getDeploymentDiagram(){
        return deploymentDiagram;
    }
    
    public SequenceDiagram getSequenceDiagram(){
        return sequenceDiagram;
    }
}
