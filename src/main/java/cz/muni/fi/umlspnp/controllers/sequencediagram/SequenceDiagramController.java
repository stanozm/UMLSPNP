package cz.muni.fi.umlspnp.controllers.sequencediagram;

import cz.muni.fi.umlspnp.controllers.BaseController;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.deploymentdiagram.Artifact;
import cz.muni.fi.umlspnp.models.sequencediagram.Lifeline;
import cz.muni.fi.umlspnp.models.sequencediagram.Loop;
import cz.muni.fi.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.umlspnp.models.sequencediagram.SequenceDiagram;
import cz.muni.fi.umlspnp.views.MainView;
import cz.muni.fi.umlspnp.views.sequencediagram.ActivationView;
import cz.muni.fi.umlspnp.views.sequencediagram.LifelineView;
import cz.muni.fi.umlspnp.views.sequencediagram.SequenceDiagramView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *  Controller which handles all functionalities within the sequence diagram
  and provides a mainModel-mainView binding.
 *
 */
public class SequenceDiagramController extends BaseController<SequenceDiagram, SequenceDiagramView>{

    private final List<MessageController> messageControllers;
    private final List<LifelineController> lifelineControllers;
    private final List<LoopController> loopControllers;
    
    public SequenceDiagramController(MainModel mainModel, MainView mainView){
        super(mainModel, mainView, mainModel.getSequenceDiagram(), mainView.getSequenceDiagramView());
        
        messageControllers = new ArrayList<>();
        lifelineControllers = new ArrayList<>();
        loopControllers = new ArrayList<>();

        sequenceDiagramInit();
    }
    
    public void refresh() {
        messageControllers.forEach(controller -> {
            controller.refreshAnnotationVisibility();
        });
    }

    private void sequenceDiagramInit(){
        var deployment = mainModel.getDeploymentDiagram();
        
        // Reassign orders to messages when their order is supposed to change
        model.getSortedMessages().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                var sortedMessages = model.getSortedMessages();
                sortedMessages.forEach(message -> {
                    message.setOrder(sortedMessages.indexOf(message));
                });
            }
        });

        // Remove operation types from messages if the operation type no longer exists
        var allOperationTypes = deployment.getOperationTypes();
        allOperationTypes.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        change.getRemoved().forEach(removedItem -> {
                            model.getSortedMessages().forEach(message -> {
                                if(removedItem.equals(message.getOperationType()))
                                    message.removeOperationType();
                            });
                        });
                    }
                }
            }
        });
        
        // Creation and removal of lifelines
        lifelineManagerInit();
        
        // Creation and removal of messages
        messageManagerInit();
        
        // Creation and removal of loops
        loopManagerInit();
        
        // Connection container for connecting two lifeline activations with a message
        connectionContainerInit();
        
        // Lifeline and loop menu
        nodeMenuInit();
    }

    private void lifelineManagerInit() {
        model.addLifelinesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof Lifeline){
                        var newLifeline = (Lifeline) newNode;
                        var newLifelineView = view.createLifelineView(newLifeline.getObjectInfo().getID());
                        
                        var controller = new LifelineController(mainModel, mainView, newLifeline, newLifelineView);
                        lifelineControllers.add(controller);
                    }
                }
                else if(change.wasRemoved()){
                    var removedLifeline = (Lifeline) change.getValueRemoved();
                    view.removeLifelineView(removedLifeline.getObjectInfo().getID());
                    lifelineControllers.removeIf(controller -> controller.getModel().equals(removedLifeline));
                }
            }
        });

        view.getHighestLifelineProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null)
                    model.setHighestLevelLifeline(Integer.MIN_VALUE);
                else
                    model.setHighestLevelLifeline(((LifelineView) newValue).getObjectInfo().getID());
            }
        });
    }
    
    private void messageManagerInit() {
        model.addMessagesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newMessage = (Message) change.getValueAdded();
                    var firstID = newMessage.getFirst().getObjectInfo().getID();
                    var secondID = newMessage.getSecond().getObjectInfo().getID();
                    var newMessageView = view.createMessage(firstID, secondID, newMessage.getObjectInfo().getID());

                    var controller = new MessageController(mainModel, mainView, newMessage, newMessageView);
                    controller.addSortMessagesCallback(() -> sortMessages());
                    messageControllers.add(controller);
                    sortMessages();
                    Platform.runLater(() -> refresh());
                }
                if(change.wasRemoved()){
                    var removedMessage = (Message) change.getValueRemoved();
                    view.removeMessage(removedMessage.getObjectInfo().getID());
                    messageControllers.removeIf(controller -> controller.getModel().equals(removedMessage));
                    sortMessages();
                    Platform.runLater(() -> refresh());
                }
            }
        });
    }
    
    private void loopManagerInit() {
        model.addLoopsChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newLoop = (Loop) change.getValueAdded();
                    var newLoopView = view.createLoop(newLoop.getObjectInfo().getID());

                    var controller = new LoopController(mainModel, mainView, newLoop, newLoopView);
                    loopControllers.add(controller);
                }
                if(change.wasRemoved()){
                    var removedLoop = (Loop) change.getValueRemoved();
                    view.removeLoop(removedLoop.getObjectInfo().getID());
                    loopControllers.removeIf(controller -> controller.getModel().equals(removedLoop));
                }
            }
        });
    }
    
    private void connectionContainerInit() {
        var connectionContainer = view.getConnectionContainer();
        connectionContainer.connectionProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null)
                    return;
                
                var firstElementID = connectionContainer.getFirstElementID();
                var secondElementID = connectionContainer.getSecondElementID();
                if(firstElementID != null){
                    var firstActivationView = view.getActivationView(firstElementID.intValue());
                    firstActivationView.setSelected(true);
                    
                    if(secondElementID != null){
                        if(connectionContainer.getFirstElement() instanceof ActivationView){
                            var firstActivation = model.getActivation(firstElementID.intValue());
                            var secondActivation = model.getActivation(secondElementID.intValue());
                            model.createMessage(firstActivation, secondActivation);
                        }
                        firstActivationView.setSelected(false);
                        connectionContainer.clear();
                    }
                }
            }
        });
    }
    
    private MenuItem createLifelineSubmenu(SequenceDiagram sequence, Artifact artifact){
        var submenu = new MenuItem(artifact.getNameProperty().getValue());
        submenu.textProperty().bind(artifact.getNameProperty());
        
        EventHandler<ActionEvent> submenuEventHandler = (ActionEvent tt) -> {
            if(tt.getSource().equals(submenu)){
                sequence.createLifeline(artifact);
            }
        };
        submenu.setOnAction(submenuEventHandler);
        
        return submenu;
    }
    
    /**
     * Creates a collection of available lifeline menuitems which mantains 
     * itself by checking the added and removed artifact and lifelines.
     * 
     * @return The self-maintaining map between artifact and the corresponding lifeline Menu Item.
     */
    private ObservableMap<Artifact, MenuItem> createLifelineSubmenus() {
        var deployment = this.mainModel.getDeploymentDiagram();
        ObservableMap<Artifact, MenuItem> lifelineSubmenus = FXCollections.observableHashMap();

        // Changes in the deployment diagram
        deployment.addAllNodesChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = (Artifact) change.getValueAdded();
                    var newMenuItem = createLifelineSubmenu(model, newNode);
                    lifelineSubmenus.put(newNode, newMenuItem);
                }
                else if(change.wasRemoved()){
                    var removedNode = (Artifact) change.getValueRemoved();
                    lifelineSubmenus.remove(removedNode);
                    
                    model.removeLifeline(removedNode);
                }
            }
        });
        
        // Changes in the sequence diagram
        model.addLifelinesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newLifeline = (Lifeline) change.getValueAdded();
                    lifelineSubmenus.remove(newLifeline.getArtifact());
                }
                else if(change.wasRemoved()){
                    var removedLifeline = (Lifeline) change.getValueRemoved();
                    var removedNodeArtifact = removedLifeline.getArtifact();
                    if(deployment.getNode(removedNodeArtifact.getObjectInfo().getID()) != null){
                        var newMenuItem = createLifelineSubmenu(model, removedNodeArtifact);
                        lifelineSubmenus.put(removedNodeArtifact, newMenuItem);
                    }
                }
            }
        });

        return lifelineSubmenus;
    }
    
    private void nodeMenuInit() {
        Menu addNodeMenu = new Menu("Add Node");
        Menu lifelineMenu = new Menu("Lifeline");
        
        var lifelineSubmenus = createLifelineSubmenus();

        lifelineSubmenus.addListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newItem = (MenuItem) change.getValueAdded();
                    lifelineMenu.getItems().add(newItem);
                }
                else if(change.wasRemoved()){
                    var removedItem = (MenuItem) change.getValueRemoved();
                    lifelineMenu.getItems().remove(removedItem);
                }
            }
        });
        
        // Disable menu when the collection of available lifelines is empty
        lifelineMenu.disableProperty().bind(Bindings.isEmpty(lifelineSubmenus));

        var loopMenuItem = new MenuItem("Add loop");
        loopMenuItem.setOnAction((ActionEvent tt) -> {
            if(tt.getSource().equals(loopMenuItem)){
                model.createLoop();
            }
        });
        
        addNodeMenu.getItems().addAll(lifelineMenu, loopMenuItem);
        view.addMenu(addNodeMenu);
    }
    
    public void sortMessages() {
        Collections.sort(model.getSortedMessages(), (m1, m2) -> {
            Double first = view.getConnection(m1.getObjectInfo().getID()).getSourceConnectionSlot().getLocalToSceneTransform().getTy();
            Double second = view.getConnection(m2.getObjectInfo().getID()).getSourceConnectionSlot().getLocalToSceneTransform().getTy();
            return first.compareTo(second);
        });
    }
}
