/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 *
 * @author 10ondr
 * GridPane used because in VBox order of elements equals their focus (toFront(), toBack())
 * which is not wanted nor expected.
 */
public class DiagramView extends GridPane{
    protected final MenuBar diagramMenu = new MenuBar();
    protected final Pane diagramPane = new Pane();
    
    public DiagramView(){
        diagramPane.setStyle("-fx-background-color: white");
        
        GridPane.setVgrow(diagramPane, Priority.ALWAYS);
        GridPane.setHgrow(diagramPane, Priority.ALWAYS);
        
        GridPane.setConstraints(diagramMenu, 0, 0);
        GridPane.setConstraints(diagramPane, 0, 1);

        this.getChildren().addAll(diagramMenu, diagramPane);
        diagramMenu.toFront();
    }
    
    public void addMenu(Menu newMenu){
        diagramMenu.getMenus().add(newMenu);
    }
    
    public void setActive(boolean value){
        diagramPane.setVisible(value);
    }
}
