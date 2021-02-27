/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 *
 * @author 10ondr
 */
public class Box extends NamedRectangle{
    private final Polygon polygonTop;
    private final Polygon polygonRight;
    private final DoubleProperty zOffset;

    public Box(double x, double y, double width, double height, double zOffset, String name, int modelObjectID){
        super(x, y, width, height, name, modelObjectID);
        
        this.zOffset = new SimpleDoubleProperty(zOffset);

        polygonTop = new Polygon();
        polygonTop.setFill(Color.WHITE);
        polygonTop.setStroke(Color.BLACK);
        
        polygonRight = new Polygon();
        polygonRight.setFill(Color.WHITE);
        polygonRight.setStroke(Color.BLACK);

        this.setBoxZDimensions();
        
        this.getChildren().addAll(polygonTop, polygonRight);
        polygonTop.toBack();
        polygonRight.toBack();
    }
    
    private void setBoxZDimensions(){
        double r_x = rect.getX();
        double r_y = rect.getY();
        double r_width = rect.getWidth();
        double r_height = rect.getHeight();

        double z = this.zOffset.doubleValue();
        
        polygonTop.getPoints().clear();
        polygonTop.getPoints().addAll(new Double[]{
            r_x, r_y,
            r_x + z, r_y - z,
            r_x + r_width + z, r_y - z,
            r_x + r_width, r_y
        });
        
        polygonRight.getPoints().clear();
        polygonRight.getPoints().addAll(new Double[]{
            r_x + r_width, r_y,
            r_x + r_width + z, r_y - z,
            r_x + r_width + z, r_y + r_height - z,
                
            r_x + r_width, r_y + r_height,
        });
    }

    @Override
    public void changeDimensions(double newWidth, double newHeight){
        super.changeDimensions(newWidth, newHeight);
        
        setBoxZDimensions();
    }

    public DoubleProperty getZOffset(){
        return zOffset;
    }
    
    public void setZOffset(double value){
        zOffset.set(value);
        setBoxZDimensions();
    }
    
    @Override
    public void setStroke(Color newColor) {
        super.setStroke(newColor);
        polygonTop.setStroke(newColor);
        polygonRight.setStroke(newColor);
    }
}
