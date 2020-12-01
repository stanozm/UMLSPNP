/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author 10ondr
 */
public class MainView extends VBox{
    private final DeploymentDiagramView deploymentDiagramView;
    private final SequenceDiagramView sequenceDiagramView;
    
    public MainView(){
        /* Menu */
        Menu fileMenu = new Menu("File");
        Menu aboutMenu = new Menu("About");
        aboutMenu.getItems().add(new MenuItem("Info"));
        MenuBar mainMenu = new MenuBar(fileMenu, aboutMenu);
        this.getChildren().add(mainMenu);

        /* Tabs */
        TabPane tabpane = new TabPane();
        tabpane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab deploymentDiagramTab = new Tab("Deployment Diagram");
        Tab sequenceDiagramTab = new Tab("Sequence Diagram");
        tabpane.getTabs().addAll(deploymentDiagramTab, sequenceDiagramTab);
        this.getChildren().add(tabpane);

        HBox.setHgrow(tabpane, Priority.ALWAYS);
        VBox.setVgrow(tabpane, Priority.ALWAYS);

        deploymentDiagramView = new DeploymentDiagramView();
        deploymentDiagramTab.setContent(deploymentDiagramView);
        
        sequenceDiagramView = new SequenceDiagramView();
        sequenceDiagramTab.setContent(sequenceDiagramView);
    }
    
    public DeploymentDiagramView getDeploymentDiagramView(){
        return deploymentDiagramView;
    }
    
    public SequenceDiagramView getSequenceDiagramView(){
        return sequenceDiagramView;
    }
}
