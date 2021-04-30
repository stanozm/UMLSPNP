package cz.muni.fi.umlspnp.views.sequencediagram;

import cz.muni.fi.umlspnp.views.common.layouts.EditFailureTypeModalWindow;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * A modal window which edits a message failure type with a name, rate and the
 * hw node fail properties.
 *
 */
public class EditMessageFailureTypeModalWindow extends EditFailureTypeModalWindow {
    private final Label causeHWFailLabel;
    private final CheckBox HWFailCheckBox;

    public EditMessageFailureTypeModalWindow(Stage parentStage,
                                             String windowName,
                                             StringProperty failureName,
                                             DoubleProperty failureRate,
                                             BooleanProperty causeHWFail) {
        super(parentStage, windowName, failureName, failureRate, false);
        
        causeHWFailLabel = new Label("Causes node failure:");
        HWFailCheckBox = new CheckBox();
        HWFailCheckBox.selectedProperty().bindBidirectional(causeHWFail);
        
        this.rootGrid.add(nameLabel, 0, 0);
        this.rootGrid.add(nameInput, 1, 0);

        this.rootGrid.add(rateLabel, 0, 1);
        this.rootGrid.add(rateInput, 1, 1);

        this.rootGrid.add(causeHWFailLabel, 0, 2);
        this.rootGrid.add(HWFailCheckBox, 1, 2);

        this.rootGrid.add(confirmButton, 0, 3);
    }
    
}
