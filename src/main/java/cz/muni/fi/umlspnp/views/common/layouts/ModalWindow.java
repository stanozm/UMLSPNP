package cz.muni.fi.umlspnp.views.common.layouts;

import cz.muni.fi.umlspnp.common.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *  Base class for any modal window.
 *
 */
public abstract class ModalWindow extends Stage{
    protected final Stage parentStage;
    protected final Scene scene;
    protected final GridPane rootGrid;
    
    private final double rootGridOffset = 75.0;
    
    public ModalWindow(Stage parentStage, String windowName){
        this.parentStage = parentStage;
        
        this.setTitle(windowName);
        
        this.initOwner(parentStage);
        this.setResizable(false);
        this.initModality(Modality.APPLICATION_MODAL);
        
        this.rootGrid = new GridPane();
        this.rootGrid.setPadding(new Insets(5, 5, 5, 5));
        this.rootGrid.setVgap(5);
        this.rootGrid.setHgap(5);

        AnchorPane anchor = new AnchorPane();
        AnchorPane.setTopAnchor(rootGrid, 10.0);
        AnchorPane.setLeftAnchor(rootGrid, 10.0);
        anchor.getChildren().add(rootGrid);
        
        this.scene = new Scene(anchor, 0, 0);
        this.setScene(this.scene);
        
        this.rootGrid.widthProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                setWidth((double) newValue + rootGridOffset);
            }
        });
        this.rootGrid.heightProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                setHeight((double) newValue + rootGridOffset);
            }
        });
        
    }
   
   public Stage getParentStage(){
       return this.parentStage;
   }

    protected void showAlert(String errorMessage){
        var alert = Utils.createAlertDialog("Input error", "Incorrect values!", errorMessage);
        alert.showAndWait();
    }
}
