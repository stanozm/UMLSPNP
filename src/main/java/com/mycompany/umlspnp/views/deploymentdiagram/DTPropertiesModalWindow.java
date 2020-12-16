/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.ModalWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author 10ondr
 */
public class DTPropertiesModalWindow extends ModalWindow{
    private final Button closeButton;
    
    
    /*
    private final Button addStateButton;
    private final Button removeStateButton;
    private final Button renameStateButton;
    private final Button setDefaultStateButton;
    */
    
    public DTPropertiesModalWindow(Stage parentStage, String windowName, EditableListView statesList) {
        super(parentStage, windowName);

        this.closeButton = new Button("Close");
        this.closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                close();
            }
        });
        
        /*
        ObservableList items = FXCollections.observableArrayList("Single", "Double", "Suite", "Family App");

        
        this.addStateButton = new Button("Add");
        this.addStateButton.setMaxWidth(Double.MAX_VALUE);
        this.addStateButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        this.addStateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.err.println(states.getSelectionModel().getSelectedItem().getClass());
                items.add("New state");
            }
        });
        */
        
        /*
        this.removeStateButton = new Button("Remove");
        this.removeStateButton.setMaxWidth(Double.MAX_VALUE);
        this.removeStateButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        this.removeStateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                var selected = states.getSelectionModel().getSelectedItem();
                if(selected != null){
                    BooleanProperty confirmProperty = new SimpleBooleanProperty();
                    confirmProperty.addListener(new ChangeListener(){
                        @Override
                        public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                            if((boolean) newValue){
                                items.remove((String) selected);
                            }
                        }
                    });
                    BooleanModalWindow confirmWindow = new BooleanModalWindow((Stage) getScene().getWindow(), 
                            "Confirm", "The state \"" + (String) selected + "\" will be deleted. Proceed?", confirmProperty);
                    confirmWindow.showAndWait();
                }
            }
        });
        
        this.renameStateButton = new Button("Rename");
        this.renameStateButton.setMaxWidth(Double.MAX_VALUE);
        this.renameStateButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        */
        
        /*
        this.renameStateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                var selected = states.getSelectionModel().getSelectedItem();
                if(selected != null){
                    StringProperty stateNameProperty = new SimpleStringProperty();
                    stateNameProperty.addListener(new ChangeListener(){
                        @Override
                        public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                            
                        }
                    });
                    BooleanModalWindow confirmWindow = new BooleanModalWindow((Stage) getScene().getWindow(), 
                            "Confirm", "The state \"" + (String) selected + "\" will be deleted. Proceed?", confirmProperty);
                    confirmWindow.showAndWait();
                }
            }
        });
        */
        
        
        /*
        this.setDefaultStateButton = new Button("Set default");
        this.setDefaultStateButton.setMaxWidth(Double.MAX_VALUE);
        this.setDefaultStateButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        */
        
        /*
        this.states.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                System.err.println(ov);
            }
        });
        */
        
        //page.add(Node, colIndex, rowIndex, colSpan, rowSpan):
        
        /*
        GridPane.setHalignment(statesLabel, HPos.CENTER);
        GridPane.setHalignment(addStateButton, HPos.CENTER);
        GridPane.setHalignment(removeStateButton, HPos.CENTER);
        GridPane.setHalignment(renameStateButton, HPos.CENTER);
        GridPane.setHalignment(setDefaultStateButton, HPos.CENTER);

        this.statesGrid.add(addStateButton, 0, 2);
        this.statesGrid.add(removeStateButton, 1, 2);
        this.statesGrid.add(renameStateButton, 2, 2);
        this.statesGrid.add(setDefaultStateButton, 3, 2);
        
        ColumnConstraints c1_constraint = new ColumnConstraints();
        c1_constraint.setPercentWidth(25);
        ColumnConstraints c3_constraint = new ColumnConstraints();
        c3_constraint.setPercentWidth(25);
        this.statesGrid.getColumnConstraints().addAll(c1_constraint, c3_constraint);

        
        //this.statesGrid.setGridLinesVisible(true);
        */
        
        
        this.rootGrid.add(statesList, 0, 0);
        this.rootGrid.add(closeButton, 0, 2);
    }
    
    
}
