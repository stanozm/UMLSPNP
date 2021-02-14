/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.BasicNode;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import javafx.beans.property.StringProperty;

/**
 *
 * @author 10ondr
 */
public class Lifeline extends BasicNode {
    private final Artifact artifact;
    
    public Lifeline(Artifact linkedArtifact){
        this.artifact = linkedArtifact;
    }
    
    public StringProperty nameProperty(){
        return this.artifact.getNameProperty();
    }
}
