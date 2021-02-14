/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.common.ElementContainer;
import com.mycompany.umlspnp.views.DiagramView;
import com.mycompany.umlspnp.views.common.Annotation;
import com.mycompany.umlspnp.views.common.BasicRectangle;
import com.mycompany.umlspnp.views.common.ConnectionContainer;
import com.mycompany.umlspnp.views.common.NamedRectangle;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author 10ondr
 */
public class DeploymentDiagramView extends DiagramView {
    private final Group root;
    
    private static final ElementContainer allElements = new ElementContainer<NamedRectangle, CommunicationLinkView>();
    
    private final ConnectionContainer connectionContainer = new ConnectionContainer();
    
    public DeploymentDiagramView(){
        this.root = new Group();
        
        diagramPane.getChildren().add(root);
    }

    public static ElementContainer getElementContainer(){
        return allElements;
    }
    
    public NamedRectangle getNode(int objectID){
        return (NamedRectangle) allElements.getNode(objectID);
    }
    
    public DeploymentTargetView getDeploymentTargetView(int objectID){
        var node = getNode(objectID);
        if(node instanceof DeploymentTargetView)
            return (DeploymentTargetView) node;
        return null;
    }
    
    public DeploymentTargetView createDeploymentTargetView(DeploymentTargetView parentNode, int modelObjectID){
        var dt = new DeploymentTargetView(0, 10, 0, 0, 10, root, modelObjectID);
        allElements.addNode(dt, modelObjectID);
        registerNodeToSelect(dt);
        
        if(parentNode == null){
            dt.setRestrictionsInParent(root);
            root.getChildren().add(dt);
        }
        else{
            parentNode.addInnerNode(dt);
        }
        
        dt.changeDimensions(150, 150);
        return dt;
    }
        
    public ArtifactView CreateArtifact(DeploymentTargetView parentNode, int modelObjectID){
        var newArtifact = new ArtifactView(0, 0, 0, 0, modelObjectID);
        allElements.addNode(newArtifact, modelObjectID);
        parentNode.addInnerNode(newArtifact);
        newArtifact.changeDimensions(150, 150);
        return newArtifact;
    }
    
    private void registerNodeToSelect(BasicRectangle node){
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            var startElement = connectionContainer.getFirstElement();
            if(startElement != null){
                if(startElement != node && startElement.getClass().equals(node.getClass())){
                    connectionContainer.setSecondElement(node);
                }
                else{
                    System.err.println("Unable to create connection. Select suitable destination node.");
                    connectionContainer.clear();
                }
            }
        });
    }
    
    private boolean removeInnerNode(NamedRectangle removedNode){
        var parent = removedNode.getParentDeploymentTargetview();
        if(parent != null)
            return parent.removeInnerNode(removedNode.getObjectInfo().getID());
        return false;
    }
    
    public boolean removeNode(int objectID){
        var removedNode = getNode(objectID);

        if(removedNode == null)
            return false;

        boolean result = allElements.removeNode(objectID);
        if(result){
            if(removedNode instanceof DeploymentTargetView){
                var DTV = (DeploymentTargetView) removedNode;
                Annotation states = DTV.getStatesAnnotation();
                Annotation stateTransitions = DTV.getStateTransitionsAnnotation();
                Annotation stateOperations = DTV.getStateOperationsAnnotation();
                root.getChildren().remove(states);
                root.getChildren().remove(states.getLine());
                root.getChildren().remove(stateTransitions);
                root.getChildren().remove(stateTransitions.getLine());
                root.getChildren().remove(stateOperations);
                root.getChildren().remove(stateOperations.getLine());
            }
 
            if(!root.getChildren().remove(removedNode)){
                removeInnerNode(removedNode);
            }
        }
        return result;
    }

    public ConnectionContainer getConnectionContainer(){
        return connectionContainer;
    }
    
    public void startConnection(DeploymentTargetView startingNode){
        connectionContainer.clear();
        connectionContainer.setFirstElement(startingNode);
    }
    
    public CommunicationLinkView getConnection(int objectID){
        var connection = allElements.getConnection(objectID);

        if(connection instanceof CommunicationLinkView)
            return (CommunicationLinkView) connection;
        return null;
    }
    
    public CommunicationLinkView createConnection(DeploymentTargetView source, DeploymentTargetView destination, int connectionModelID){
        var newConnection = new CommunicationLinkView(connectionModelID, source.getEmptySlot(), destination.getEmptySlot(), root);

        allElements.addConnection(newConnection, connectionModelID);
        root.getChildren().add(newConnection);
        newConnection.refreshLinePosition();
        return newConnection;
    }
    
    public CommunicationLinkView createConnection(int sourceID, int destinationID, int connectionModelID){
        var source = getDeploymentTargetView(sourceID);
        var destination = getDeploymentTargetView(destinationID);
        return createConnection(source, destination, connectionModelID);
    }
    
    public boolean removeConnection(int connectionModelID){
        var connection = getConnection(connectionModelID);

        if(connection == null)
            return false;

        connection.removeSlots();
        allElements.removeConnection(connectionModelID);
        root.getChildren().remove(connection);
        return true;
    }
}
