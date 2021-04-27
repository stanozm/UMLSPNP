package com.mycompany.umlspnp.views.common.layouts;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * A modal window which edits a failure type with a name and rate property.
 *
 */
public class EditFailureTypeModalWindow extends NameRateModalWindow {
    private final TextField nameInput;
    private final TextField rateInput;
    
    public EditFailureTypeModalWindow(  Stage parentStage, 
                                        String windowName,
                                        StringProperty failureName,
                                        DoubleProperty failureRate) {
        super(parentStage, windowName, true);
        

        var nameLabel = new Label("Failure name:");
        this.nameInput = new TextField(failureName.getValue());
        
        var rateLabel = new Label("Rate:");
        // TODO: Use TextFormatter or something as a better number input method
        this.rateInput = new TextField(failureRate.getValue().toString());
        
        var confirmButton = new Button("Confirm");
        confirmButton.setOnAction((ActionEvent e) -> {
            if(checkNameRateInputs(nameInput.textProperty(), rateInput.textProperty())){
                failureName.setValue(nameInput.textProperty().getValue());
                failureRate.setValue(parseRate(rateInput.textProperty()));
                
                close();
            }
        });

        this.rootGrid.add(nameLabel, 0, 0);
        this.rootGrid.add(this.nameInput, 1, 0);
        
        this.rootGrid.add(rateLabel, 0, 1);
        this.rootGrid.add(this.rateInput, 1, 1);
        
        this.rootGrid.add(confirmButton, 0, 2);
    }
}
