package cz.muni.fi.umlspnp.views.common.layouts;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *  Modal window rendering the properties of an element.
 *
 */
public class PropertiesModalWindow extends ModalWindow{
    private final Button closeButton;
    
    public PropertiesModalWindow(Stage parentStage, String windowName, List<EditableListView> sections) {
        super(parentStage, windowName);

        this.closeButton = new Button("Close");
        this.closeButton.setOnAction((ActionEvent e) -> {
            close();
        });
        
        for (int i = 0; i < sections.size(); i ++){
            this.rootGrid.add(sections.get(i), i, 0);
        }
        
        this.rootGrid.add(closeButton, 0, 1);
    }
}
