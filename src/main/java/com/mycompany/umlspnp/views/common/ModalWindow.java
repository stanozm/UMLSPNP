/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author 10ondr
 */
public class ModalWindow extends Stage{
    protected final Stage parentStage;
    protected final Scene scene;
    protected final GridPane rootGrid;
    
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

        this.scene = new Scene(rootGrid, 0, 0);
        this.setScene(this.scene);
    }
   
   public Stage getParentStage(){
       return this.parentStage;
   }
}
