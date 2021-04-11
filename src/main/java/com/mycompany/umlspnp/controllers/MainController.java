/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.sequencediagram.Activation;
import com.mycompany.umlspnp.models.sequencediagram.Lifeline;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import com.mycompany.umlspnp.transformations.Transformator;
import com.mycompany.umlspnp.views.MainView;
import java.util.ArrayList;
import java.util.Collections;
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
                    performPreTransformActions();

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
    
    private void createActivationSortedMessages(Activation activation) {
        var sequenceDiagram = model.getSequenceDiagram();
        var sequenceDiagramView = view.getSequenceDiagramView();

        var messages = new ArrayList<Message>(activation.getMessages());
        
        Collections.sort(messages, (m1, m2) -> {
            return m1.getOrder().compareTo(m2.getOrder());
        });

        activation.setSortedMessages(messages);
    }
    
    private void createLifelineSortedActivations(Lifeline lifeline) {
        var sequenceDiagram = model.getSequenceDiagram();
        var sequenceDiagramView = view.getSequenceDiagramView();

        var activationIDs = new ArrayList<Integer>();
        
        for(var activation : lifeline.getActivations()){
            activationIDs.add(activation.getObjectInfo().getID());
        }

        var sortedActivationViews = sequenceDiagramView.sortActivations(activationIDs);

        var sortedActivations = new ArrayList<Activation>();
        for(var activationView : sortedActivationViews){
            var activation = lifeline.getActivation(activationView.getObjectInfo().getID());
            if(activation != null){
                sortedActivations.add(activation);
            }
        }
        lifeline.setSortedActivations(sortedActivations);
    }
    
    private void performPreTransformActions() {
        model.getSequenceDiagram().getLifelines().forEach(lifeline -> {
            createLifelineSortedActivations(lifeline);

            lifeline.getActivations().forEach(activation -> {
                createActivationSortedMessages(activation);
            });
        });
    }
}
