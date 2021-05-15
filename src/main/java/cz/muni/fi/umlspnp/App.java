package cz.muni.fi.umlspnp;

import cz.muni.fi.umlspnp.controllers.MainController;
import static cz.muni.fi.umlspnp.common.Utils.setStageIcon;
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
    
    @Override
    public void start(Stage stage) {
        var mainModel = new MainModel();
        var mainView = new MainView(stage);
        
        mainController = new MainController(mainModel, mainView);

        setStageIcon(getClass(), stage);
        
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