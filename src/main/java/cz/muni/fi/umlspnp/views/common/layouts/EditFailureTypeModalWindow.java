package cz.muni.fi.umlspnp.views.common.layouts;

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
    protected final Label nameLabel;
    protected final TextField nameInput;
    
    protected final Label rateLabel;
    protected final TextField rateInput;
    
    protected final Button confirmButton;
    
    public EditFailureTypeModalWindow(  Stage parentStage, 
                                    String windowName,
                                    StringProperty failureName,
                                    DoubleProperty failureRate) {
        this(parentStage, windowName, failureName, failureRate, true);
    }
    
    public EditFailureTypeModalWindow(  Stage parentStage, 
                                        String windowName,
                                        StringProperty failureName,
                                        DoubleProperty failureRate,
                                        boolean build) {
        super(parentStage, windowName, true);
        

        nameLabel = new Label("Failure name:");
        this.nameInput = new TextField(failureName.getValue());
        
        rateLabel = new Label("Rate:");
        // TODO: Use TextFormatter or something as a better number input method
        this.rateInput = new TextField(failureRate.getValue().toString());
        
        confirmButton = new Button("Confirm");
        confirmButton.setOnAction((ActionEvent e) -> {
            if(checkNameRateInputs(nameInput.textProperty(), rateInput.textProperty())){
                failureName.setValue(nameInput.textProperty().getValue());
                failureRate.setValue(parseRate(rateInput.textProperty()));
                
                close();
            }
        });
        if(build) {
            this.rootGrid.add(nameLabel, 0, 0);
            this.rootGrid.add(this.nameInput, 1, 0);

            this.rootGrid.add(rateLabel, 0, 1);
            this.rootGrid.add(this.rateInput, 1, 1);

            this.rootGrid.add(confirmButton, 0, 2);
        }
    }
}
