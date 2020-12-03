/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author 10ondr
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
        
        this.clearButton.setOnAction(actionEvent ->  {
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
