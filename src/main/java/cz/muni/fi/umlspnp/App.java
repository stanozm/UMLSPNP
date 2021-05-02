package cz.muni.fi.umlspnp;

import cz.muni.fi.umlspnp.controllers.MainController;
import cz.muni.fi.umlspnp.controllers.sequencediagram.SequenceDiagramController;
import cz.muni.fi.umlspnp.common.Utils;
import cz.muni.fi.umlspnp.controllers.deploymentdiagram.DeploymentDiagramController;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.views.MainView;

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

        deploymentDiagramController = new DeploymentDiagramController(mainModel, mainView);
        sequenceDiagramController = new SequenceDiagramController(mainModel, mainView);

        if(Utils.__DEBUG_CREATE_SAMPLE_DATA) {
            deploymentDiagramController.createSampleData();
            sequenceDiagramController.createSampleData();
        }

        var scene = new Scene(mainView, 640, 480);
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