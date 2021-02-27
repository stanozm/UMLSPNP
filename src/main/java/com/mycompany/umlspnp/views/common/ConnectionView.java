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
import javafx.scene.transform.Transform;

/**
 *
 * @author 10ondr
 */
public class ConnectionView extends BasicElement {
    protected final ConnectionSlot source;
    protected final ConnectionSlot destination;
    protected final Arrow arrow;
    
    protected final Group diagramRoot;

    public ConnectionView(int modelObjectID, ConnectionSlot source, ConnectionSlot destination, Group diagramRoot, boolean hasArrow){
        super(modelObjectID);
        
        this.source = source;
        this.destination = destination;

        this.diagramRoot = diagramRoot;
        
        this.arrow = new Arrow(hasArrow);
        
        this.source.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                refreshLineStartPosition();
            }
        });
        
        this.destination.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                refreshLineEndPosition();
            }
        });
        
        this.getChildren().add(arrow);
    }
    
    private Point2D calculatePosition(Node relativeTo, Transform newPositionTransform){
        var newLocalPosition = sceneToLocal(new Point2D(newPositionTransform.getTx(), newPositionTransform.getTy()));
        return Utils.getPositionRelativeTo(this, relativeTo, newLocalPosition);
    }
    
    private void refreshLineStartPosition(){
        var startPosition = calculatePosition(diagramRoot, (Transform) source.localToSceneTransformProperty().getValue());
        var line = arrow.getLine();
        line.startXProperty().setValue(startPosition.getX());
        line.startYProperty().setValue(startPosition.getY());
    }
    
    private void refreshLineEndPosition(){
        var endPosition = calculatePosition(diagramRoot, (Transform) destination.localToSceneTransformProperty().getValue());
        var line = arrow.getLine();
        line.endXProperty().setValue(endPosition.getX());
        line.endYProperty().setValue(endPosition.getY());
    }
    
    public void refreshLinePosition(){
        refreshLineStartPosition();
        refreshLineEndPosition();
    }
    
    public void removeSlots(){
        this.source.setDeleted(true);
        this.destination.setDeleted(true);
    }
    
    public Arrow getArrow(){
        return arrow;
    }
}
