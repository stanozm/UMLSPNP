/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.BasicNode;

/**
 *
 * @author 10ondr
 */
public class CommunicationLink extends BasicNode {
    private final DeploymentTarget target1;
    private final DeploymentTarget target2;
    
    public CommunicationLink(DeploymentTarget target1, DeploymentTarget target2){
        this.target1 = target1;
        this.target2 = target2;
    }
    
    public DeploymentTarget getFirst(){
        return target1;
    }
    
    public DeploymentTarget getSecond(){
        return target2;
    }
}
