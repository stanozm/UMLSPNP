/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models;

import com.mycompany.umlspnp.models.deploymentdiagram.*;

/**
 *
 * @author 10ondr
 */
public class MainModel {
    private final DeploymentDiagram deploymentDiagram;
    
    public MainModel(){
        deploymentDiagram = new DeploymentDiagram();
    }
    
    public DeploymentDiagram getDeploymentDiagram(){
        return deploymentDiagram;
    }
}
