package cz.muni.fi.umlspnp.common;

import com.google.gson.annotations.Expose;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * Container for a system of generally defined nodes and connections, each with
 * its unique number ID.
 * 
 * @param <T1>  Data type of the node
 * @param <T2>  Data type of the connection
 */
public class ElementContainer<T1, T2> {
    @Expose(serialize = true)
    private final ObservableMap<Number, T1> allNodes = FXCollections.observableHashMap();
    @Expose(serialize = true)
    private final ObservableMap<Number, T2> allConnections = FXCollections.observableHashMap();
    
    public ElementContainer(){
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
    
    public void removeAllNodesChangeListener(MapChangeListener listener){
        allNodes.removeListener(listener);
    }
    
    public void addAllConnectionsChangeListener(MapChangeListener listener){
        allConnections.addListener(listener);
    }
    
    public void removeAllConnectionsChangeListener(MapChangeListener listener){
        allConnections.removeListener(listener);
    }
    
    public ObservableMap<Number, T2> getConnections(){
        return allConnections;
    }
    
    public ObservableMap<Number, T1> getNodes(){
        return allNodes;
    }
}
