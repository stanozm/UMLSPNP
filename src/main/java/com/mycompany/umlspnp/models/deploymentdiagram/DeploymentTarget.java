package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.OperationEntry;
import com.mycompany.umlspnp.common.ElementContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Pair;

/**
 * Deployment Target as specified by the formal deployment diagram specification.
 * The represented node is some physical or other system which can fail, communicate with other 
 * systems and have children nodes (Deployment Targets or Artifacts).
 *
 */
public class DeploymentTarget extends Artifact {
    private final ObjectProperty<RedundancyGroup> redundancyGroup = new SimpleObjectProperty<>();
    private final ElementContainer<Artifact, CommunicationLink> allElements;

    private final ObservableMap<Number, Artifact> innerNodes;
    private final ObservableMap<Number, CommunicationLink> innerConnections;

    // Annotations
    private final ObservableList<State> states;
    private final ObservableList<StateTransition> stateTransitions;
    private final ObservableList<StateOperation> stateOperations;

    // Listeners
    private final Map<StateOperation, ListChangeListener> OperationEntriesListeners = new HashMap();

    // Shortcuts
    private final ObservableList<State> statesWithoutOperations;
    private final ObservableList<OperationEntry> allOperationEntries;

    public DeploymentTarget(ElementContainer<Artifact, CommunicationLink> allElements, String name, DeploymentTarget parent){
        super(name, parent);
        
        this.allElements = allElements;
        innerNodes = FXCollections.observableHashMap();
        innerConnections = FXCollections.observableHashMap();
        
        states = FXCollections.observableArrayList((State param) -> new Observable[]{
            param.getStringRepresentation()
        });
        
        stateTransitions = FXCollections.observableArrayList((StateTransition param) -> new Observable[]{
            param.getStringRepresentation()
        });

        stateOperations = FXCollections.observableArrayList((StateOperation param) -> new Observable[]{
            param.getStringRepresentation()
        });

        allOperationEntries = FXCollections.observableArrayList((OperationEntry param) -> new Observable[]{
            param.getStringRepresentation()
        });
        
        statesWithoutOperations = FXCollections.observableArrayList();
        initStatesWithoutOperations();
    }
    
    private void cleanup(){
        var connections = new ArrayList<CommunicationLink>(innerConnections.values());
        
        for(var connection : connections){
            connection.cleanup();
            allElements.removeConnection(connection.getObjectInfo().getID());
        }
    }

    public void cleanupRecursive(){
        for(var item : innerNodes.values()){
            if(item == null)
                continue;

            if(item instanceof DeploymentTarget){
                var removedDeploymentTarget = (DeploymentTarget) item;
                removedDeploymentTarget.cleanupRecursive();
            }
            allElements.removeNode(item.getObjectInfo().getID());
        }
        cleanup();
    }
    
    public boolean removeInnerNode(int objectID){
        var removed = innerNodes.remove(objectID);
        return removed != null;
    }

    public void addInnerNodesChangeListener(MapChangeListener listener){
        innerNodes.addListener(listener);
    }

    public void addInnerNode(Artifact newInnerNode){
        innerNodes.put(newInnerNode.getObjectInfo().getID(), newInnerNode);
    }
    
    public void removeConnection(CommunicationLink removedConnection){
        innerConnections.remove(removedConnection.getObjectInfo().getID());
    }
    
    public void addInnerConnectionsChangeListener(MapChangeListener listener){
        innerConnections.addListener(listener);
    }

    public void addInnerConnection(CommunicationLink newConnection){
        innerConnections.put(newConnection.getObjectInfo().getID(), newConnection);
    }

    public ObjectProperty<RedundancyGroup> redundancyGroupProperty() {
        return redundancyGroup;
    }

    public RedundancyGroup getRedundancyGroup() {
        return redundancyGroup.getValue();
    }

    public void setRedundancyGroup(RedundancyGroup newRedundancyGroup) {
        redundancyGroup.setValue(newRedundancyGroup);
    }

    public Set<DeploymentTarget> getRedundantNodes() {
        var nodes = allElements.getNodes().values();
        var result = new HashSet<DeploymentTarget>();
        if(this.getRedundancyGroup() == null)
            return result;

        nodes.forEach(node -> {
            if(node instanceof DeploymentTarget) {
                var dt = (DeploymentTarget) node;
                if(this.getRedundancyGroup() == dt.getRedundancyGroup()) {
                    result.add(dt);
                }
            }
        });
        return result;
    }

    public void addStatesChangeListener(ListChangeListener listener){
        states.addListener(listener);
    }
    
    public void addStateTransitionsChangeListener(ListChangeListener listener){
        stateTransitions.addListener(listener);
    }
    
    public void addStateOperationsChangeListener(ListChangeListener listener){
        stateOperations.addListener(listener);
    }
    
    public void addState(State newState){
        states.add(newState);
    }
    
    public boolean removeState(State removedState){
        return states.remove(removedState);
    }
    
    public void addStateTransition(StateTransition newTransition){
        stateTransitions.add(newTransition);
    }
    
    public void addStateOperation(StateOperation newOperation){
        stateOperations.add(newOperation);
        
        var operationsListener = new ListChangeListener(){
                @Override
                public void onChanged(ListChangeListener.Change change) {
                    while (change.next()) {
                        if(change.wasAdded()){
                            for(var addedObject : change.getAddedSubList()){
                                var addedEntry = (OperationEntry) addedObject;
                                allOperationEntries.add(addedEntry);
                            }
                        }
                        else if(change.wasRemoved()){
                            for(var removedObject : change.getRemoved()){
                                var removedEntry = (OperationEntry) removedObject;
                                allOperationEntries.remove(removedEntry);
                            }
                        }
                    }
                }
            };
        newOperation.getOperationEntries().addListener(operationsListener);
        OperationEntriesListeners.put(newOperation, operationsListener);
    }
    
    public boolean removeStateOperation(StateOperation removedOperation){
        var listener = OperationEntriesListeners.get(removedOperation);
        removedOperation.getOperationEntries().removeListener(listener);
        OperationEntriesListeners.remove(removedOperation);
        return stateOperations.remove(removedOperation);
    }
    
    public ObservableList<OperationEntry> getAllOperationEntries(){
        return allOperationEntries;
    }

    public ObservableList<State> getStates() {
        return this.states;
    }
    
    public ObservableList<State> getStatesWithoutOperations() {
        return this.statesWithoutOperations;
    }
    
    public void setDefaultState(State newDefaultState){
        states.forEach(state -> {
            if(state.equals(newDefaultState))
                state.setDefault(true);
            else
                state.setDefault(false);
        });
    }
    
    public ObservableList<StateTransition> getStateTransitions(){
        return this.stateTransitions;
    }
    
    public ObservableList<StateOperation> getStateOperations(){
        return this.stateOperations;
    }
    
    private boolean stateHasOperations(State state){
        for(var operation : stateOperations){
            if(state == operation.getState()){
                return true;
            }
        }
        return false;
    }
    
    public void refilterStatesWithoutOperations(){
        this.statesWithoutOperations.setAll(states.filtered(state -> !stateHasOperations(state)));
    }
    
    public final void initStatesWithoutOperations(){
        refilterStatesWithoutOperations();
        
        states.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (var addedItem : change.getAddedSubList()){
                            var addedState = (State) addedItem;
                            statesWithoutOperations.add(addedState);
                        }
                    }
                    else if (change.wasRemoved()) {
                        for(var removedItem : change.getRemoved()){
                            var removedState = (State) removedItem;
                            statesWithoutOperations.remove(removedState);
                        }
                    }
                }
            }
        });
        
        stateOperations.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refilterStatesWithoutOperations();
            }
        });
    }
    
    @Override
    public Set<Pair<CommunicationLink, Artifact>> getConnectedNodes(){
        var connectedNodes = new HashSet<Pair<CommunicationLink, Artifact>>();
        connectedNodes.addAll(getConnectedNodes(true, false));
        connectedNodes.addAll(getConnectedNodes(false, false));
        
        return connectedNodes;
    }
    
    @Override
    public Set<Pair<CommunicationLink, Artifact>> getConnectedNodes(boolean directionUp, boolean shallow) {
        var connectedNodes = new HashSet<Pair<CommunicationLink, Artifact>>();
        if(!shallow){
            innerConnections.values().forEach(connection -> {
                var other = connection.getOther(this);
                connectedNodes.add(new Pair<>(connection, other));
                var otherShallow = other.getConnectedNodes(true, true);
                otherShallow.addAll(other.getConnectedNodes(false, true));
                otherShallow.forEach(pair -> {
                    connectedNodes.add(new Pair<>(connection, pair.getValue()));
                });
            });
        }
        
        if(directionUp) {
            innerNodes.values().forEach(child -> {
                connectedNodes.add(new Pair<>(null, child));
                connectedNodes.addAll(child.getConnectedNodes(true, shallow));
            });
        }
        else{
            var parent = getParent();
            if(parent != null){
                connectedNodes.add(new Pair<>(null, parent));
                connectedNodes.addAll(parent.getConnectedNodes(false, shallow));
            }
        }
        return connectedNodes;
    }
}
