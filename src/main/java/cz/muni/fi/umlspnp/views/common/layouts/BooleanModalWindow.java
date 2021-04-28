package cz.muni.fi.umlspnp.views.common.layouts;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *  A modal window which edits a boolean value.
 *
 */
public class BooleanModalWindow extends ModalWindow{
    private final Label stringLabel;
    private final Button yesButton;
    private final Button noButton;
    
    private boolean result;
    
    public BooleanModalWindow(Stage parentStage, String windowName, String labelText) {
        super(parentStage, windowName);

        this.stringLabel = new Label(labelText);
        
        this.yesButton = new Button("Yes");
        this.yesButton.setOnAction((ActionEvent e) -> {
            result = true;
            close();
        });    
        
        this.noButton = new Button("No");
        this.noButton.setOnAction((ActionEvent e) -> {
            result = false;
            close();
        });
        
        this.rootGrid.add(stringLabel, 0, 0, 2, 1);
        this.rootGrid.add(yesButton, 0, 1);
        this.rootGrid.add(noButton, 1, 1);
    }
    
    public BooleanModalWindow(Stage parentStage, String windowName, String labelText, BooleanProperty output) {
        this(parentStage, windowName, labelText);

        this.yesButton.setOnAction((ActionEvent e) -> {
            result = true;
            output.setValue(true);
            close();
        });    
        this.noButton.setOnAction((ActionEvent e) -> {
            result = false;
            output.setValue(false);
            close();
        });
    }
    
    public Label getLabel(){
        return this.stringLabel;
    }
    
    public boolean getResult(){
        return result;
    }
}
