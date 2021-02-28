/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common.layouts;

import javafx.beans.property.IntegerProperty;
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
public class IntegerModalWindow extends ModalWindow {
    private final Label stringLabel;
    private final TextField stringField;
    private final Button confirmButton;
    
    private final Integer min;
    private final Integer max;
    
    public IntegerModalWindow(Stage parentStage, String windowName, String labelText, Integer min, Integer max, IntegerProperty output) {
        super(parentStage, windowName);

        this.min = min;
        this.max = max;
        
        this.stringLabel = new Label(labelText);
        this.stringField = new TextField(output.getValue().toString());
        
        this.confirmButton = new Button("Confirm");
        this.confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(checkInput()){
                    output.setValue(parseInteger(stringField.textProperty()));
                    close();
                }
            }
        });
        
        this.rootGrid.add(stringLabel, 0, 0);
        this.rootGrid.add(stringField, 1, 0);
        this.rootGrid.add(confirmButton, 0, 1);
    }
    
    public Label getLabel(){
        return this.stringLabel;
    }
    
    private boolean checkInput(){
        String errorMessage = null;
        
        try {
            int value = parseInteger(this.stringField.textProperty());
            if(min != null && value < min.intValue()){
                errorMessage = "Value is smaller than minimum (" + min.toString() + ").";
            }
            else if(max != null && value > max.intValue()){
                errorMessage = "Value is larger than maximum (" + max.toString() + ").";
            }
        }
        catch(Exception e) {
            errorMessage = "Error while parsing value.";
        }
        
        if(errorMessage != null){
            showAlert(errorMessage);
            return false;
        }
        return true;
    }
    
    protected int parseInteger(StringProperty stringProperty){
        return Integer.parseInt(stringProperty.getValue());
    }
}
