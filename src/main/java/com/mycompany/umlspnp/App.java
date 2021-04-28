package com.mycompany.umlspnp;

import com.mycompany.umlspnp.controllers.deploymentdiagram.DeploymentDiagramController;
import com.mycompany.umlspnp.controllers.*;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.views.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {
    private MainController mainController;
    private DeploymentDiagramController deploymentDiagramController;
    private SequenceDiagramController sequenceDiagramController;
    
    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var mainModel = new MainModel();
        var mainView = new MainView(stage);
        
        mainController = new MainController(mainModel, mainView);
        mainController.init();
        deploymentDiagramController = new DeploymentDiagramController(mainModel, mainView);
        sequenceDiagramController = new SequenceDiagramController(mainModel, mainView);
        
        // TODO: remove
        deploymentDiagramController.createSampleData();
        sequenceDiagramController.createSampleData();

        var scene = new Scene(deploymentDiagramController.getView(), 640, 480);
        stage.setTitle("UML2SPNP");
        stage.setScene(scene);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}