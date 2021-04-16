/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.ObservableString;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author 10ondr
 */
public class RedundancyGroup extends ObservableString {
    private final IntegerProperty groupID = new SimpleIntegerProperty();
    private final ObservableList<DeploymentTarget> nodes;

    public RedundancyGroup(Integer groupID) {
        this.groupID.setValue(groupID);
        
        nodes = FXCollections.observableArrayList();
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };

        this.groupID.addListener(stringChangeListener);
    }

    public void addNode(DeploymentTarget node) {
        nodes.add(node);
    }

    public boolean removeNode(DeploymentTarget node) {
        return nodes.remove(node);
    }

    public ObservableList<DeploymentTarget> getNodes() {
        return nodes;
    }

    public IntegerProperty groupIDProperty() {
        return groupID;
    }

    public Integer getGroupID() {
        return groupID.getValue();
    }
    
    @Override
    public String toString() {
        if(groupID.getValue() == null)
            return "";
        return groupID.getValue().toString().concat(".");
    }
}

