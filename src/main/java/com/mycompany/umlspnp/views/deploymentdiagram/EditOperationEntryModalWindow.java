package com.mycompany.umlspnp.views.deploymentdiagram;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *  Modal window which edits operation entries of the supported operation
 * in deployment target.
 *
 */
public class EditOperationEntryModalWindow extends SelectOperationEntryModalWindow{
    private final TextField speedInput;
    private final CheckBox speedEnabled = new CheckBox();

    public EditOperationEntryModalWindow(Stage parentStage,
                                         String windowName,
                                         ObservableList allOperationTypes,
                                         ObjectProperty operationType,
                                         IntegerProperty entryProcessingSpeed) {
        super(parentStage, windowName, allOperationTypes, false);

        operationEntry.getSelectionModel().select(operationType.getValue());

        var speedEnabledLabel = new Label("Limit processing speed");
  
        var speedLabel = new Label("Processing speed [%]:");
        
        Integer speedLimit = entryProcessingSpeed.getValue();
        if(speedLimit < 0){
            this.speedEnabled.setSelected(false);
            this.speedInput = new TextField("100");
        }
        else{
            this.speedEnabled.setSelected(true);
            this.speedInput = new TextField(speedLimit.toString());
        }
        speedLabel.visibleProperty().bind(this.speedEnabled.selectedProperty());
        this.speedInput.visibleProperty().bind(this.speedEnabled.selectedProperty());

        this.confirmButton.setOnAction((ActionEvent e) -> {
            if(checkInputs()){
                operationType.setValue(operationEntry.getSelectionModel().getSelectedItem());
                entryProcessingSpeed.setValue(parseProcessingSpeedValue());
                close();
            }
        });

        this.rootGrid.add(nameLabel, 0, 0);
        this.rootGrid.add(operationEntry, 1, 0);
        
        this.rootGrid.add(speedEnabled, 0, 1);
        this.rootGrid.add(speedEnabledLabel, 1, 1);
        
        this.rootGrid.add(speedLabel, 0, 2);
        this.rootGrid.add(speedInput, 1, 2);
        
        this.rootGrid.add(confirmButton, 0, 3);
    }
    
    private Integer parseProcessingSpeedValue(){
        if(this.speedEnabled.isSelected())
            return Integer.parseInt(speedInput.textProperty().getValue());
        return -1;
    }

    @Override
    protected boolean checkInputs(){
        if(super.checkInputs() == false)
            return false;

        String errorMessage = null;
        
        try {
            int speedLimit = parseProcessingSpeedValue();
            if(speedEnabled.isSelected() && (speedLimit > 100 || speedLimit < 0)){
                errorMessage = "Speed limit is out of range (0% to 100%).";
            }
        }
        catch(Exception e) {
            errorMessage = "Error while parsing speed limit value.";
        }
        
        if(errorMessage != null){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Input error");
            errorDialog.setHeaderText("Incorrect values!");
            errorDialog.setContentText(errorMessage);
            errorDialog.showAndWait();
            return false;
        }
        return true;
    }
    
}
