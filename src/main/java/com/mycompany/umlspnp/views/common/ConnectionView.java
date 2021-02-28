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
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
        
        this.arrow.setCursor(Cursor.HAND);
        
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
        
        this.setOnMouseEntered((e) -> {
            this.arrow.setStrokeWidth(4);
            e.consume();
        });
        
        this.setOnMouseExited((e) -> {
            this.arrow.setStrokeWidth(1);
            e.consume();
        });
        
        this.setOnMousePressed((e) -> {
            actionElementClicked(e);
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
    
    private void actionElementClicked(MouseEvent e){
        if(e.getButton() == MouseButton.PRIMARY){
            if(contextMenu.isShowing()){
                contextMenu.hide();
            }
        }
        else if(e.getButton() == MouseButton.SECONDARY){
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
        }
        this.toFront();
    }
}
