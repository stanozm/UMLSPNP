/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common.layouts;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author 10ondr
 */
public class ModalWindow extends Stage{
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
}
