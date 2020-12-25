/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common.layouts;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author 10ondr
 */
public class StringModalWindow extends ModalWindow {
    private final Label stringLabel;
    private final TextField stringField;
    private final Button confirmButton;
    
    public StringModalWindow(Stage parentStage, String windowName, String labelText, StringProperty output) {
        super(parentStage, windowName);

        this.stringLabel = new Label(labelText);
        this.stringField = new TextField(output.getValue());
        
        this.confirmButton = new Button("Confirm");
        this.confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                output.setValue(stringField.textProperty().getValue());
                close();
            }
        });
        
        this.rootGrid.add(stringLabel, 0, 0);
        this.rootGrid.add(stringField, 1, 0);
        this.rootGrid.add(confirmButton, 0, 1);
    }
    
    public Label getLabel(){
        return this.stringLabel;
    }
    
    public TextField getTextField(){
        return this.stringField;
    }
    
    public Button getButton(){
        return this.confirmButton;
    }
}
