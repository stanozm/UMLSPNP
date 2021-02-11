/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common.layouts;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 *
 * @author 10ondr
 */
public class NameRateModalWindow extends ModalWindow {

    public NameRateModalWindow(Stage parentStage, String windowName) {
        super(parentStage, windowName);
    }
    
    protected boolean checkNameRateInputs(StringProperty nameProperty, StringProperty rateProperty){
        return checkNameInput(nameProperty) && checkRateInput(rateProperty);
    }
    
    protected void showAlert(String errorMessage){
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle("Input error");
        errorDialog.setHeaderText("Incorrect values!");
        errorDialog.setContentText(errorMessage);
        errorDialog.showAndWait();
    }
    
    private boolean checkNameInput(StringProperty nameProperty){        
        if(nameProperty.isEmpty().getValue()){
            showAlert("Name is not valid.");
            return false;
        }
        return true;
    }
    
    private boolean checkRateInput(StringProperty rateProperty){
        String errorMessage = null;
        
        try {
            double rate = parseRate(rateProperty);
            if(rate > 1.0 || rate < 0.0){
                errorMessage = "Rate is out of range (0.0 to 1.0).";
            }
        }
        catch(Exception e) {
            errorMessage = "Error while parsing rate value.";
        }
        
        if(errorMessage != null){
            showAlert(errorMessage);
            return false;
        }
        return true;
    }
    
    protected double parseRate(StringProperty rateProperty){
        return Double.parseDouble(rateProperty.getValue());
    }
}
