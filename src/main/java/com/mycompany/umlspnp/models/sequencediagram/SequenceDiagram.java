/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.common.ElementContainer;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import javafx.collections.MapChangeListener;

/**
 *
 * @author 10ondr
 */
public class SequenceDiagram {
    private static final ElementContainer<Lifeline, Message> allElements = new ElementContainer<>();
    
    public SequenceDiagram(){
    
    }
    
    public void addAllNodesChangeListener(MapChangeListener listener){
        allElements.addAllNodesChangeListener(listener);
    }
    
    public void removeAllNodesChangeListener(MapChangeListener listener){
        allElements.removeAllNodesChangeListener(listener);
    }
    
    public static ElementContainer getElementContainer(){
        return allElements;
    }
    
    public Lifeline createLifeline(Artifact artifact){
        var newLifeline = new Lifeline(artifact);
        
        allElements.addNode(newLifeline, newLifeline.getObjectInfo().getID());
        return newLifeline;
    }
    
    public boolean removeLifeline(int objectID){
        return allElements.removeNode(objectID);
    }
    
    public Lifeline getLifeline(Artifact artifact) {
        for (var lifeline : this.allElements.getNodes().values()){
            if(lifeline.getArtifact().equals(artifact)){
                return lifeline;
            }
        }
        return null;
    }
    
    public Lifeline getLifeline(int objectID){
        var lifeline = allElements.getNode(objectID);
        if(lifeline instanceof Lifeline)
            return (Lifeline) lifeline;
        return null;
    }
    
    public Message createMessage(Lifeline source, Lifeline destination){
        var message = new Message(source, destination);
        
        allElements.addConnection(message, message.getObjectInfo().getID());
        
        source.addMessage(message);
        destination.addMessage(message);
        
        return message;
    }
}
