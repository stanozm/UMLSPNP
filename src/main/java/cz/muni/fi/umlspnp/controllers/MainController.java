package cz.muni.fi.umlspnp.controllers;

import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.sequencediagram.Activation;
import cz.muni.fi.umlspnp.models.sequencediagram.Lifeline;
import cz.muni.fi.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.umlspnp.transformations.DefaultTransformator;
import cz.muni.fi.umlspnp.views.MainView;
import java.util.ArrayList;
import java.util.Collections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *  Main controller which handles the main menu functionality.
 *
 */
public class MainController extends BaseController<MainModel, MainView> {
    
    public MainController(MainModel model, MainView view){
        super(model, view, model, view);
    }
    
    public void init() {
        Menu fileMenu = new Menu("File");
        var transformMenuItem = new MenuItem("Transform");

        transformMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(transformMenuItem)){
                    performPreTransformActions();

                    var transformator = new DefaultTransformator(model);
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
        var messages = new ArrayList<Message>(activation.getMessages());
        
        Collections.sort(messages, (m1, m2) -> {
            return m1.getOrder().compareTo(m2.getOrder());
        });

        activation.setSortedMessages(messages);
    }
    
    private void createLifelineSortedActivations(Lifeline lifeline) {
        var sequenceDiagramView = view.getSequenceDiagramView();

        var activationIDs = new ArrayList<Integer>();
        lifeline.getActivations().forEach(activation -> {
            activationIDs.add(activation.getObjectInfo().getID());
        });

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
