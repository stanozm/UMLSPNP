/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author 10ondr
 */
public class BasicRectangle extends BasicElement{
    protected Rectangle rect;
    private double originalPositionX, originalPositionY;

    protected final DoubleProperty borderOffset = new SimpleDoubleProperty(20.0);

    protected Rectangle resizeBottom;
    protected Rectangle resizeRight;
    
    private ReadOnlyDoubleProperty yMax = new SimpleDoubleProperty(Double.POSITIVE_INFINITY);
    private ReadOnlyDoubleProperty xMax = new SimpleDoubleProperty(Double.POSITIVE_INFINITY);
    private ReadOnlyDoubleProperty parentBorderOffset = new SimpleDoubleProperty(0);
    
    public BasicRectangle(double x, double y, double width, double height) {
        super(10);
        
        rect = new Rectangle(0, 0, width, height);
        this.setTranslateX(x);
        this.setTranslateY(y);
        rect.setFill(Color.gray(0.95));
        rect.setStroke(Color.BLACK);

        this.getChildren().add(rect);

        createResizeAreas(5);
        
        rect.setOnMousePressed((e) -> {
            actionElementClicked(e);
        });

        rect.setOnMouseDragged((e) -> {
            if(e.getButton() == MouseButton.PRIMARY){
                moveInGrid(e.getSceneX(), e.getSceneY());
            }
        });
    }

    private void moveInGrid(double scenePosX, double scenePosY){
        double moveX = this.getTranslateX() + getPositionInGrid(scenePosX, originalPositionX);

        if(moveX >= parentBorderOffset.getValue() && moveX <= xMax.getValue() - getWidth() - parentBorderOffset.getValue()){
            if(moveX != this.getTranslateX()){
                originalPositionX = scenePosX - ((this.getTranslateX() + (scenePosX - originalPositionX)) - moveX);
            }
            this.setTranslateX(moveX);
        }

        double moveY = this.getTranslateY() + getPositionInGrid(scenePosY, originalPositionY);
        if(moveY >= parentBorderOffset.getValue() && moveY <= yMax.getValue() - getHeight() - parentBorderOffset.getValue()){
            if(moveY != this.getTranslateY()){
                originalPositionY = scenePosY - ((this.getTranslateY() + (scenePosY - originalPositionY)) - moveY);
            }
            this.setTranslateY(moveY);
        }
    }

 
    private double getPositionInGrid(double scenePosition, double originalPosition){
        if(Math.abs(originalPosition - scenePosition) > gridSize){
            return Math.round((scenePosition - originalPosition) / gridSize) * gridSize;
        }
        return 0;
    }

    private void actionElementClicked(MouseEvent e){
        if(e.getButton() == MouseButton.PRIMARY){
            originalPositionX = e.getSceneX();
            originalPositionY = e.getSceneY();
            if(contextMenu.isShowing()){
                contextMenu.hide();
            }
        }
        else if(e.getButton() == MouseButton.SECONDARY){
            contextMenu.show(rect, e.getScreenX(), e.getScreenY());
        }
        this.toFront();
    }

    
    private void createResizeAreas(double size){
        resizeBottom = new Rectangle(0, 0, rect.getWidth(), size);
        resizeBottom.setCursor(Cursor.V_RESIZE);
        resizeBottom.setFill(Color.TRANSPARENT);
        resizeBottom.widthProperty().bind(rect.widthProperty());
        resizeBottom.xProperty().bind(rect.xProperty());
        resizeBottom.yProperty().bind(Bindings.add(rect.yProperty(), rect.heightProperty().subtract(size / 2)));
        

        resizeRight = new Rectangle(0, rect.getY(), size, rect.getHeight());
        resizeRight.setCursor(Cursor.H_RESIZE);
        resizeRight.setFill(Color.TRANSPARENT);
        resizeRight.heightProperty().bind(rect.heightProperty());
        resizeRight.xProperty().bind(Bindings.add(rect.xProperty(), rect.widthProperty().subtract(size / 2)));
        resizeRight.yProperty().bind(rect.yProperty());
        
        
        resizeRight.setOnMousePressed((e) -> {
             actionElementClicked(e);
        });

        resizeRight.setOnMouseDragged((e) -> {
            if(e.getButton() == MouseButton.PRIMARY){
                double scenePosX = e.getSceneX();
                double moveX = this.getWidth() + getPositionInGrid(scenePosX, originalPositionX);
                if(moveX >= parentBorderOffset.getValue() * 3 && this.getTranslateX() + moveX <= xMax.getValue() - parentBorderOffset.getValue()){
                    if(moveX != this.getWidth()){
                        originalPositionX = scenePosX - ((this.getWidth() + (scenePosX - originalPositionX)) - moveX);
                    }
                    
                    this.changeDimensions(moveX, this.getHeight());
                }
            }
        });
        
        resizeBottom.setOnMousePressed((e) -> {
             actionElementClicked(e);
        });

        resizeBottom.setOnMouseDragged((e) -> {
            if(e.getButton() == MouseButton.PRIMARY){
                double scenePosY = e.getSceneY();
                double moveY = this.getHeight() + getPositionInGrid(scenePosY, originalPositionY);
                if(moveY >= parentBorderOffset.getValue() * 3 && this.getTranslateY() + moveY <= yMax.getValue() - parentBorderOffset.getValue()){
                    if(moveY != this.getHeight()){
                        originalPositionY = scenePosY - ((this.getHeight() + (scenePosY - originalPositionY)) - moveY);
                    }
                    
                    this.changeDimensions(this.getWidth(), moveY);
                }
            }
        });

        this.getChildren().addAll(resizeBottom, resizeRight);
    }
    
    public void changeDimensions(double newWidth, double newHeight){
        rect.setWidth(newWidth);
        rect.setHeight(newHeight);
    }

    public double getWidth(){
        return this.rect.getWidth();
    }

    public double getHeight(){
        return this.rect.getHeight();
    }

    public DoubleProperty widthProperty(){
        return rect.widthProperty();
    }

    public DoubleProperty heightProperty(){
        return rect.heightProperty();
    }
    
    public void setFill(Color newColor){
        rect.setFill(newColor);
    }
    
    public void setMaxX(ReadOnlyDoubleProperty maxProp){
        xMax = maxProp;
    }
    
    public void setMaxY(ReadOnlyDoubleProperty maxProp){
        yMax = maxProp;
    }
    
    public void setParentBorderOffset(ReadOnlyDoubleProperty borderOffset){
        parentBorderOffset = borderOffset;
    }
}
