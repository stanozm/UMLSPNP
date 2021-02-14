/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import com.mycompany.umlspnp.views.sequencediagram.SequenceDiagramView;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentDiagramView;
import com.mycompany.umlspnp.views.common.layouts.StringModalWindow;
import com.mycompany.umlspnp.views.common.layouts.PropertiesModalWindow;
import java.io.PrintStream;
import java.util.ArrayList;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author 10ondr
 */
public class MainView extends VBox{
    private final DeploymentDiagramView deploymentDiagramView;
    private final SequenceDiagramView sequenceDiagramView;
    private final Stage appStage;
    
    
    public MainView(Stage appStage){
        this.appStage = appStage;
        
        /* Main menu */
        Menu fileMenu = new Menu("File");
        Menu aboutMenu = new Menu("About");
        aboutMenu.getItems().add(new MenuItem("Info"));
        MenuBar mainMenu = new MenuBar(fileMenu, aboutMenu);
        this.getChildren().add(mainMenu);
        
        /* Diagram tabs */
        TabPane diagramTabPane = new TabPane();
        diagramTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab deploymentDiagramTab = new Tab("Deployment Diagram");
        Tab sequenceDiagramTab = new Tab("Sequence Diagram");
        diagramTabPane.getTabs().addAll(deploymentDiagramTab, sequenceDiagramTab);

        deploymentDiagramView = new DeploymentDiagramView();
        deploymentDiagramTab.setContent(deploymentDiagramView);
        
        sequenceDiagramView = new SequenceDiagramView();
        sequenceDiagramTab.setContent(sequenceDiagramView);

        /* Console output tabs */
        TabPane debugTabPane = new TabPane();
        debugTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab stdoutTab = new Tab("Stdout");
        Tab stderrTab = new Tab("Stderr");
        
        /* Stdout */
        StreamViewer stdoutViewer = new StreamViewer();
        PrintStream stdoutPrintStream = new PrintStream(stdoutViewer, true);
        System.setOut(stdoutPrintStream);
        stdoutTab.setContent(stdoutViewer.getContainer());
        
        /* Stderr */
        StreamViewer stderrViewer = new StreamViewer();
        PrintStream stderrPrintStream = new PrintStream(stderrViewer, true);
        System.setErr(stderrPrintStream);
        stderrTab.setContent(stderrViewer.getContainer());
        stderrViewer.getTextArea().setStyle("-fx-text-fill: red;");

        debugTabPane.getTabs().addAll(stderrTab, stdoutTab);
        
        /* Vertical split */
        SplitPane splitPane = new SplitPane();
        splitPane.orientationProperty().setValue(Orientation.VERTICAL);
        splitPane.setDividerPosition(0, 0.75);

        splitPane.getItems().addAll(diagramTabPane, debugTabPane);
        
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        
        this.getChildren().add(splitPane);
    }
 
    public Stage getAppStage(){
        return appStage;
    }
    
    public DeploymentDiagramView getDeploymentDiagramView(){
        return deploymentDiagramView;
    }
    
    public SequenceDiagramView getSequenceDiagramView(){
        return sequenceDiagramView;
    }
    
    public void createStringModalWindow(String windowName, String labelText, StringProperty output){
        var modal = new StringModalWindow(appStage, windowName, labelText, output);
        modal.showAndWait();
    }
    
    public void createPropertiesModalWindow(String windowName, ArrayList sections){
        var modal = new PropertiesModalWindow(appStage, windowName, sections);
        modal.showAndWait();
    }
}