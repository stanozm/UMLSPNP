package cz.muni.fi.umlspnp.models.sequencediagram;

import cz.muni.fi.umlspnp.common.ElementContainer;
import cz.muni.fi.umlspnp.models.deploymentdiagram.Artifact;
import java.util.ArrayList;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *  The sequence diagram representation which holds all lifelines, messages and loops
 * related information and functionality.
 *
 */
public class SequenceDiagram {
    private final ElementContainer<Lifeline, Message> allElements = new ElementContainer<>();
    private final ObservableList<Message> sortedMessages;
    
    private final ObservableMap<Number, Loop> loops;
    private Lifeline highestLevelLifeline = null;
    
    public SequenceDiagram(){
        sortedMessages = FXCollections.observableArrayList();
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
    
    public ElementContainer getElementContainer(){
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
            new ArrayList<>(removedLifeline.getActivations()).forEach(activation -> {
                removeActivation(activation);
            });
            return allElements.removeNode(objectID);
        }
        return false;
    }
    
    public boolean removeLifeline(Artifact artifact){
        var lifeline = getLifeline(artifact);
        if(lifeline != null) {
            return removeLifeline(lifeline.getObjectInfo().getID());
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
    
    public Activation getActivation(int objectID){
        var lifelines = allElements.getNodes().values();
        for(var lifeline : lifelines) {
            for(var activation : lifeline.getActivations()) {
                if(activation.getObjectInfo().getID() == objectID) {
                    return activation;
                }
            }
        }
        return null;
    }
    
    private boolean removeActivation(Activation activation) {
        new ArrayList<>(activation.getMessages()).forEach(message -> {
            removeMessage(message.getObjectInfo().getID());
        });

        for(var lifeline : allElements.getNodes().values()) {
            if(lifeline.getActivation(activation.getObjectInfo().getID()) != null) {
                lifeline.removeActivation(activation.getObjectInfo().getID());
                return true;
            }        
        }
        return false;
    }
    
    public boolean removeActivation(int objectID) {
        var activation = getActivation(objectID);
        return removeActivation(activation);
    }
    
    public Collection<Lifeline> getLifelines() {
        return allElements.getNodes().values();
    }

    public Message createMessage(Activation source, Activation destination){
        var message = new Message(source, destination);
        
        sortedMessages.add(message);
        allElements.addConnection(message, message.getObjectInfo().getID());

        source.addMessage(message);
        destination.addMessage(message);
        
        return message;
    }
    
    public boolean removeMessage(int objectID){
        var message = getMessage(objectID);
        if(message == null)
            return false;
        message.getFrom().removeMessage(message);
        message.getTo().removeMessage(message);
        
        sortedMessages.remove(message);
        return allElements.removeConnection(objectID);
    }
    
    public Message getMessage(int objectID){
        return allElements.getConnection(objectID);
    }
    
    public ObservableList<Message> getSortedMessages() {
        return sortedMessages;
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
    
    public Collection<Loop> getLoops() {
        return loops.values();
    }
    
    public void addLoopsChangeListener(MapChangeListener listener){
        loops.addListener(listener);
    }
    
    public void setHighestLevelLifeline(int objectID) {
        highestLevelLifeline = getLifeline(objectID);
    }
    
    public Lifeline getHighestLevelLifeline() {
        return highestLevelLifeline;
    }
}
