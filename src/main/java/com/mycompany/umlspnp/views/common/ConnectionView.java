/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import com.mycompany.umlspnp.common.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Transform;

/**
 *
 * @author 10ondr
 */
public class ConnectionView extends BasicElement {
    protected final ConnectionSlot source;
    protected final ConnectionSlot destination;
    protected final Arrow arrow;
    
    protected final Group diagramRoot;
    
    private boolean isHovered = false;
    
    private final boolean sourceIsDestination;

    public ConnectionView(int modelObjectID, ConnectionSlot source, ConnectionSlot destination, Group diagramRoot, boolean hasArrow, boolean sourceIsDestination){
        super(modelObjectID);
        
        this.sourceIsDestination = sourceIsDestination;
        
        this.source = source;
        initSource();
        
        this.destination = destination;
        initDestination();

        this.diagramRoot = diagramRoot;
        
        this.arrow = new Arrow(hasArrow);
        
        this.arrow.setCursor(Cursor.HAND);

        this.setOnMouseEntered((e) -> {
            isHovered = true;
            this.arrow.setStrokeWidth(4);
            e.consume();
        });
        
        this.setOnMouseExited((e) -> {
            this.arrow.setStrokeWidth(1);
            e.consume();
            isHovered = false;
        });
        
        this.setOnMousePressed((e) -> {
            actionElementClicked(e);
        });
        
        this.getChildren().add(arrow);
    }
    
    private void initSource() {
        this.source.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                refreshLinePosition();
            }
        });
        
        this.source.setOnMousePressed((e) -> {
            this.toFront();
            e.consume();
        });
    }
    
    private void initDestination() {
        this.destination.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                refreshLinePosition();
            }
        });
        
        this.destination.setOnMousePressed((e) -> {
            this.toFront();
            e.consume();
        });
    }
    
    private Point2D calculatePosition(Node relativeTo, Transform newPositionTransform){
        var newLocalPosition = sceneToLocal(new Point2D(newPositionTransform.getTx(), newPositionTransform.getTy()));
        return Utils.getPositionRelativeTo(this, relativeTo, newLocalPosition);
    }
    
    public void refreshLinePosition(){
        var line = arrow.getLine();
        
        Point2D startPosition;
        Point2D endPosition = calculatePosition(diagramRoot, (Transform) destination.localToSceneTransformProperty().getValue());
        if(sourceIsDestination)
            startPosition = new Point2D(endPosition.getX() + 70, endPosition.getY());
        else
            startPosition = calculatePosition(diagramRoot, (Transform) source.localToSceneTransformProperty().getValue());
        
        double angle =  Utils.getAngle(startPosition, endPosition);

        // The line should start/end on the edge of the ConnectionSlot circle
        double sourceCircleOffsetX = source.getDefaultRadius() * Math.cos(angle);
        double sourceCircleOffsetY = source.getDefaultRadius() * Math.sin(angle);
        
        double destinationCircleOffsetX = destination.getDefaultRadius() * Math.cos(angle);
        double destinationCircleOffsetY = destination.getDefaultRadius() * Math.sin(angle);

        line.setStartX(startPosition.getX() + sourceCircleOffsetX);
        line.setStartY(startPosition.getY() + sourceCircleOffsetY);
 
        line.setEndX(endPosition.getX() - destinationCircleOffsetX);
        line.setEndY(endPosition.getY() - destinationCircleOffsetY);
    }
    
    public void removeSlots(){
        this.source.setDeleted(true);
        this.destination.setDeleted(true);
    }
    
    public Arrow getArrow(){
        return arrow;
    }
    
    private void actionElementClicked(MouseEvent e){
        if(e.getButton() == MouseButton.PRIMARY){
            if(contextMenu.isShowing()){
                contextMenu.hide();
            }
        }
        else if(e.getButton() == MouseButton.SECONDARY){
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
        }
        this.toFront();
    }
    
    public boolean getHovered(){
        return isHovered;
    }
}
