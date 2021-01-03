/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.common;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class ElementContainer<T1, T2> {
    private final ObservableMap<Number, T1> allNodes = FXCollections.observableHashMap();
    private final ObservableMap<Number, T2> allConnections = FXCollections.observableHashMap();
    
    private static ElementContainer mInstanceModel = null;
    private static ElementContainer mInstanceView = null;
    
    private ElementContainer(){
    }
    
    public static ElementContainer getInstanceModel(){
        if(mInstanceModel == null)
            mInstanceModel = new ElementContainer();
        
        return mInstanceModel;
    }
    
    public static ElementContainer getInstanceView(){
        if(mInstanceView == null)
            mInstanceView = new ElementContainer();
        
        return mInstanceView;
    }
    
    public T1 getNode(int objectID){
        return allNodes.get(objectID);
    }
    
    public void addNode(T1 newNode, int objectID){
        allNodes.put(objectID, newNode);
    }
        
    public boolean removeNode(int objectID){
        var removed = allNodes.remove(objectID);
        return removed != null;
    }
    
    public T2 getConnection(int objectID){
        return allConnections.get(objectID);
    }
    
    public void addConnection(T2 newConnection, int objectID){
        allConnections.put(objectID, newConnection);
    }

    public boolean removeConnection(int objectID){
        var removed = allConnections.remove(objectID);
        return removed != null;
    }
    
    public void addAllNodesChangeListener(MapChangeListener listener){
        allNodes.addListener(listener);
    }
    
    public void addAllConnectionsChangeListener(MapChangeListener listener){
        allConnections.addListener(listener);
    }
}
