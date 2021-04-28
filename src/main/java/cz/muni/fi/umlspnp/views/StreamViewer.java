package cz.muni.fi.umlspnp.views;

import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *  A primitive stream viewer to render the stdout and stderr in the application directly.
 *
 */
public class StreamViewer extends OutputStream {
    private final VBox container;
    private final TextArea streamView;
    private final Button clearButton;

    public StreamViewer()
    {
        this.streamView = new TextArea();
        this.streamView.setEditable(false);
        
        this.clearButton = new Button("Clear");
        
        this.clearButton.setOnAction(_unused ->  {
            streamView.clear();
        });
        
        this.container = new VBox();
        
        VBox.setVgrow(streamView, Priority.ALWAYS);
        
        this.container.getChildren().addAll(streamView, clearButton);
    }
    
    public VBox getContainer(){
        return this.container;
    }
    
    public TextArea getTextArea(){
        return this.streamView;
    }

    public Button getClearButton(){
        return this.clearButton;
    }
    
    @Override
    public void write(final int i) throws IOException {
        Platform.runLater(() -> {
            this.streamView.appendText(String.valueOf((char) i));
        });
    }
}
