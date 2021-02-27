/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.sequencediagram;

import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.views.common.BasicRectangle;
import com.mycompany.umlspnp.views.common.NamedRectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *
 * @author 10ondr
 */
public class LifelineView extends NamedRectangle {
    private final BasicRectangle spanBox;
    private final Line spanLine;
    
    public LifelineView(double x, double y, double width, double height, int modelObjectID) {
        super(x, y, width, height, "Unlinked lifeline", modelObjectID);
        
        this.setResizable(false, false);
        
        spanBox = new BasicRectangle(Utils.generateObjectID(), 0, 0, 10, 150);
        initSpanBox();
        
        spanLine = new Line();
        initSpanLine();
        
        this.getChildren().add(spanBox);
        this.getChildren().add(spanLine);
    }
    
    private void initSpanBox(){
        spanBox.setFill(Color.WHITE);
        spanBox.setMinHeight(30);
        spanBox.setResizable(true, false);
        spanBox.setDraggable(false);
        spanBox.translateXProperty().bind(this.widthProperty().divide(2).subtract(spanBox.widthProperty().divide(2)));
        spanBox.translateYProperty().bind(this.heightProperty().add(30));
    }
    
    private void initSpanLine(){
        spanLine.getStrokeDashArray().addAll(7.0, 4.0);
        spanLine.startXProperty().bind(this.widthProperty().divide(2));
        spanLine.startYProperty().bind(this.heightProperty());
        spanLine.endXProperty().bind(spanLine.startXProperty());
        spanLine.endYProperty().bind(spanBox.translateYProperty());
    }
    
    public BasicRectangle getSpanBox() {
        return this.spanBox;
    }
}
