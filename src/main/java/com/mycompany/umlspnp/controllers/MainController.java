/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.transformations.Transformator;
import com.mycompany.umlspnp.views.MainView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author 10ondr
 */
public class MainController {
    private final MainModel model;
    private final MainView view;
    
    public MainController(MainModel mainModel, MainView mainView){
        this.model = mainModel;
        this.view = mainView;
        
        init();
    }
    
    private void init() {
        Menu fileMenu = new Menu("File");
        var transformMenuItem = new MenuItem("Transform");

        transformMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(transformMenuItem)){
                    var transformator = new Transformator(model);
                    transformator.transform();
                    System.out.println(transformator.getOutput());
                }
            }
        });

        fileMenu.getItems().add(transformMenuItem);

        Menu aboutMenu = new Menu("About");
        aboutMenu.getItems().add(new MenuItem("Info"));
        
        this.view.addMenu(fileMenu);
        this.view.addMenu(aboutMenu);
    }
}
