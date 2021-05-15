package cz.muni.fi.umlspnp.models.deploymentdiagram;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.models.ObservableString;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *  Deployment Target nodes can be part of a Redundancy Group which represents their interchangeability.
 *
 */
public class RedundancyGroup extends ObservableString {
    @Expose(serialize = true)
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
    
    public void clear() {
        if(nodes != null) {
            nodes.forEach(node -> {
                node.setRedundancyGroup(null);
            });

            nodes.clear();
        }
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

