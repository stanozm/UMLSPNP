package com.mycompany.umlspnp.views.common;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *  Renders a line with an optional arrow at one end.
 *
 */
public class Arrow extends Group {
    private final Line line;
    
    private final Line vertical;
    private final Line horizontal;
    
    private boolean arrowAtEnd = true;
    
    private final DoubleProperty lineCenterX = new SimpleDoubleProperty();
    private final DoubleProperty lineCenterY = new SimpleDoubleProperty();
    
    public Arrow(boolean hasArrowHead){
        line = new Line();
        
        vertical = new Line();
        horizontal = new Line();
        
        this.getChildren().addAll(line);
        
        this.line.layoutBoundsProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                var newLineCenter = (Bounds) newValue;
                lineCenterX.set(newLineCenter.getCenterX());
                lineCenterY.set(newLineCenter.getCenterY());
            }
        });
        
        if(hasArrowHead) {
            ChangeListener cl = createRefreshChangeListener();
            line.startXProperty().addListener(cl);
            line.endXProperty().addListener(cl);
            line.startYProperty().addListener(cl);
            line.endYProperty().addListener(cl);

            this.getChildren().addAll(vertical, horizontal);
        }
    }
    
    /**
     * Creates a change listener which refreshes the orientation of the line and
     * the arrow head.
     * 
     * @return The refresh change listener.
     */
    private ChangeListener createRefreshChangeListener() {
        return new ChangeListener(){
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
    }
    
    public Line getLine(){
        return line;
    }
    
    public void setStrokeWidth(double width){
        line.setStrokeWidth(width);
        vertical.setStrokeWidth(width);
        horizontal.setStrokeWidth(width);
    }
    
    public void setStroke(Color newColor){
        line.setStroke(newColor);
        vertical.setStroke(newColor);
        horizontal.setStroke(newColor);
    }
    
    public void setArrowAtEnd(boolean value){
        arrowAtEnd = value;
    }
    
    public DoubleProperty getCenterX(){
        return lineCenterX;
    }
    
    public DoubleProperty getCenterY(){
        return lineCenterY;
    }
}
