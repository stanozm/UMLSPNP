/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

/**
 *
 * @author 10ondr
 */
public class CommunicationLink {
    private final DeploymentTarget target1;
    private final DeploymentTarget target2;
    
    public CommunicationLink(DeploymentTarget target1, DeploymentTarget target2){
        this.target1 = target1;
        this.target2 = target2;
    }
}
