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
    
    public DTPropertiesModalWindow(Stage parentStage, String windowName, EditableListView statesList) {
        super(parentStage, windowName);

        this.closeButton = new Button("Close");
        this.closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                close();
            }
        });
        
        this.rootGrid.add(statesList, 0, 0);
        this.rootGrid.add(closeButton, 0, 2);
    }
}
