/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.BasicNode;
import com.mycompany.umlspnp.models.common.OperationEntry;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class Lifeline extends BasicNode {
    private final Artifact artifact;
    private final ObservableMap<Number, Message> messages;
    
    // Only available while transforming to SPNP
    private ArrayList<Message> sortedMessages = null;
    
    public Lifeline(Artifact linkedArtifact){
        this.artifact = linkedArtifact;
        
        this.messages = FXCollections.observableHashMap();
    }
    
    public Artifact getArtifact(){
        return this.artifact;
    }
    
    public StringProperty nameProperty(){
        return this.artifact.getNameProperty();
    }
    
    public void addMessage(Message newMessage){
        messages.put(newMessage.getObjectInfo().getID(), newMessage);
    }
    
    public void removeMessage(Message removedMessage){
        messages.remove(removedMessage.getObjectInfo().getID());
    }
    
    public Collection<Message> getMessages(){
        return messages.values();
    }
    
    public void addMessagesChangeListener(MapChangeListener listener){
        messages.addListener(listener);
    }
    
    public ObservableList<OperationEntry> getOperationEntries(){
        if(this.artifact instanceof DeploymentTarget){
            var dt = (DeploymentTarget) this.artifact;
            return dt.getAllOperationEntries();
        }
        return null;
    }
    
    public void setSortedMessages(ArrayList<Message> sortedMessages) {
        this.sortedMessages = sortedMessages;
    }

    public ArrayList<Message> getSortedMessages() {
        return sortedMessages;
    }
}
