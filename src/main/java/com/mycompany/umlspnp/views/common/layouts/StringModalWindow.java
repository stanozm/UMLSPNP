package com.mycompany.umlspnp.views.common.layouts;

import java.util.regex.Pattern;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * A modal window which edits a string value.
 *
 */
public class StringModalWindow extends ModalWindow {
    private final Label stringLabel;
    private final TextField stringField;
    private final Button confirmButton;
    
    private String restrictionRegex = null;
    
    public StringModalWindow(Stage parentStage, String windowName, String labelText, StringProperty output) {
        super(parentStage, windowName);

        this.stringLabel = new Label(labelText);
        this.stringField = new TextField(output.getValue());
        
        this.confirmButton = new Button("Confirm");
        this.confirmButton.setOnAction((ActionEvent e) -> {
            if(checkInput()) {
                output.setValue(stringField.textProperty().getValue());
                close();
            }
        });
        
        this.rootGrid.add(stringLabel, 0, 0);
        this.rootGrid.add(stringField, 1, 0);
        this.rootGrid.add(confirmButton, 0, 1);
    }
    
    private boolean checkInput(){
        String errorMessage = null;
        
        if(restrictionRegex != null) {
            var regex = Pattern.compile(restrictionRegex);
            if(!regex.matcher(this.stringField.textProperty().getValue()).matches()) {
                errorMessage = "The value must start with a letter and contain only english letters, numbers, whitespace and underscore.";
            }
        }
        
        if(errorMessage != null){
            showAlert(errorMessage);
            return false;
        }
        return true;
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
    
    public void setStringRestrictionRegex(String regex){
        restrictionRegex = regex;
    }
}
