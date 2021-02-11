/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.layouts.NameRateModalWindow;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author 10ondr
 */
public class EditTransitionModalWindow extends NameRateModalWindow {
    private final ComboBox fromStateInput;
    private final ComboBox toStateInput;
    private final TextField nameInput;
    private final TextField rateInput;
    
    public EditTransitionModalWindow(   Stage parentStage, 
                                        String windowName,
                                        StringProperty transitionName,
                                        DoubleProperty transitionRate,
                                        ObjectProperty fromState,
                                        ObjectProperty toState,
                                        ObservableList states   ) {
        super(parentStage, windowName);
        
        var fromLabel = new Label("From state:");
        this.fromStateInput = new ComboBox(states);
        setDefaultFromState(fromState.getValue());
        
        var toLabel = new Label("To state:");
        this.toStateInput = new ComboBox(states);
        setDefaultToState(toState.getValue());
        
        var nameLabel = new Label("Transition name:");
        this.nameInput = new TextField(transitionName.getValue());
        
        var rateLabel = new Label("Rate:");
        // TODO: Use TextFormatter or something as a better number input method
        this.rateInput = new TextField(transitionRate.getValue().toString());
        
        var confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(checkInputs()){
                    transitionName.setValue(nameInput.textProperty().getValue());
                    transitionRate.setValue(parseRate());
                    fromState.setValue(fromStateInput.getSelectionModel().getSelectedItem());
                    toState.setValue(toStateInput.getSelectionModel().getSelectedItem());

                    close();
                }
            }
        });
        
        this.rootGrid.add(fromLabel, 0, 0);
        this.rootGrid.add(this.fromStateInput, 1, 0);
        
        this.rootGrid.add(toLabel, 0, 1);
        this.rootGrid.add(this.toStateInput, 1, 1);
        
        this.rootGrid.add(nameLabel, 0, 2);
        this.rootGrid.add(this.nameInput, 1, 2);
        
        this.rootGrid.add(rateLabel, 0, 3);
        this.rootGrid.add(this.rateInput, 1, 3);
        
        this.rootGrid.add(confirmButton, 0, 4);
    }
    
    private boolean checkInputs(){
        return checkNameRateInputs(nameInput.textProperty(), rateInput.textProperty()) && checkStatesInput();
    }
    
    private boolean checkStatesInput(){
        String errorMessage = null;
        if(this.fromStateInput.getSelectionModel().getSelectedItem() == this.toStateInput.getSelectionModel().getSelectedItem()){
            errorMessage = "States \"From\" and \"To\" must differ.";
        }
        else if(this.fromStateInput.getSelectionModel().getSelectedItem() == null){
            errorMessage = "State \"From\" is not valid.";
        }
        else if(this.toStateInput.getSelectionModel().getSelectedItem() == null){
            errorMessage = "State \"To\" is not valid.";
        }
        
        if(errorMessage != null){
            showAlert(errorMessage);
            return false;
        }
        return true;
    }
    
    private void setDefaultToState(Object toState){
        int index = this.toStateInput.getItems().indexOf(toState);
        if(index > -1){
            this.toStateInput.getSelectionModel().select(index);
        }
    }
    
    private void setDefaultFromState(Object fromState){
        int index = this.fromStateInput.getItems().indexOf(fromState);
        if(index > -1){
            this.fromStateInput.getSelectionModel().select(index);
        }
    }
    
    private double parseRate(){
        return Double.parseDouble(this.rateInput.textProperty().getValue());
    }
}
