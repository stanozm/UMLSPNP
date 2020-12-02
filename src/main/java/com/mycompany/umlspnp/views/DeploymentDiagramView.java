/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import com.mycompany.umlspnp.views.common.Connection;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import java.util.HashMap;
import javafx.scene.Group;
import javafx.scene.control.Menu;

/**
 *
 * @author 10ondr
 */
public class DeploymentDiagramView extends DiagramView {
    private final Group root;
        private final HashMap<Number, DeploymentTargetView> deploymentTargetViews;
    
    public DeploymentDiagramView(){
        this.root = new Group();
        
        this.deploymentTargetViews = new HashMap();
        
        diagramPane.getChildren().add(root);
    }

    public DeploymentTargetView getDeploymentTarget(int objectID){
        return deploymentTargetViews.get(objectID);
    }
    
    public void addMenu(Menu newMenu){
        diagramMenu.getMenus().add(newMenu);
    }

    public DeploymentTargetView CreateDeploymentTarget(int modelObjectID){
        var dt = new DeploymentTargetView(0, 10, 150, 150, 10, modelObjectID);

        root.getChildren().add(dt);
        deploymentTargetViews.put(modelObjectID, dt);
        return dt;
    }
    
    public boolean deleteDeploymentTargetView(int objectID){
        DeploymentTargetView DTV = getDeploymentTarget(objectID);

        if(DTV != null){
            boolean result = deploymentTargetViews.remove(objectID) != null;
            if(result){
                root.getChildren().remove(DTV);
            }
            return result;
        }
        return false;
    }

    public Connection CreateConnection(DeploymentTargetView source, DeploymentTargetView destination){
        var c = new Connection(source.getEmptySlot(), destination.getEmptySlot());
        
        root.getChildren().add(c);
        
        return c;
    }
}
