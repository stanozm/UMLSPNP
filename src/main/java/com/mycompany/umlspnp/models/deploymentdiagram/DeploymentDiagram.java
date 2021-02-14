/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;


import com.mycompany.umlspnp.common.ElementContainer;
import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.models.common.NamedNode;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;

/**
 *
 * @author 10ondr
 */
public class DeploymentDiagram {
    private static final ElementContainer allElements = new ElementContainer<NamedNode, CommunicationLink>();
    private final ObservableList<LinkType> allLinkTypes;
    
    public DeploymentDiagram(){
        allLinkTypes = FXCollections.observableArrayList(
                new Callback<LinkType, Observable[]>() {
                    @Override
                    public Observable[] call(LinkType param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        allLinkTypes.add(new LinkType("Default", 1.0));
    }
    
    public static ElementContainer getElementContainer(){
        return allElements;
    }
    
    public void addAllNodesChangeListener(MapChangeListener listener){
        allElements.addAllNodesChangeListener(listener);
    }
    
    public void addCommunicationLinksChangeListener(MapChangeListener listener){
        allElements.addAllConnectionsChangeListener(listener);
    }

    public DeploymentTarget createDeploymentTarget(DeploymentTarget parent){
        var newDT = new DeploymentTarget("New deployment target", parent);
        addNode(newDT);
        if(parent != null)
            parent.addInnerNode(newDT);
        else
            newDT.getObjectInfo().setGroupID(Utils.generateGroupID());
        return newDT;
    }
    
    public Artifact createArtifact(DeploymentTarget parent){
        var newArtifact = new Artifact("New artifact", parent);
        addNode(newArtifact);
        if(parent != null)
            parent.addInnerNode(newArtifact);
        return newArtifact;
    }
    
    public void addNode(NamedNode newNode){
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
    
    public DeploymentTarget getDeploymentTarget(int objectID){
        var node = allElements.getNode(objectID);
        if(node instanceof DeploymentTarget)
            return (DeploymentTarget) node;
        return null;
    }
    
    public CommunicationLink createCommunicationLink(DeploymentTarget source, DeploymentTarget destination){
        var commLink = new CommunicationLink(source, destination, allLinkTypes);
        
        allElements.addConnection(commLink, commLink.getObjectInfo().getID());
        
        source.addInnerConnection(commLink);
        destination.addInnerConnection(commLink);
        
        return commLink;
    }
    
    public boolean removeCommunicationLink(int objectID){
        return allElements.removeConnection(objectID);
    }
    
    public CommunicationLink getCommunicationLink(int objectID){
        var connection = allElements.getConnection(objectID);
        if(connection instanceof CommunicationLink)
            return (CommunicationLink) connection;
        return null;
    }
    
    public LinkType createLinkType(String name, Double rate){
        var newLinkType = new LinkType(name, rate); 
        allLinkTypes.add(newLinkType);
        
        return newLinkType;
    }
    
    public boolean removeLinkType(LinkType linkType){
        if(allLinkTypes.size() > 1) // One default link type must remain
            return allLinkTypes.remove(linkType);
        return false;
    }

    public ObservableList getAllLinkTypes(){
        return allLinkTypes;
    }
    
    public boolean areNodesInGroup(DeploymentTarget first, DeploymentTarget second){
        return first.getObjectInfo().getGroupID() == second.getObjectInfo().getGroupID();
    }
    
    public boolean areNodesConnected(DeploymentTarget first, DeploymentTarget second){
        if(areNodesInGroup(first, second))
            return true;
        
        // TODO: What is the expected behavior of nodes being connected (multiple connections,...?)
        boolean connected = false;
        for(var obj : allElements.getConnections().values()){
            var link = (CommunicationLink) obj;
            var linkFirst = link.getFirst();
            var linkSecond = link.getSecond();

            if( (linkFirst.equals(first) || linkSecond.equals(first)) &&
                (linkFirst.equals(second) || linkSecond.equals(second))){
                connected = true;
                break;
            }
        }

        return connected;
    }
}