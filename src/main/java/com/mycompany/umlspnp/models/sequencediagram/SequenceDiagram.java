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
    private static final ElementContainer allElements = new ElementContainer<Lifeline, Message>();
    
    public SequenceDiagram(){
    
    }
    
    public void addAllNodesChangeListener(MapChangeListener listener){
        allElements.addAllNodesChangeListener(listener);
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
    
    public Lifeline getLifeline(int objectID){
        var lifeline = allElements.getNode(objectID);
        if(lifeline instanceof Lifeline)
            return (Lifeline) lifeline;
        return null;
    }
}
