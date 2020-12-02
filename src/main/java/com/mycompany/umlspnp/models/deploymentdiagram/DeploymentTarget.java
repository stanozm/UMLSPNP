/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.*;
import java.util.List;

/**
 *
 * @author 10ondr
 */
public class DeploymentTarget extends NamedNode {
    private List<DeploymentTarget> innerTargets;
    private List<Artifact> artifacts;
    
    public DeploymentTarget(String name){
        super(name);
    }
}
