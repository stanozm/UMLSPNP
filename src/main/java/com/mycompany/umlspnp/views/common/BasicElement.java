/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author 10ondr
 */
public class BasicElement extends Group{
    protected final double gridSize;
    
    protected final ContextMenu contextMenu;
    
    public BasicElement(double gridSize){
        this.gridSize = gridSize;
        contextMenu = new ContextMenu();
    }
    
    public ContextMenu getContextMenu(){
        return contextMenu;
    }
    
    public void addMenuItem(MenuItem newMenuItem){
        this.getContextMenu().getItems().add(newMenuItem);
    }
}
