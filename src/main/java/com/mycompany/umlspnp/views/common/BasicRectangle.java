/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    private final DoubleExpression centerX;
    private final DoubleExpression centerY;
    
    protected final DoubleProperty borderOffset = new SimpleDoubleProperty(20.0);
    
    protected Rectangle resizeBottom;
    protected Rectangle resizeRight;

    public BasicRectangle(int modelObjectID, double x, double y, double width, double height) {
        super(modelObjectID);
        
        rect = new Rectangle(0, 0, width, height);
        this.setTranslateX(x);
        this.setTranslateY(y);
        rect.setFill(Color.gray(0.95));
        rect.setStroke(Color.BLACK);

        this.getChildren().add(rect);

        centerX = this.translateXProperty().add(this.widthProperty().divide(2));
        centerY = this.translateYProperty().add(this.heightProperty().divide(2));
        
        createResizeAreas(5);
        
        this.setOnMousePressed((e) -> {
            actionElementClicked(e);
            e.consume();
        });

        this.setOnMouseDragged((e) -> {
            if(e.getButton() == MouseButton.PRIMARY){
                moveInGrid(e.getSceneX(), e.getSceneY());
            }
            e.consume();
        });
    }

    public DoubleExpression getCenterX(){
        return this.centerX;
    }
    
    public DoubleExpression getCenterY(){
        return this.centerY;
    }
    
    private void moveInGrid(double scenePosX, double scenePosY){
        double moveX = this.getTranslateX() + getPositionInGrid(scenePosX, originalPositionX);

        if(moveX != this.getTranslateX()){
            originalPositionX = scenePosX - ((this.getTranslateX() + (scenePosX - originalPositionX)) - moveX);
        }
        this.setTranslateX(moveX);


        double moveY = this.getTranslateY() + getPositionInGrid(scenePosY, originalPositionY);
        if(moveY != this.getTranslateY()){
            originalPositionY = scenePosY - ((this.getTranslateY() + (scenePosY - originalPositionY)) - moveY);
        }
        this.setTranslateY(moveY);
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
                if(moveX != this.getWidth()){
                    originalPositionX = scenePosX - ((this.getWidth() + (scenePosX - originalPositionX)) - moveX);
                }

                this.changeDimensions(moveX, this.getHeight());
            }
        });
        
        resizeBottom.setOnMousePressed((e) -> {
             actionElementClicked(e);
        });

        resizeBottom.setOnMouseDragged((e) -> {
            if(e.getButton() == MouseButton.PRIMARY){
                double scenePosY = e.getSceneY();
                double moveY = this.getHeight() + getPositionInGrid(scenePosY, originalPositionY);
                if(moveY != this.getHeight()){
                    originalPositionY = scenePosY - ((this.getHeight() + (scenePosY - originalPositionY)) - moveY);
                }

                this.changeDimensions(this.getWidth(), moveY);
            }
        });

        this.getChildren().addAll(resizeBottom, resizeRight);
    }
    
    public void setRestrictionsInParent(BasicRectangle parent){
        this.widthProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if((double) newValue > (double) oldValue){
                    if(parent != null){
                        double newParentWidth = (double) newValue + getTranslateX() + parent.borderOffset.getValue();
                        if(newParentWidth > parent.getWidth()){
                            parent.changeDimensions(newParentWidth, parent.getHeight());
                        }
                    }
                }
                else if((double) newValue < 30){
                    changeDimensions((double) oldValue, getHeight());
                }
            }
        });

        this.heightProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if((double) newValue > (double) oldValue){
                    if(parent != null){
                        double newParentHeight = (double) newValue + getTranslateY() + parent.borderOffset.getValue();
                        if(newParentHeight > parent.getHeight()){
                            parent.changeDimensions(parent.getWidth(), newParentHeight);
                        }
                    }
                }
                else if((double) newValue < 30){
                   changeDimensions(getWidth(), (double) oldValue);
                }
            }
        });

        this.translateXProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(parent != null){
                    if((double) newValue + getWidth() > parent.getWidth() - parent.borderOffset.getValue()){
                        setTranslateX((double) oldValue);
                    }
                }
                double minVal;
                if(parent != null)
                    minVal = parent.borderOffset.getValue();
                else
                    minVal = 0;

                if((double) newValue < minVal){
                    setTranslateX(minVal);
                }
            }
        });

        this.translateYProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(parent != null){
                    if((double) newValue + getHeight() > parent.getHeight() - parent.borderOffset.getValue()){
                        setTranslateY((double) oldValue);
                    }
                }

                double minVal;
                if(parent != null)
                    minVal = parent.borderOffset.getValue();
                else
                    minVal = 0;
                if((double) newValue < minVal){
                    setTranslateY(minVal);
                }
            }
        });
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
}
