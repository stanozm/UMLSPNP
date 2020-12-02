/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.views.*;
import com.mycompany.umlspnp.models.*;
import com.mycompany.umlspnp.models.deploymentdiagram.*;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author 10ondr
 */
public class MainController {
    private final MainModel model;
    private final MainView view;
    
    public MainController(MainModel mainModel, MainView mainView){
        this.model = mainModel;
        this.view = mainView;
        
        deploymentDiagramInit(this.model.getDeploymentDiagram());
    }

    public MainView getView(){
        return view;
    }
    
    private void deploymentDiagramInit(DeploymentDiagram deployment){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        
        deployment.addDeploymentTargetsChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    DeploymentTarget newDT = (DeploymentTarget) change.getValueAdded();
                    var newDTView = deploymentDiagramView.CreateDeploymentTarget(newDT.getObjectInfo().getID());
                    deploymentTargetInit(newDTView);
                }
                else if(change.wasRemoved()){
                    DeploymentTarget removedDT = (DeploymentTarget) change.getValueRemoved();
                    deploymentDiagramView.deleteDeploymentTargetView(removedDT.getObjectInfo().getID());
                }
            }
        });

        Menu addNodeMenu = new Menu("Add Node");

        MenuItem deviceMenuItem = new MenuItem("Deployment target");
        MenuItem artifactMenuItem = new MenuItem("Artifact");
        
        EventHandler<ActionEvent> menuEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(deviceMenuItem)){
                    DeploymentTarget newDT = new DeploymentTarget("New deployment target");
                    deployment.addDeploymentTarget(newDT);

                    //var dt2 = deploymentDiagramView.CreateDeploymentTarget();
                    //var c = deploymentDiagramView.CreateConnection(dt1, dt2);
                }
            }
        };
        
        deviceMenuItem.setOnAction(menuEventHandler);
        artifactMenuItem.setOnAction(menuEventHandler);
        addNodeMenu.getItems().addAll(deviceMenuItem, artifactMenuItem);
        
        deploymentDiagramView.addMenu(addNodeMenu);
    }
    
    private void deploymentTargetInit(DeploymentTargetView deploymentTargetView){
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            var deploymentDiagram = this.model.getDeploymentDiagram();
            deploymentDiagram.deleteDeploymentTarget(deploymentTargetView.getObjectInfo().getID());
        });
        deploymentTargetView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemAddArtifact = new MenuItem("Add artifact");

        menuItemAddArtifact.setOnAction((e) -> {
            deploymentTargetView.CreateArtifact();
        });
        deploymentTargetView.addMenuItem(menuItemAddArtifact);
    }
}
