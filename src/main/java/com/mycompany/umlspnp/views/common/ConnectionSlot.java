/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    
    private double defaultRadius;
    private final ReadOnlyDoubleProperty zOffset;
    
    private final BooleanProperty deletedProperty = new SimpleBooleanProperty();
    
    public ConnectionSlot(  double radius,
                            ReadOnlyDoubleProperty zOffset, 
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
                this.moveOnEdge(p);
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
    }
    
    private void moveOnEdge(Point2D mousePosition){
        double mouseX = mousePosition.getX();
        double mouseY = mousePosition.getY();

        double z = this.zOffset.doubleValue();

        // Is the value above first and/or second diagonal line of the rectangle
        boolean aboveFirst = isPointAboveLine(0, -z, this.parentWidth.getValue() + z, this.parentHeight.getValue(), mouseX, mouseY);
        boolean aboveSecond = isPointAboveLine(0, this.parentHeight.getValue(), this.parentWidth.getValue(), 0, mouseX, mouseY);

        if(aboveFirst && aboveSecond){
            this.setTranslateX(Math.max(0, Math.min(this.parentWidth.getValue() + z, mouseX)));

            this.setTranslateY(-z);
        }
        else if(!aboveFirst && !aboveSecond){
            this.setTranslateX(Math.max(0, Math.min(this.parentWidth.getValue() + z, mouseX)));

            this.setTranslateY(this.parentHeight.getValue());
        }
        else if(aboveFirst){
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
    
    public void refreshPosition(){
        Point2D p = new Point2D(this.getTranslateX(), this.getTranslateY());
        this.moveOnEdge(p);
    }
}
