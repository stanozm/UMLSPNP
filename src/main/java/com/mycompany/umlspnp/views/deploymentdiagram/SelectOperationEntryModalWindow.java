package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.layouts.ModalWindow;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public abstract class SelectOperationEntryModalWindow extends ModalWindow{
    protected final Label nameLabel;
    protected final ComboBox operationEntry = new ComboBox();
    
    protected final Button confirmButton;
    
    public SelectOperationEntryModalWindow( Stage parentStage,
                                            String windowName,
                                            ObservableList allOperationTypes,
                                            boolean build) {
        super(parentStage, windowName);
        nameLabel = new Label("Select operation type:");

        operationEntry.setItems(allOperationTypes);

        this.confirmButton = new Button("Confirm");
        this.confirmButton.setOnAction((ActionEvent e) -> {
            if(checkInputs()){
                close();
            }
        });

        if(build) {
            this.rootGrid.add(nameLabel, 0, 0);
            this.rootGrid.add(operationEntry, 1, 0);

            this.rootGrid.add(confirmButton, 0, 3);
        }
    }
    
    public SelectOperationEntryModalWindow( Stage parentStage,
                                            String windowName,
                                            ObservableList allOperationTypes) {
        this(parentStage, windowName, allOperationTypes, true);
    }

    protected boolean checkInputs(){
        if(getSelected() == null){
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Input error");
            errorDialog.setHeaderText("Incorrect values!");
            errorDialog.setContentText("Please select valid operation type from the list");
            errorDialog.showAndWait();
            return false;
        }
        return true;
    }
    
    public Object getSelected() {
        return operationEntry.getSelectionModel().getSelectedItem();
    }
}
