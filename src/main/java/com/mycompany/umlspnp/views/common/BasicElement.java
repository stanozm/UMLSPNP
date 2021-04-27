package com.mycompany.umlspnp.views.common;

import com.mycompany.umlspnp.common.ObjectInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 *  Provides a base class for a element that will be rendered in one of the
 * diagrams.
 * Each of these elements move in a virtual grid for better alignment.
 *
 */
public abstract class BasicElement extends Group{
    private final ObjectInfo objectInfo;
    protected double gridSize = 10;
    
    protected final ContextMenu contextMenu;
    
    public BasicElement(int modelObjectID){
        this.objectInfo = new ObjectInfo(modelObjectID);
        
        contextMenu = new ContextMenu();
    }
    
    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }
    
    public ContextMenu getContextMenu(){
        return contextMenu;
    }
    
    public void addMenuItem(MenuItem newMenuItem){
        this.getContextMenu().getItems().add(newMenuItem);
    }
    
    protected void lockMovement(Node childElement){
        this.translateXProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                double diff = (double) newValue - (double) oldValue;
                childElement.setTranslateX(childElement.getTranslateX() + diff);
            }
        });
        
        this.translateYProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                double diff = (double) newValue - (double) oldValue;
                childElement.setTranslateY(childElement.getTranslateY() + diff);
            }
        });
    }
    
    public void setGridSize(double newGridSize) {
        this.gridSize = newGridSize;
    }
}
