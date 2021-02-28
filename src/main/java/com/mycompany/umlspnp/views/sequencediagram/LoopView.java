/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.sequencediagram;

import com.mycompany.umlspnp.views.common.NamedRectangle;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author 10ondr
 */
public class LoopView extends NamedRectangle {
    public LoopView(double x, double y, double width, double height, String name, int modelObjectID) {
        super(x, y, width, height, name, modelObjectID);
        
        this.setFill(Color.TRANSPARENT);
    }

    public void addChangeListener(ChangeListener cl){
        this.localToSceneTransformProperty().addListener(cl);
    }
    
    public Rectangle getRectangle(){
        return rect;
    }
}
