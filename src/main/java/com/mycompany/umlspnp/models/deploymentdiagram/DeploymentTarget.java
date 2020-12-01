/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.common.*;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author 10ondr
 */
public class DeploymentTarget {
    private final ObjectInfo objectInfo;
    private final StringProperty name = new SimpleStringProperty();
    private List<DeploymentTarget> innerTargets;
    private List<Artifact> artifacts;
    
    public DeploymentTarget(String name){
        this.objectInfo = new ObjectInfo(Utils.generateObjectID());
        this.name.setValue(name);
    }
    
    public StringProperty getNameProperty(){
        return name;
    }
    
    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }
}
