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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;

/**
 *
 * @author 10ondr
 */
public class Connection extends BasicElement {
    private final ConnectionSlot source;
    private final ConnectionSlot destination;
    private final Line line;
    
    public Connection(int modelObjectID, ConnectionSlot source, ConnectionSlot destination, Group diagramRoot){
        super(modelObjectID);
        
        this.source = source;
        this.destination = destination;

        this.line = new Line();
        
        this.source.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                var position = calculatePosition(diagramRoot, (Transform) newValue);

                line.startXProperty().setValue(position.getX());
                line.startYProperty().setValue(position.getY());
            }
        });
        
        this.destination.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                var position = calculatePosition(diagramRoot, (Transform) newValue);
                
                line.endXProperty().setValue(position.getX());
                line.endYProperty().setValue(position.getY());
            }
        });

        this.getChildren().add(line);
    }

    private Point2D calculatePosition(Node relativeTo, Transform newPositionTransform){
        var newLocalPosition = sceneToLocal(new Point2D(newPositionTransform.getTx(), newPositionTransform.getTy()));
        return Utils.getPositionRelativeTo( this, relativeTo, newLocalPosition);
    }
    
    public void removeSlots(){
        this.source.setDeleted(true);
        this.destination.setDeleted(true);
    }
}
