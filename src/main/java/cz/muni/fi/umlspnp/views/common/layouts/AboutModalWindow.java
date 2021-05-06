package cz.muni.fi.umlspnp.views.common.layouts;

import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A modal window which show general information about the application
 *
 */
public class AboutModalWindow extends ModalWindow {
    private final Text contentView;
    
    public AboutModalWindow(Stage parentStage, String windowName, String content) {
        super(parentStage, windowName);
        
        contentView = new Text(content);
        
        var closeButton = new Button("Close");
        closeButton.setOnAction((eh) -> {
            close();
        });
        rootGrid.add(contentView, 0, 0);
        rootGrid.add(closeButton, 0, 1);
    }

}
