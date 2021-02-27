/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import com.mycompany.umlspnp.common.ObjectInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author 10ondr
 */
public class BasicElement extends Group{
    private final ObjectInfo objectInfo;
    protected final static double gridSize = 10;
    
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
}
