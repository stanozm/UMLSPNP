/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.ModalWindow;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 *
 * @author 10ondr
 */
public class EditOperationModalWindow extends ModalWindow {
    private final ComboBox stateInput = new ComboBox();
    private EditableListView operationsInput;

    
    public EditOperationModalWindow(   Stage parentStage, 
                                        String windowName,
                                        ObjectProperty state,
                                        ObservableList states,
                                        EditableListView operationEntriesView ) {
        super(parentStage, windowName);

        var stateLabel = new Label("State:");
        
        var availableStatesList = FXCollections.observableArrayList(states);
        availableStatesList.add(state.getValue());
        stateInput.setItems(availableStatesList);
        
        setDefaultState(state.getValue());

        this.operationsInput = operationEntriesView;
        
        var confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                var selected = stateInput.getSelectionModel().getSelectedItem();
                if(selected != null)
                    state.setValue(selected);
                close();
            }
        });
        
        this.rootGrid.add(stateLabel, 0, 0);
        this.rootGrid.add(this.stateInput, 0, 1);
        
        this.rootGrid.add(operationsInput, 0, 2);
        GridPane.setVgrow(operationsInput, Priority.ALWAYS);
        
        this.rootGrid.add(confirmButton, 0, 3);
        
        // TODO: make responsive
        this.setWidth(300);
        this.setHeight(400);
    }

    private void setDefaultState(Object state){
        int index = this.stateInput.getItems().indexOf(state);
        if(index > -1){
            this.stateInput.getSelectionModel().select(index);
        }
    }
}
