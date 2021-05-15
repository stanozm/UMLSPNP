package cz.muni.fi.umlspnp.models.deploymentdiagram;


import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.common.ElementContainer;
import cz.muni.fi.umlspnp.models.Connection;
import cz.muni.fi.umlspnp.models.Diagram;
import cz.muni.fi.umlspnp.models.OperationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

/**
 *  The deployment diagram representation which holds all deployment targets, artifacts,
 * communication links and related information and functionality.
 *
 */
public class DeploymentDiagram implements Diagram {
    @Expose(serialize = true, deserialize = false)
    private final ElementContainer<Artifact, CommunicationLink> allElements = new ElementContainer<>();
    
    @Expose(serialize = true)
    private final ObservableList<LinkType> allLinkTypes;
    @Expose(serialize = true)
    private final ObservableList<OperationType> operationTypes;
    @Expose(serialize = true)
    private final ObservableList<RedundancyGroup> redundancyGroups;
    
    public DeploymentDiagram(){
	operationTypes = FXCollections.observableArrayList((OperationType param) -> new Observable[]{
            param.stringRepresentationProperty()
        });

	redundancyGroups = FXCollections.observableArrayList();

        allLinkTypes = FXCollections.observableArrayList((LinkType param) -> new Observable[]{
            param.stringRepresentationProperty()
        });

        initAllElements();
    }
    
    public void createSampleData() {
        allLinkTypes.add(new LinkType("Default", 1.0));
    }
    
    private void initAllElements(){
        var connections = allElements.getConnections();
        connections.addListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasRemoved()){
                    var removedConnection = (CommunicationLink) change.getValueRemoved();
                    removedConnection.cleanup();
                }
            }
        
        });
    }
    
    public ElementContainer<Artifact, CommunicationLink> getElementContainer(){
        return allElements;
    }
    
    public void addAllNodesChangeListener(MapChangeListener listener){
        allElements.addAllNodesChangeListener(listener);
    }
    
    public void addCommunicationLinksChangeListener(MapChangeListener listener){
        allElements.addAllConnectionsChangeListener(listener);
    }
    
    public ObservableList<OperationType> getOperationTypes() {
        return operationTypes;
    }
    
    public OperationType getOperationType(String name) {
        for(var ot : getOperationTypes()) {
            if(ot.getName().equals(name))
                return ot;
        }
        return null;
    }
    
    public void addOperationType(OperationType operationType) {
        operationTypes.add(operationType);
    }
    
    public boolean removeOperationType(OperationType operationType) {
        return operationTypes.remove(operationType);
    }

    public ObservableList<RedundancyGroup> getRedundancyGroups() {
        return redundancyGroups;
    }
    
    public RedundancyGroup getRedundancyGroup(Integer num) {
        for(var rg : getRedundancyGroups()) {
            if(rg.getGroupID().equals(num))
                return rg;
        }
        return null;
    }
    
    public void createRedundancyGroup() {
        Integer newGroupID = 1;
        boolean foundFree = false;
        while(!foundFree) {
            foundFree = true;
            for(var rg : redundancyGroups){
                if(Objects.equals(rg.getGroupID(), newGroupID)) {
                    newGroupID += 1;
                    foundFree = false;
                    break;
                }
            }
        }
        redundancyGroups.add(new RedundancyGroup(newGroupID));
    }
    
    public void addRedundancyGroup(Integer groupID) {
        redundancyGroups.add(new RedundancyGroup(groupID));
    }

    public boolean removeRedundancyGroup(RedundancyGroup rg) {
        if(rg != null) {
            rg.clear();
            return redundancyGroups.remove(rg);
        }
        return false;
    }
    
    public DeploymentTarget createDeploymentTarget(DeploymentTarget parent){
        var newDT = new DeploymentTarget(allElements, "New deployment target", parent);
        addNode(newDT);
        if(parent != null)
            parent.addInnerNode(newDT);

        return newDT;
    }
    
    public Artifact createArtifact(DeploymentTarget parent){
        var newArtifact = new Artifact("New artifact", parent);
        addNode(newArtifact);
        if(parent != null)
            parent.addInnerNode(newArtifact);
        return newArtifact;
    }
    
    public void addNode(Artifact newNode){
        allElements.addNode(newNode, newNode.getObjectInfo().getID());
    }

    public boolean removeNode(int objectID){
        var removed = allElements.getNode(objectID);
        if(removed == null)
            return false;
        
        allElements.removeNode(objectID);
        
        if(removed instanceof DeploymentTarget){
            var removedDeploymentTarget = (DeploymentTarget) removed;
            DeploymentTarget parent = removedDeploymentTarget.getParent();
            if(parent != null)
                parent.removeInnerNode(objectID);
            removedDeploymentTarget.cleanupRecursive();
        }
        else if(removed instanceof Artifact){
            var removedArtifact = (Artifact) removed;
            DeploymentTarget parent = removedArtifact.getParent();
            if(parent != null)
                parent.removeInnerNode(objectID);
        }
        return true;
    }
    
    @Override
    public Artifact getNode(int objectID){
        return allElements.getNode(objectID);
    }
    
    public Collection<Artifact> getNodes() {
        return allElements.getNodes().values();
    }
    
    public DeploymentTarget getDeploymentTarget(int objectID){
        var node = allElements.getNode(objectID);
        if(node instanceof DeploymentTarget)
            return (DeploymentTarget) node;
        return null;
    }
    
    public CommunicationLink createCommunicationLink(DeploymentTarget source, DeploymentTarget destination){
        var commLink = new CommunicationLink(source, destination, allLinkTypes);
        
        addCommunicationLink(commLink);
        
        source.addInnerConnection(commLink);
        destination.addInnerConnection(commLink);
        
        return commLink;
    }
    
    public void addCommunicationLink(CommunicationLink cl) {
        allElements.addConnection(cl, cl.getObjectInfo().getID());
    }
    
    public boolean removeCommunicationLink(int objectID){
        return allElements.removeConnection(objectID);
    }
    
    @Override
    public Connection getConnection(int objectID) {
        return getCommunicationLink(objectID);
    }
    
    public CommunicationLink getCommunicationLink(int objectID){
        return allElements.getConnection(objectID);
    }
    
    public Collection<CommunicationLink> getCommunicationLinks() {
        return allElements.getConnections().values();
    }
    
    public LinkType getLinkType(String name) {
        for(var lt : getAllLinkTypes()) {
            if(lt.getName().equals(name))
                return lt;
        }
        return null;
    }
    
    public LinkType createLinkType(String name, Double rate){
        var newLinkType = new LinkType(name, rate); 
        allLinkTypes.add(newLinkType);
        
        return newLinkType;
    }
    
    public boolean removeLinkType(LinkType linkType) {
        return allLinkTypes.remove(linkType);
    }

    public ObservableList<LinkType> getAllLinkTypes(){
        return allLinkTypes;
    }
    
    public boolean areNodesConnected(Artifact first, Artifact second){
        if(first == null || second == null)
            return false;

        Set<Pair<CommunicationLink, Artifact>> connectedNodes;
        if(first instanceof DeploymentTarget)
            connectedNodes =  ((DeploymentTarget) first).getConnectedNodes();
        else
            connectedNodes = first.getConnectedNodes();

        for(var pair : connectedNodes) {
            if(pair.getValue() == second)
                return true;
        }
        return false;
    }
    
    public void clear() {
        var links = new ArrayList<>(getCommunicationLinks());
        for(var link : links) {
            removeCommunicationLink(link.getObjectInfo().getID());
        }
        var nodes = new ArrayList<>(getNodes());
        for(var node : nodes) {
            removeNode(node.getObjectInfo().getID());
        }
        var linkTypes = new ArrayList<>(getAllLinkTypes());
        for(var linkType : linkTypes) {
            removeLinkType(linkType);
        }
        var opTypes = new ArrayList<>(getOperationTypes());
        for(var operationType : opTypes) {
            removeOperationType(operationType);
        }
        var redGroups = new ArrayList<>(getRedundancyGroups());
        for(var redundancyGroup : redGroups) {
            this.removeRedundancyGroup(redundancyGroup);
        }
    }
}