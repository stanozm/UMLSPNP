package com.mycompany.umlspnp;

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

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var mainController = new MainController(new MainModel(), new MainView());

        var scene = new Scene(mainController.getView(), 640, 480);
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