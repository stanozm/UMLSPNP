/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.*;
import java.util.HashSet;

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
    
    public HashSet<Artifact> getConnectedNodes(){
        System.err.println("Artifact getConnectedNodes()");
        var connectedNodes = new HashSet();
        if(this.DTparent != null){
            connectedNodes.add(this.DTparent);
            connectedNodes.addAll(this.DTparent.getConnectedNodes());
        }
        
        return connectedNodes;
    }
}
