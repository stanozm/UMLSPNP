/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.common.ElementContainer;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class SequenceDiagram {
    private static final ElementContainer<Lifeline, Message> allElements = new ElementContainer<>();
    private final ObservableMap<Number, Loop> loops;
    
    public SequenceDiagram(){
        loops = FXCollections.observableHashMap();
    }
    
    public void addLifelinesListener(MapChangeListener listener){
        allElements.addAllNodesChangeListener(listener);
    }
    
    public void removeLifelinesListener(MapChangeListener listener){
        allElements.removeAllNodesChangeListener(listener);
    }
    
    public void addMessagesListener(MapChangeListener listener){
        allElements.addAllConnectionsChangeListener(listener);
    }
    
    public void removeMessagesListener(MapChangeListener listener){
        allElements.removeAllConnectionsChangeListener(listener);
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
        var removedLifeline = getLifeline(objectID);
        if(removedLifeline != null){
            removedLifeline.getMessages().forEach(message -> {
                removeMessage(message.getObjectInfo().getID());
            });
            return allElements.removeNode(objectID);
        }
        return false;
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
    
    public boolean removeMessage(int objectID){
        return allElements.removeConnection(objectID);
    }
    
    public Message getMessage(int objectID){
        return allElements.getConnection(objectID);
    }
    
    public Loop createLoop(){
        var loop = new Loop();
        
        loops.put(loop.getObjectInfo().getID(), loop);
        return loop;
    }
    
    public boolean removeLoop(int objectID){
        return loops.remove(objectID) != null;
    }
    
    public Loop getLoop(int objectID){
        return loops.get(objectID);
    }
    
    public void addLoopsChangeListener(MapChangeListener listener){
        loops.addListener(listener);
    }
}
