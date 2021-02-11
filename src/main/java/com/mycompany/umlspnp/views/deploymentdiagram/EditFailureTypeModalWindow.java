/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.layouts.NameRateModalWindow;
import javafx.beans.property.DoubleProperty;
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
public class EditFailureTypeModalWindow extends NameRateModalWindow {
    private final TextField nameInput;
    private final TextField rateInput;
    
    public EditFailureTypeModalWindow(  Stage parentStage, 
                                        String windowName,
                                        StringProperty failureName,
                                        DoubleProperty failureRate) {
        super(parentStage, windowName);
        

        var nameLabel = new Label("Failure name:");
        this.nameInput = new TextField(failureName.getValue());
        
        var rateLabel = new Label("Rate:");
        // TODO: Use TextFormatter or something as a better number input method
        this.rateInput = new TextField(failureRate.getValue().toString());
        
        var confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(checkNameRateInputs(nameInput.textProperty(), rateInput.textProperty())){
                    failureName.setValue(nameInput.textProperty().getValue());
                    failureRate.setValue(parseRate(rateInput.textProperty()));

                    close();
                }
            }
        });

        this.rootGrid.add(nameLabel, 0, 0);
        this.rootGrid.add(this.nameInput, 1, 0);
        
        this.rootGrid.add(rateLabel, 0, 1);
        this.rootGrid.add(this.rateInput, 1, 1);
        
        this.rootGrid.add(confirmButton, 0, 2);
    }
}
