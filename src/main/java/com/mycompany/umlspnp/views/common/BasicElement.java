/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import com.mycompany.umlspnp.common.ObjectInfo;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

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
}
