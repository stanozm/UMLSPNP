package com.mycompany.umlspnp.controllers.sequencediagram;

import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.ConnectionFailure;
import com.mycompany.umlspnp.models.OperationType;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.sequencediagram.Activation;
import com.mycompany.umlspnp.models.sequencediagram.ExecutionTime;
import com.mycompany.umlspnp.models.sequencediagram.Lifeline;
import com.mycompany.umlspnp.models.sequencediagram.Loop;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import com.mycompany.umlspnp.models.sequencediagram.MessageSize;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.common.AnnotationOwner;
import com.mycompany.umlspnp.views.common.layouts.BooleanModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditFailureTypeModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.IntegerModalWindow;
import com.mycompany.umlspnp.views.sequencediagram.ActivationView;
import com.mycompany.umlspnp.views.sequencediagram.LifelineView;
import com.mycompany.umlspnp.views.sequencediagram.LoopView;
import com.mycompany.umlspnp.views.sequencediagram.MessageView;
import com.mycompany.umlspnp.views.sequencediagram.SequenceDiagramView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

/**
 *  Controller which handles all functionalities within the sequence diagram
  and provides a mainModel-mainView binding.
 *
 */
public class SequenceDiagramController {
    private final MainModel mainModel;
    private final MainView mainView;
    
    private final SequenceDiagram model;
    private final SequenceDiagramView view;
    
    private final List<MessageController> messageControllers;
    
    public SequenceDiagramController(MainModel mainModel, MainView mainView){
        this.mainModel = mainModel;
        this.mainView = mainView;
        
        messageControllers = new ArrayList<>();
        
        this.model = mainModel.getSequenceDiagram();
        this.view = mainView.getSequenceDiagramView();
        
        sequenceDiagramInit();
    }
    
    /**
     * Creates sample sequence diagram lifelines, activations and messages.
     */
    public void createSampleData() {
        var deployment = mainModel.getDeploymentDiagram();
        var sequence = mainModel.getSequenceDiagram();
        var sequenceView = mainView.getSequenceDiagramView();
        
        var A = deployment.getDeploymentTarget(1);
        var AA = deployment.getDeploymentTarget(2);
        var AAA = deployment.getDeploymentTarget(3);
        var B = deployment.getDeploymentTarget(4);
        var BB = deployment.getDeploymentTarget(5);
        var BBB = deployment.getDeploymentTarget(6);
        
        var lifelineA = sequence.createLifeline(A);
        var lifelineAA = sequence.createLifeline(AA);
        var lifelineAAA = sequence.createLifeline(AAA);
        var lifelineB = sequence.createLifeline(B);
        
        var activationA = lifelineA.createActivation();
        var activationAA = lifelineAA.createActivation();
        var activationAAA = lifelineAAA.createActivation();
        var activationB = lifelineB.createActivation();

        // Sequence diagram for tree building algorithm
        AA.getNameProperty().setValue("B");
        AAA.getNameProperty().setValue("C");
        B.getNameProperty().setValue("D");

        var lifeA = lifelineA;
        var lifeA_view = sequenceView.getLifelineView(lifeA.getObjectInfo().getID());

        var lifeB = lifelineAA;
        var lifeB_view = sequenceView.getLifelineView(lifeB.getObjectInfo().getID());
        lifeB_view.setTranslateX(300);
        
        var lifeC = lifelineAAA;
        var lifeC_view = sequenceView.getLifelineView(lifeC.getObjectInfo().getID());
        lifeC_view.setTranslateX(600);
        
        var lifeD = lifelineB;
        var lifeD_view = sequenceView.getLifelineView(lifeD.getObjectInfo().getID());
        lifeD_view.setTranslateX(900);

        
        var aA = activationA;
        var aA_view = sequenceView.getActivationView(aA.getObjectInfo().getID());
        aA_view.changeDimensions(aA_view.getWidth(), 400);
        
        var ACTIVATION_BASE_Y = aA_view.getTranslateY();
        
        var aB = activationAA;
        var aB_view = sequenceView.getActivationView(aB.getObjectInfo().getID());
        aB_view.changeDimensions(aB_view.getWidth(), 400);
        
        var aC1 = activationAAA;
        var aC1_view = sequenceView.getActivationView(aC1.getObjectInfo().getID());
        aC1_view.changeDimensions(aC1_view.getWidth(), 150);
        aC1_view.setTranslateY(aC1_view.getTranslateY() + 20);
        
        var aC2 = lifeC.createActivation();
        var aC2_view = sequenceView.getActivationView(aC2.getObjectInfo().getID());
        aC2_view.changeDimensions(aC2_view.getWidth(), 75);
        aC2_view.setTranslateY(aC1_view.getTranslateY() + aC1_view.getHeight() + 40);
        
        var aD1 = activationB;
        var aD1_view = sequenceView.getActivationView(aD1.getObjectInfo().getID());
        aD1_view.changeDimensions(aD1_view.getWidth(), 30);
        aD1_view.setTranslateY(aD1_view.getTranslateY() + 40);
        
        var aD2 = lifeD.createActivation();
        var aD2_view = sequenceView.getActivationView(aD2.getObjectInfo().getID());
        aD2_view.changeDimensions(aD2_view.getWidth(), 75);
        aD2_view.setTranslateY(aD1_view.getTranslateY() + aD1_view.getHeight() + 40);
        
        var aD3 = lifeD.createActivation();
        var aD3_view = sequenceView.getActivationView(aD3.getObjectInfo().getID());
        aD3_view.changeDimensions(aD3_view.getWidth(), 30);
        aD3_view.setTranslateY(aD2_view.getTranslateY() + aD2_view.getHeight() + 30);
        
        
        var mess1 = sequence.createMessage(activationA, activationAA);
        mess1.nameProperty().setValue("1");
        
        var mess1_1 = sequence.createMessage(activationAA, activationAAA);
        var mess1_1_view = sequenceView.getConnection(mess1_1.getObjectInfo().getID());
        mess1_1_view.getSourceConnectionSlot().setTranslateY(aC1_view.getTranslateY() - ACTIVATION_BASE_Y);
        mess1_1.nameProperty().setValue("11");
        
        var mess1_1_1 = sequence.createMessage(activationAAA, activationB);
        var mess1_1_1_view = sequenceView.getConnection(mess1_1_1.getObjectInfo().getID());
        mess1_1_1_view.getSourceConnectionSlot().setTranslateY((aD1_view.getTranslateY() - ACTIVATION_BASE_Y) - (aC1_view.getTranslateY() - ACTIVATION_BASE_Y));
        mess1_1_1.nameProperty().setValue("111");
        mess1_1_1.setMessageSize(10);
        
        var mess1_1_2 = sequence.createMessage(aC1, aC1);
        var mess1_1_2_view = sequenceView.getConnection(mess1_1_2.getObjectInfo().getID());
        mess1_1_2_view.getSourceConnectionSlot().setTranslateY(mess1_1_1_view.getSourceConnectionSlot().getTranslateY() + 30);
        mess1_1_2_view.getDestinationConnectionSlot().setTranslateY(mess1_1_1_view.getSourceConnectionSlot().getTranslateY() + 30);
        mess1_1_2.nameProperty().setValue("112");
        
        var mess1_1_3 = sequence.createMessage(aC1, aD2);
        var mess1_1_3_view = sequenceView.getConnection(mess1_1_3.getObjectInfo().getID());
        mess1_1_3_view.getSourceConnectionSlot().setTranslateY((aD2_view.getTranslateY() - ACTIVATION_BASE_Y) - (aC1_view.getTranslateY() - ACTIVATION_BASE_Y));
        mess1_1_3.nameProperty().setValue("113");
        mess1_1_3.setMessageSize(10);
        
        var mess1_1_3_1 = sequence.createMessage(aD2, aD2);
        var mess1_1_3_1_view = sequenceView.getConnection(mess1_1_3_1.getObjectInfo().getID());
        mess1_1_3_1_view.getSourceConnectionSlot().setTranslateY(20);
        mess1_1_3_1_view.getDestinationConnectionSlot().setTranslateY(20);
        mess1_1_3_1.nameProperty().setValue("1131");
        
        var mess1_2 = sequence.createMessage(aB, aC2);
        var mess1_2_view = sequenceView.getConnection(mess1_2.getObjectInfo().getID());
        mess1_2_view.getSourceConnectionSlot().setTranslateY((aC2_view.getTranslateY() - ACTIVATION_BASE_Y));
        mess1_2.nameProperty().setValue("12");
        
        var mess1_2_1 = sequence.createMessage(aC2, aD3);
        var mess1_2_1_view = sequenceView.getConnection(mess1_2_1.getObjectInfo().getID());
        mess1_2_1_view.getSourceConnectionSlot().setTranslateY((aD3_view.getTranslateY() - ACTIVATION_BASE_Y) - (aC2_view.getTranslateY() - ACTIVATION_BASE_Y) + 10);
        mess1_2_1_view.getDestinationConnectionSlot().setTranslateY(mess1_2_1_view.getSourceConnectionSlot().getTranslateY());
        mess1_2_1.nameProperty().setValue("121");
        mess1_2_1.setMessageSize(10);
        
        var mess2 = sequence.createMessage(aA, aB);
        var mess2_view = sequenceView.getConnection(mess2.getObjectInfo().getID());
        mess2_view.getSourceConnectionSlot().setTranslateY((aA_view.getTranslateY() - ACTIVATION_BASE_Y) + (aA_view.getHeight()) - 40);
        mess2_view.getDestinationConnectionSlot().setTranslateY(mess2_view.getSourceConnectionSlot().getTranslateY());
        mess2.nameProperty().setValue("2");
        
        var mess2_1 = sequence.createMessage(aB, aB);
        var mess2_1_view = sequenceView.getConnection(mess2_1.getObjectInfo().getID());
        mess2_1_view.getSourceConnectionSlot().setTranslateY(mess2_view.getSourceConnectionSlot().getTranslateY() + 15);
        mess2_1_view.getDestinationConnectionSlot().setTranslateY(mess2_view.getSourceConnectionSlot().getTranslateY() + 15);
        mess2_1.nameProperty().setValue("21");
    }

    private void sequenceDiagramInit(){
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
        
        model.getSortedMessages().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                var sortedMessages = model.getSortedMessages();
                sortedMessages.forEach(message -> {
                    message.setOrder(sortedMessages.indexOf(message));
                });
            }
        });
        
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
                }
                if(change.wasRemoved()){
                    var removedMessage = (Message) change.getValueRemoved();
                    view.removeMessage(removedMessage.getObjectInfo().getID());
                    messageControllers.removeIf(controller -> controller.getModel().equals(removedMessage));
                    sortMessages();
                }
            }
        });
        
        
        model.addLifelinesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof Lifeline){
                        var newLifeline = (Lifeline) newNode;

                        var newLifelineView = view.createLifelineView(newLifeline.getObjectInfo().getID());
                        lifelineInit(newLifelineView, newLifeline);
                        lifelineMenuInit(newLifelineView);
                        //deploymentTargetAnnotationsInit(newDT);
                    }
                }
                else if(change.wasRemoved()){
                    var removedLifeline = (Lifeline) change.getValueRemoved();
                    view.removeLifelineView(removedLifeline.getObjectInfo().getID());
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
        
        Menu addNodeMenu = new Menu("Add Node");
        Menu lifelineMenu = new Menu("Lifeline");
        
        ObservableMap<Artifact, MenuItem> lifelineSubmenus = FXCollections.observableHashMap();
        
        var deployment = this.mainModel.getDeploymentDiagram();
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
        
        lifelineMenu.disableProperty().bind(Bindings.isEmpty(lifelineSubmenus));


        var loopMenuItem = new MenuItem("Add loop");

        loopMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(loopMenuItem)){
                    model.createLoop();
                }
            }
        });
        
        var loopChangeListener = new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newLoop = (Loop) change.getValueAdded();
                    var newLoopView = view.createLoop(newLoop.getObjectInfo().getID());

                    loopInit(newLoop, newLoopView);
                }
                if(change.wasRemoved()){
                    var removedLoop = (Loop) change.getValueRemoved();
                    view.removeLoop(removedLoop.getObjectInfo().getID());
                }
            }
        };
        model.addLoopsChangeListener(loopChangeListener);
        
        addNodeMenu.getItems().addAll(lifelineMenu, loopMenuItem);
        view.addMenu(addNodeMenu);
        
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
    }

    private void loopInit(Loop loop, LoopView loopView){
        var sequence = this.mainModel.getSequenceDiagram();
        var loopObjectID = loop.getObjectInfo().getID();
        
        loopView.getNameProperty().bind(loop.nameProperty());
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            sequence.removeLoop(loopObjectID);
        });
        loopView.addMenuItem(menuItemDelete);

        
        MenuItem menuItemIterations = new MenuItem("Change iterations");
        menuItemIterations.setOnAction((e) -> {
            var iterationsWindow = new IntegerModalWindow(   (Stage) loopView.getScene().getWindow(),
                                                                                    "Edit loop iterations",
                                                                                    "Iterations",
                                                                                    2,
                                                                                    null,
                                                                                    loop.iterationsProperty());
            iterationsWindow.showAndWait();
        });
        loopView.addMenuItem(menuItemIterations);
    }
    
    private MenuItem createLifelineSubmenu(SequenceDiagram sequence, Artifact artifact){
        var submenu = new MenuItem(artifact.getNameProperty().getValue());
        submenu.textProperty().bind(artifact.getNameProperty());
        
        EventHandler<ActionEvent> submenuEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(submenu)){
                    sequence.createLifeline(artifact);
                }
            }
        };
        submenu.setOnAction(submenuEventHandler);
        
        return submenu;
    }
    
    private void lifelineInit(LifelineView newLifelineView, Lifeline newLifeline) {
        var sequenceDiagramView = mainView.getSequenceDiagramView();
        
        newLifelineView.getNameProperty().bind(newLifeline.nameProperty());

        newLifeline.addActivationsChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof Activation){
                        var newActivation = (Activation) newNode;

                        var lifelineView = sequenceDiagramView.getLifelineView(newActivation.getLifeline().getObjectInfo().getID());
                        var newActivationView = lifelineView.createActivationView(newActivation.getObjectInfo().getID());
                        
                        activationInit(newActivationView, newActivation);
                        activationMenuInit(newActivationView);
                        //deploymentTargetAnnotationsInit(newDT);
                    }
                }
                else if(change.wasRemoved()){
                    var removedActivation = (Activation) change.getValueRemoved();
                    var lifeline = removedActivation.getLifeline();
                    var lifelineView = sequenceDiagramView.getLifelineView(lifeline.getObjectInfo().getID());
                    lifelineView.removeActivationView(removedActivation.getObjectInfo().getID());
                }
            }
        });
    }
    
    private void lifelineMenuInit(LifelineView lifelineView){
        var sequenceDiagram = this.mainModel.getSequenceDiagram();
        var lifelineObjectID = lifelineView.getObjectInfo().getID();
        var lifeline = sequenceDiagram.getLifeline(lifelineObjectID);
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) lifelineView.getScene().getWindow(), 
                        "Confirm", "The lifeline \"" + Utils.shortenString(lifelineView.getNameProperty().getValue(), 50) + "\" will be deleted. Proceed?");
            confirmWindow.showAndWait();
            if(confirmWindow.getResult()){
                sequenceDiagram.removeLifeline(lifelineObjectID);
            }
        });
        lifelineView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemCreateActivation = new MenuItem("Create activation");
        menuItemCreateActivation.setOnAction((e) -> {
            lifeline.createActivation();
        });
        lifelineView.addMenuItem(menuItemCreateActivation);
    }
    
    private void activationInit(ActivationView newActivationView, Activation newActivation) {
        var sequenceDiagram = mainModel.getSequenceDiagram();
        var deploymentDiagram = mainModel.getDeploymentDiagram();
        var sequenceDiagramView = mainView.getSequenceDiagramView();

        var connectionContainer = sequenceDiagramView.getConnectionContainer();
        
        sequenceDiagramView.registerNodeToSelect(newActivationView, (e) -> {
            var startElement = (ActivationView) connectionContainer.getFirstElement();
            if(startElement != null){
                if(startElement.getClass().equals(newActivationView.getClass())){
                    var startActivation = sequenceDiagram.getActivation(startElement.getObjectInfo().getID());
                    var startArtifact = startActivation.getLifeline().getArtifact();
                    var newArtifact = newActivation.getLifeline().getArtifact();
                    if(startActivation == newActivation || deploymentDiagram.areNodesConnected(startArtifact, newArtifact)){
                        connectionContainer.setSecondElement(newActivationView);
                        return;
                    }
                    else{
                        System.err.println("Unable to create connection. Nodes in deployment diagram are not connected.");
                    }
                }
                else{
                    System.err.println("Unable to create connection. Select suitable destination node.");
                }
                startElement.setSelected(false);
                connectionContainer.clear();
            }
        });
    }
    
    private void activationMenuInit(ActivationView activationView){
        var sequenceDiagram = this.mainModel.getSequenceDiagram();
        var activationObjectID = activationView.getObjectInfo().getID();
        var lifeline = sequenceDiagram.getActivation(activationObjectID).getLifeline();
        
        MenuItem menuItemDelete = new MenuItem("Delete activation");
        menuItemDelete.setOnAction((e) -> {
            BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) activationView.getScene().getWindow(), 
                        "Confirm", "The activation of lifeline \"" + Utils.shortenString(lifeline.nameProperty().getValue(), 50) + "\" will be deleted. Proceed?");
            confirmWindow.showAndWait();
            if(confirmWindow.getResult()){
                sequenceDiagram.removeActivation(activationObjectID);
            }
        });
        activationView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemConnect = new MenuItem("Create message");
        menuItemConnect.setOnAction((e) -> {
            this.mainView.getSequenceDiagramView().startConnection(activationView);
        });
        activationView.addMenuItem(menuItemConnect);
    }
    
    public void sortMessages() {
        Collections.sort(model.getSortedMessages(), (m1, m2) -> {
            Double first = view.getConnection(m1.getObjectInfo().getID()).getSourceConnectionSlot().getLocalToSceneTransform().getTy();
            Double second = view.getConnection(m2.getObjectInfo().getID()).getSourceConnectionSlot().getLocalToSceneTransform().getTy();
            return first.compareTo(second);
        });
    }

}
