package cz.muni.fi.umlspnp.views.deploymentdiagram;

import cz.muni.fi.umlspnp.views.common.layouts.EditableListView;
import cz.muni.fi.umlspnp.views.common.layouts.ModalWindow;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 *  Modal window which edits operation the supported operation
 * in deployment target.
 * 
 */
public class EditOperationModalWindow extends ModalWindow {
    private final ComboBox stateInput = new ComboBox();
    private final EditableListView operationsInput;

    
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
        confirmButton.setOnAction((ActionEvent e) -> {
            var selected = stateInput.getSelectionModel().getSelectedItem();
            if(selected != null)
                state.setValue(selected);
            close();
        });
        
        this.rootGrid.add(stateLabel, 0, 0);
        this.rootGrid.add(this.stateInput, 0, 1);
        
        this.rootGrid.add(operationsInput, 0, 2);
        GridPane.setVgrow(operationsInput, Priority.ALWAYS);
        
        this.rootGrid.add(confirmButton, 0, 3);
    }

    private void setDefaultState(Object state){
        int index = this.stateInput.getItems().indexOf(state);
        if(index > -1){
            this.stateInput.getSelectionModel().select(index);
        }
    }
}
