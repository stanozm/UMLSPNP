package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.layouts.NameRateModalWindow;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *  Modal window which edits link types of the communication link.
 * It manages a global list of link types one of which can be selected for a 
 * communication link.
 *
 */
public class EditAllLinkTypesModalWindow extends NameRateModalWindow {
    private final TextField nameInput;
    private final TextField rateInput;
    
    public EditAllLinkTypesModalWindow( Stage parentStage, 
                                        String windowName,
                                        StringProperty linkName,
                                        DoubleProperty transferRate) {
        super(parentStage, windowName, false);
        

        var nameLabel = new Label("Link type name:");
        this.nameInput = new TextField(linkName.getValue());
        
        var rateLabel = new Label("Transfer rate:");
        this.rateInput = new TextField(transferRate.getValue().toString());
        
        var confirmButton = new Button("Confirm");
        confirmButton.setOnAction((ActionEvent e) -> {
            if(checkNameRateInputs(nameInput.textProperty(), rateInput.textProperty())){
                linkName.setValue(nameInput.textProperty().getValue());
                transferRate.setValue(parseRate(rateInput.textProperty()));
                
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
