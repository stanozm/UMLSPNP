/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.sequencediagram;

import com.mycompany.umlspnp.views.common.AnnotationOwner;
import com.mycompany.umlspnp.views.common.ConnectionSlot;
import com.mycompany.umlspnp.views.common.ConnectionView;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.Label;

/**
 *
 * @author 10ondr
 */
public class MessageView extends ConnectionView implements AnnotationOwner{
    private final Label messageLabel;
    
    public MessageView(int modelObjectID, ConnectionSlot source, ConnectionSlot destination, Group diagramRoot) {
        super(modelObjectID, source, destination, diagramRoot, true);
        
        messageLabel = new Label("newMessage()");
        
        messageLabel.translateXProperty().bind(arrow.getCenterX().subtract(messageLabel.widthProperty().divide(2)));
        messageLabel.translateYProperty().bind(arrow.getCenterY().subtract(25));
        
        this.getChildren().add(messageLabel);
    }

    @Override
    public void setAnnotationsDisplayed(boolean value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean areAnnotationsDisplayed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public StringProperty nameProperty(){
        return messageLabel.textProperty();
    }
    
    public void setName(String newName){
        messageLabel.setText(newName);
    }
}
