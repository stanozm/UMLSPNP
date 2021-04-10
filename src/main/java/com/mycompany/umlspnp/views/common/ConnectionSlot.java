/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author 10ondr
 */
public class ConnectionSlot extends Circle{
    private final ReadOnlyDoubleProperty parentWidth;
    private final ReadOnlyDoubleProperty parentHeight;
    
    public enum LimitMovement {
        notLimited,
        onlyLeft,
        onlyRight,
        onlyTop,
        onlyBottom
    }
    
    private LimitMovement limitMovement = LimitMovement.notLimited;
    
    private double defaultRadius;
    private final double zOffset;
    
    private ConnectionSlot siblingHorizontal = null;
    private ConnectionSlot siblingVertical = null;
    
    private final BooleanProperty deletedProperty = new SimpleBooleanProperty();
    
    public ConnectionSlot(  double radius,
                            double zOffset, 
                            ReadOnlyDoubleProperty parentWidth,
                            ReadOnlyDoubleProperty parentHeight){
        this.deletedProperty.setValue(false);

        this.defaultRadius = radius;
        
        this.zOffset = zOffset;

        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        
        this.setRadius(this.defaultRadius);
        this.setFill(Color.WHITE);
        this.setStroke(Color.BLACK);
        this.setCursor(Cursor.HAND);

        this.setOnMousePressed((e) -> {
            this.toFront();
        });

        this.setOnMouseDragged((e) -> {
            if(e.getButton() == MouseButton.PRIMARY){
                Point2D p = this.localToParent(this.sceneToLocal(e.getSceneX(), e.getSceneY()));
                refreshPosition(p);
            }
            e.consume();
        });
        
        this.setOnMouseEntered((e) -> {
            this.setRadius(this.defaultRadius * 1.5);
            e.consume();
        });
        
        this.setOnMouseExited((e) -> {
            this.setRadius(this.defaultRadius);
            e.consume();
        });

        var parentDimensionsChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                refreshPosition();
            }
        };
        parentWidth.addListener(parentDimensionsChangeListener);
        parentHeight.addListener(parentDimensionsChangeListener);
    }
    
    public void moveOnEdge(double mouseX, double mouseY){
        double z = this.zOffset;

        // Is the value above first and/or second diagonal line of the rectangle
        boolean aboveFirst = isPointAboveLine(0, -z, this.parentWidth.getValue() + z, this.parentHeight.getValue(), mouseX, mouseY);
        boolean aboveSecond = isPointAboveLine(0, this.parentHeight.getValue(), this.parentWidth.getValue(), 0, mouseX, mouseY);

        if(limitMovement == LimitMovement.onlyTop || (limitMovement == LimitMovement.notLimited && aboveFirst && aboveSecond)){
            this.setTranslateX(Math.max(0, Math.min(this.parentWidth.getValue() + z, mouseX)));

            this.setTranslateY(-z);
        }
        else if(limitMovement == LimitMovement.onlyBottom || (limitMovement == LimitMovement.notLimited && !aboveFirst && !aboveSecond)){
            this.setTranslateX(Math.max(0, Math.min(this.parentWidth.getValue() + z, mouseX)));

            this.setTranslateY(this.parentHeight.getValue());
        }
        else if(limitMovement == LimitMovement.onlyRight || (limitMovement == LimitMovement.notLimited && aboveFirst)){
            this.setTranslateX(this.parentWidth.getValue() + z);

            this.setTranslateY(Math.max(0, Math.min(this.parentHeight.getValue(), mouseY)));
        }
        else{
            this.setTranslateX(0);

            this.setTranslateY(Math.max(0, Math.min(this.parentHeight.getValue(), mouseY)));
        }
    }
    
    private boolean isPointAboveLine(double x1, double y1, double x2, double y2, double pointX, double pointY){
        return (x2 - x1) * (y2 - pointY) - (y2 - y1) * (x2 - pointX) > 0;
    }

    public BooleanProperty deletedProperty(){
        return deletedProperty;
    }
    
    public void setDeleted(boolean value){
        deletedProperty.setValue(value);
    }
    
    public void refreshPosition(Point2D newPoint){
        this.moveOnEdge(newPoint.getX(), newPoint.getY());

        if(siblingHorizontal != null || siblingVertical != null) {
            var sceneTransformPoint = this.getLocalToSceneTransform();
            var siblingLocalPoint = siblingVertical.getParent().sceneToLocal(sceneTransformPoint.getTx(), sceneTransformPoint.getTy());

            if(siblingHorizontal != null)
                siblingHorizontal.moveOnEdge(siblingLocalPoint.getX(), siblingHorizontal.getTranslateY());
            if(siblingVertical != null)
                siblingVertical.moveOnEdge(siblingVertical.getTranslateX(), siblingLocalPoint.getY());
        }
    }
    
    public void refreshPosition() {
        refreshPosition(new Point2D(this.getTranslateX(), this.getTranslateY()));
    }
    
    public void setSiblingHorizontal(ConnectionSlot sibling){
        siblingHorizontal = sibling;
    }
    
    public void setSiblingVertical(ConnectionSlot sibling){
        siblingVertical = sibling;
    }
    
    public double getDefaultRadius(){
        return defaultRadius;
    }

    public double getParentWidth(){
        return parentWidth.doubleValue();
    }
    
    public double getParentHeight(){
        return parentHeight.doubleValue();
    }
    
    public void setMovementLimit(LimitMovement newLimitMovement){
        if(limitMovement != newLimitMovement){
            limitMovement = newLimitMovement;
            refreshPosition();
        }
    }

    public void disable(boolean value) {
        this.setDisable(value);
        this.setVisible(!value);
    }
}
