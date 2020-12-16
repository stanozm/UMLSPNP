/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import com.mycompany.umlspnp.views.common.Annotation;
import com.mycompany.umlspnp.views.common.BasicRectangle;
import com.mycompany.umlspnp.views.common.Connection;
import com.mycompany.umlspnp.views.common.ConnectionContainer;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import java.util.HashMap;
import javafx.scene.Group;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author 10ondr
 */
public class DeploymentDiagramView extends DiagramView {
    private final Group root;
    private final HashMap<Number, DeploymentTargetView> deploymentTargetViews;
    private final HashMap<Number, Connection> connections;
    
    private final ConnectionContainer connectionContainer = new ConnectionContainer();
    
    public DeploymentDiagramView(){
        this.root = new Group();
        
        this.deploymentTargetViews = new HashMap();
        this.connections = new HashMap();
        
        diagramPane.getChildren().add(root);
    }

    public DeploymentTargetView getDeploymentTarget(int objectID){
        return deploymentTargetViews.get(objectID);
    }
    
    public DeploymentTargetView getDeploymentTargetRecursive(int objectID){
        var DTV = getDeploymentTarget(objectID);
        if(DTV != null)
            return DTV;
        for(var item : deploymentTargetViews.values()){
            var innerNode = item.getInnerNodeRecursive(objectID);
            if(innerNode instanceof DeploymentTargetView)
                return (DeploymentTargetView) innerNode;
        }
        return null;
    }
    
    public void addMenu(Menu newMenu){
        diagramMenu.getMenus().add(newMenu);
    }

    public DeploymentTargetView createDeploymentTarget(int modelObjectID){
        var dt = new DeploymentTargetView(0, 10, 150, 150, 10, modelObjectID);
        registerNodeToSelect(dt);
        
        dt.setRestrictionsInParent(null);
        
        root.getChildren().add(dt);
        deploymentTargetViews.put(modelObjectID, dt);
        
        Annotation states = dt.getStatesAnnotation();
        Annotation stateTransitions = dt.getStateTransitionsAnnotation();
        Annotation stateOperations = dt.getStateOperationsAnnotation();
        
        root.getChildren().add(states);
        root.getChildren().add(states.getLine());
        states.getLine().toBack();
        root.getChildren().add(stateTransitions);
        root.getChildren().add(stateTransitions.getLine());
        stateTransitions.getLine().toBack();
        root.getChildren().add(stateOperations);
        root.getChildren().add(stateOperations.getLine());
        stateOperations.getLine().toBack();
        
        return dt;
    }
    
    public DeploymentTargetView createDeploymentTarget(DeploymentTargetView parentNode, int modelObjectID){
        var dt = parentNode.CreateDeploymentTarget(modelObjectID);
        registerNodeToSelect(dt);
        
        return dt;
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
    
    public boolean removeDeploymentTargetView(int objectID){
        DeploymentTargetView DTV = getDeploymentTarget(objectID);

        if(DTV != null){
            boolean result = deploymentTargetViews.remove(objectID) != null;
            if(result){
                Annotation states = DTV.getStatesAnnotation();
                Annotation stateTransitions = DTV.getStateTransitionsAnnotation();
                root.getChildren().remove(states);
                root.getChildren().remove(states.getLine());
                root.getChildren().remove(stateTransitions);
                root.getChildren().remove(stateTransitions.getLine());
                
                root.getChildren().remove(DTV);
            }
            return result;
        }
        return false;
    }

    public ConnectionContainer getConnectionContainer(){
        return connectionContainer;
    }
    
    public void startConnection(DeploymentTargetView startingNode){
        connectionContainer.clear();
        connectionContainer.setFirstElement(startingNode);
    }
    
    public Connection createConnection(DeploymentTargetView source, DeploymentTargetView destination, int connectionModelID){
        var c = new Connection(connectionModelID, source.getEmptySlot(), destination.getEmptySlot());

        connections.put(connectionModelID, c);
        root.getChildren().add(c);
        return c;
    }
    
    public Connection createConnection(int sourceID, int destinationID, int connectionModelID){
        var source = getDeploymentTargetRecursive(sourceID);
        var destination = getDeploymentTargetRecursive(destinationID);
        return createConnection(source, destination, connectionModelID);
    }
    
    public boolean removeConnection(int connectionModelID){
        var connection = connections.get(connectionModelID);

        if(connection == null)
            return false;

        connection.removeSlots();
        connections.remove(connectionModelID);
        root.getChildren().remove(connection);
        return true;
    }
}
