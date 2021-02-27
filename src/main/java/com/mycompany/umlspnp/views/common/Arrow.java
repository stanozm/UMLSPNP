/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.shape.Line;

/**
 *
 * @author 10ondr
 */
public class Arrow extends Group {
    private final Line line;
    
    private final Line vertical;
    private final Line horizontal;
    
    private boolean arrowAtEnd = true;
    
    public Arrow(boolean hasArrowHead){
        line = new Line();
        
        vertical = new Line();
        horizontal = new Line();
        
        this.getChildren().addAll(line);
        
        if(!hasArrowHead)
            return;
        
        ChangeListener cl = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                double lineStartX;
                double lineEndX;
                double lineStartY;
                double lineEndY;
                
                if(arrowAtEnd){
                    lineStartX = line.getStartX();
                    lineEndX = line.getEndX();
                    lineStartY = line.getStartY();
                    lineEndY = line.getEndY();
                }
                else{
                    lineStartX = line.getEndX();
                    lineEndX = line.getStartX();
                    lineStartY = line.getEndY();
                    lineEndY = line.getStartY();
                }
                
                if (lineEndX != lineStartX || lineEndY != lineStartY) {
                    double f1 = 10 / Math.hypot(lineStartX - lineEndX, lineStartY - lineEndY);
                    double f2 = 5 / Math.hypot(lineStartX - lineEndX, lineStartY - lineEndY);

                    double ox = (lineStartX - lineEndX) * f2;
                    double oy = (lineStartY - lineEndY) * f2;
                    
                    double dx = (lineStartX - lineEndX) * f1;
                    double dy = (lineStartY - lineEndY) * f1;

                    vertical.setStartX(lineEndX + dx - oy);
                    vertical.setStartY(lineEndY + dy + ox);
                    horizontal.setStartX(lineEndX + dx + oy);
                    horizontal.setStartY(lineEndY + dy - ox);
                } else {
                    vertical.setStartX(lineEndX);
                    vertical.setStartY(lineEndY);
                    horizontal.setStartX(lineEndX);
                    horizontal.setStartY(lineEndY);
                }
                
                vertical.setEndX(lineEndX);
                vertical.setEndY(lineEndY);
                horizontal.setEndX(lineEndX);
                horizontal.setEndY(lineEndY);
            }
        };

        line.startXProperty().addListener(cl);
        line.endXProperty().addListener(cl);
        line.startYProperty().addListener(cl);
        line.endYProperty().addListener(cl);
        
        this.getChildren().addAll(vertical, horizontal);
    }
    
    public Line getLine(){
        return line;
    }
    
    public void setStrokeWidth(double width){
        line.setStrokeWidth(width);
        vertical.setStrokeWidth(width);
        horizontal.setStrokeWidth(width);
    }
    
    public void setArrowAtEnd(boolean value){
        arrowAtEnd = value;
    }
}
