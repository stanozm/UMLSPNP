/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.sequencediagram;

import com.mycompany.umlspnp.views.common.Annotation;
import com.mycompany.umlspnp.views.common.AnnotationOwner;
import com.mycompany.umlspnp.views.common.ConnectionSlot;
import com.mycompany.umlspnp.views.common.ConnectionView;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 *
 * @author 10ondr
 */
public class MessageView extends ConnectionView implements AnnotationOwner{
    private final Label messageLabel;
    
    private final Annotation executionTimeAnnotation;
    
    private boolean annotationsDisplayed = true;
    
    public MessageView(int modelObjectID, ConnectionSlot source, ConnectionSlot destination, Group diagramRoot) {
        super(modelObjectID, source, destination, diagramRoot, true);
        
        messageLabel = new Label();
        
        messageLabel.translateXProperty().bind(arrow.getCenterX().subtract(messageLabel.widthProperty().divide(2)));
        messageLabel.translateYProperty().bind(arrow.getCenterY().subtract(25));
        
        this.getChildren().add(messageLabel);
        
        executionTimeAnnotation = new Annotation(20, 50, this.arrow.getCenterX(), this.arrow.getCenterY(), "Execution time");
        executionTimeAnnotation.setFill(Color.LIGHTYELLOW);
        
        annotationInit(executionTimeAnnotation);
    }
    
    public StringProperty nameProperty(){
        return messageLabel.textProperty();
    }
    
    public void setName(String newName){
        messageLabel.setText(newName);
    }
    
    public Annotation getExecutionTimeAnnotation(){
        return executionTimeAnnotation;
    }
    
    private void annotationInit(Annotation newAnnotation){
        arrow.getLine().layoutBoundsProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                var annotationLine = newAnnotation.getLine();

                var oldLineCenter = (Bounds) oldValue;
                var newLineCenter = (Bounds) newValue;
                
                annotationLine.setEndX(newLineCenter.getCenterX());
                annotationLine.setEndY(newLineCenter.getCenterY());
                
                newAnnotation.setTranslateX(newAnnotation.getTranslateX() + (newLineCenter.getCenterX() - oldLineCenter.getCenterX()));
                newAnnotation.setTranslateY(newAnnotation.getTranslateY() + (newLineCenter.getCenterY() - oldLineCenter.getCenterY()));
            }
        });

        getChildren().add(newAnnotation);
        getChildren().add(newAnnotation.getLine());
        newAnnotation.getLine().toBack();
        
        newAnnotation.setRestrictionsInParent(diagramRoot);
    }
    
    @Override
    public void setAnnotationsDisplayed(boolean value){
        if(annotationsDisplayed != value){
            annotationsDisplayed = value;

            executionTimeAnnotation.setDisplayed(value);
        }
    }

    @Override
    public boolean areAnnotationsDisplayed(){
        return annotationsDisplayed;
    }
}
