/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author 10ondr
 */
public class SequenceDiagramView extends DiagramView{
    public SequenceDiagramView(){
        Menu addNodeMenu = new Menu("Add Node");
        Menu addEdgeMenu = new Menu("Add Edge");
        addNodeMenu.getItems().add(new MenuItem("Lifeline"));
        diagramMenu.getMenus().addAll(addNodeMenu, addEdgeMenu);
    }
}
