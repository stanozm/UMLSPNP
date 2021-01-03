/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.*;

/**
 *
 * @author 10ondr
 */
public class Artifact extends NamedNode{
    private final DeploymentTarget DTparent;
    
    public Artifact(String name, DeploymentTarget parent){
        super(name);
        
        DTparent = parent;
    }
    
    public DeploymentTarget getParent(){
        return DTparent;
    }
}
