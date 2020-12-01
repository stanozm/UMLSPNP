/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import com.mycompany.umlspnp.views.common.Connection;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.control.Menu;

/**
 *
 * @author 10ondr
 */
public class DeploymentDiagramView extends DiagramView {
    private final Group root;
        private final ArrayList<DeploymentTargetView> deploymentTargetViews;
    
    public DeploymentDiagramView(){
        this.root = new Group();
        
        this.deploymentTargetViews = new ArrayList();
        
        diagramPane.getChildren().add(root);
    }

    public DeploymentTargetView getDeploymentTarget(int objectID){
        for (var item : deploymentTargetViews){
            DeploymentTargetView DTV = (DeploymentTargetView) item;
            if(DTV.getObjectInfo().getID() == objectID){
                return item;
            }
        }
        return null;
    }
    
    public void addMenu(Menu newMenu){
        diagramMenu.getMenus().add(newMenu);
    }

    public DeploymentTargetView CreateDeploymentTarget(int modelObjectID){
        var dt = new DeploymentTargetView(0, 10, 150, 150, 10, modelObjectID);

        root.getChildren().add(dt);
        deploymentTargetViews.add(dt);
        return dt;
    }
    
    public boolean deleteDeploymentTargetView(int objectID){
        DeploymentTargetView DTV = getDeploymentTarget(objectID);

        if(DTV != null){
            boolean result = deploymentTargetViews.remove(DTV);
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
