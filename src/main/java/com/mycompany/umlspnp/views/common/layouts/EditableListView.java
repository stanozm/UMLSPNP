/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common.layouts;

import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 *
 * @author 10ondr
 */
public class EditableListView extends GridPane {    
    private final Label headerLabel;
    private final ListView itemList;
    
    private final ArrayList<Button> buttons = new ArrayList();
    
    public EditableListView(String header, ObservableList items){
        this.setPadding(new Insets(2, 2, 2, 2));
        this.setHgap(2);
        this.setVgap(2);

        this.headerLabel = new Label(header);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        
        this.itemList = new ListView();
        
        this.itemList.setItems(items);
        this.itemList.setMaxHeight(200);
        
        this.add(headerLabel, 0, 0);
        this.add(itemList, 0, 1);
    }
    
    public void addButton(Button newButton){
        buttons.add(newButton);
        GridPane.setHalignment(newButton, HPos.CENTER);

        int btnCount = buttons.size();
        GridPane.setColumnSpan(headerLabel, btnCount);
        GridPane.setColumnSpan(itemList, btnCount);
        
        this.getColumnConstraints().removeAll(this.getColumnConstraints());
        
        for(int i = 0; i < btnCount; i++){
            ColumnConstraints col_constraint = new ColumnConstraints();
            col_constraint.setPercentWidth((int)(100 / btnCount));
            this.getColumnConstraints().add(col_constraint);
        }
        this.add(newButton, btnCount - 1, 2);
    }
    
    public Button createButton(String buttonName, EventHandler<ActionEvent> eventHandler, boolean enabledOnSelection){
        Button newbtn = new Button(buttonName);
        newbtn.setMaxWidth(Double.MAX_VALUE);
        newbtn.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        
        newbtn.setOnAction(eventHandler);
        
        if(enabledOnSelection)
            newbtn.disableProperty().bind(Bindings.isNull(itemList.getSelectionModel().selectedItemProperty()));
        
        this.addButton(newbtn);
        return newbtn;
    }
    
    public Object getSelected(){
        return this.itemList.getSelectionModel().getSelectedItem();
    }
    
    public void refresh(){
        this.itemList.refresh();
    }
}