package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.models.OperationEntry;
import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.views.*;
import com.mycompany.umlspnp.models.*;
import com.mycompany.umlspnp.models.NamedNode;
import com.mycompany.umlspnp.models.OperationType;
import com.mycompany.umlspnp.models.deploymentdiagram.*;
import com.mycompany.umlspnp.views.common.layouts.BooleanModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.StringModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.ArtifactView;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import com.mycompany.umlspnp.views.deploymentdiagram.EditOperationEntryModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.EditOperationModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.EditTransitionModalWindow;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

/**
 *  Controller which handles all functionalities within the deployment diagram
 * and binds deployment diagram model to its view.
 *
 */
public class DeploymentDiagramController {
    private final MainModel model;
    private final MainView view;
    
    public DeploymentDiagramController(MainModel mainModel, MainView mainView){
        this.model = mainModel;
        this.view = mainView;
        
        deploymentDiagramInit(this.model.getDeploymentDiagram());
    }

    /**
     * Creates sample annotations for a specified deployment target.
     * @param DT Deployment Target to have the sample annotations added.
     */
    private void createSampleAnnotations(DeploymentTarget DT){
        var deployment = model.getDeploymentDiagram();
        
        var opType1 = new OperationType("ReadDeviceData");
        var opType2 = new OperationType("WriteDeviceData");
        deployment.addOperationType(opType1);
        deployment.addOperationType(opType2);
        
        var stateUp = DT.getStates().get(0);
        var stateDown = DT.getStates().get(1);

        StateOperation operationsUp = new StateOperation(stateUp);
        operationsUp.addOperationEntry(opType1, null);
        operationsUp.addOperationEntry(opType2, null);
        StateOperation operationsDown = new StateOperation(stateDown);
        operationsDown.addOperationEntry(opType2, 50);
        DT.addStateOperation(operationsUp);
        DT.addStateOperation(operationsDown);
    }

    /**
     * Creates sample deployment diagram nodes and communications links.
     */
    public void createSampleData() {
        var deployment = model.getDeploymentDiagram();
        
        var A = deployment.createDeploymentTarget(null);
        var ST_A_1 = new State("ST_A_1");
        A.addState(ST_A_1);
        A.addState(new State("ST_A_2"));
        A.addState(new State("ST_A_3"));
        var ST_A_1_op = new StateOperation(ST_A_1);
        A.addStateOperation(ST_A_1_op);
        var A_OP_1 = new OperationType("A_OP_1");
        deployment.addOperationType(A_OP_1);
        ST_A_1_op.addOperationEntry(new OperationEntry(A_OP_1, null));
        A.getNameProperty().setValue("A");
        
        var AA = deployment.createDeploymentTarget(A);
        AA.getNameProperty().setValue("AA");
        var ST_B_1 = new State("ST_B_1");
        AA.addState(ST_B_1);
        AA.addState(new State("ST_B_2"));
        AA.addState(new State("ST_B_3"));
        var ST_B_1_op = new StateOperation(ST_B_1);
        AA.addStateOperation(ST_B_1_op);
        var B_OP_1 = new OperationType("B_OP_1");
        deployment.addOperationType(B_OP_1);
        ST_B_1_op.addOperationEntry(new OperationEntry(B_OP_1, null));

        
        var AAA = deployment.createDeploymentTarget(AA);
        AAA.getNameProperty().setValue("AAA");
        
        var B = deployment.createDeploymentTarget(null);
        B.getNameProperty().setValue("B");
        var BB = deployment.createDeploymentTarget(B);
        BB.getNameProperty().setValue("BB");
        var BBB = deployment.createDeploymentTarget(BB);
        BBB.getNameProperty().setValue("BBB");
        
        deployment.createCommunicationLink(A, B);
    }

    public MainView getView(){
        return view;
    }
    
    private void deploymentDiagramInit(DeploymentDiagram deployment){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        
        var connectionContainer = deploymentDiagramView.getConnectionContainer();
        connectionContainer.connectionProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null)
                    return;
                
                var firstElementID = connectionContainer.getFirstElementID();
                var secondElementID = connectionContainer.getSecondElementID();
                if(firstElementID != null){
                    var firstDTView = deploymentDiagramView.getDeploymentTargetView(firstElementID.intValue());
                    firstDTView.setSelected(true);
                    
                    if(secondElementID != null){
                        if(connectionContainer.getFirstElement() instanceof DeploymentTargetView){
                            var firstDT = deployment.getDeploymentTarget(firstElementID.intValue());
                            var secondDT = deployment.getDeploymentTarget(secondElementID.intValue());
                            if(deployment.areNodesConnected(firstDT, secondDT)){
                                System.err.println("Error: Nodes \"" + firstDT.getNameProperty().getValue() + "\" and \"" + 
                                         secondDT.getNameProperty().getValue() + "\" are already connected!");
                            }
                            else{
                                deployment.createCommunicationLink(firstDT, secondDT);
                            }
                        }
                        firstDTView.setSelected(false);
                        connectionContainer.clear();
                    }
                }
            }
        });
        
        
        deployment.addCommunicationLinksChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    if(change.getValueAdded() instanceof CommunicationLink) {
                        var newConnection = (CommunicationLink) change.getValueAdded();
                        var firstID = newConnection.getFirst().getObjectInfo().getID();
                        var secondID = newConnection.getSecond().getObjectInfo().getID();
                        var newConnectionView = deploymentDiagramView.createConnection(firstID, secondID, newConnection.getObjectInfo().getID());
                        
                        var controller = new CommunicationLinkController(model, view, newConnection, newConnectionView);
//                        communicationLinkMenuInit(newConnectionView);
//                        communicationLinkAnnotationsInit(newConnection);
//                        createSampleAnnotations(newConnection);
                    }
                }
                if(change.wasRemoved()){
                    if(change.getValueRemoved() instanceof CommunicationLink) {
                        var removedConnection = (CommunicationLink) change.getValueRemoved();
                        deploymentDiagramView.removeConnection(removedConnection.getObjectInfo().getID());
                    }
                }
            }
        });
        
        
        deployment.addAllNodesChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof DeploymentTarget){
                        var newDT = (DeploymentTarget) newNode;
                        
                        DeploymentTargetView newDTParent = null;
                        if(newDT.getParent() != null)
                            newDTParent = deploymentDiagramView.getDeploymentTargetView(newDT.getParent().getObjectInfo().getID());

                        var newDTView = deploymentDiagramView.createDeploymentTargetView(newDTParent, newDT.getObjectInfo().getID());
                        newDTView.getNameProperty().bind(newDT.getNameProperty());
                        redundancyGroupInit(newDT, newDTView);
                        deploymentTargetMenuInit(newDTView);
                        deploymentTargetAnnotationsInit(newDT);
                    }
                    else if(newNode instanceof Artifact){
                        Artifact newArtifact = (Artifact) newNode;
                        var newArtifactParent = deploymentDiagramView.getDeploymentTargetView(newArtifact.getParent().getObjectInfo().getID());
                        var newArtifactView = deploymentDiagramView.CreateArtifact(newArtifactParent, newArtifact.getObjectInfo().getID());
                        newArtifactView.getNameProperty().bind(newArtifact.getNameProperty());
                        artifactMenuInit(newArtifactView);
                    }
                }
                else if(change.wasRemoved()){
                    var removedNode = (NamedNode) change.getValueRemoved();
                    deploymentDiagramView.removeNode(removedNode.getObjectInfo().getID());
                }
            }
        });

        Menu addNodeMenu = new Menu("Add Node");

        MenuItem deviceMenuItem = new MenuItem("Deployment target");
        
        EventHandler<ActionEvent> menuEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(deviceMenuItem)){
                    DeploymentTarget newDT = deployment.createDeploymentTarget(null);
                    
                    createSampleAnnotations(newDT);
                }
            }
        };
        
        deviceMenuItem.setOnAction(menuEventHandler);
        addNodeMenu.getItems().addAll(deviceMenuItem);
        deploymentDiagramView.addMenu(addNodeMenu);
        
        Menu globalMenu = new Menu("Global");
        MenuItem operationTypesMenuItem = new MenuItem("Operation types");
        operationTypesMenuItem.setOnAction((e) -> {
            var operationTypesView = createOperationTypesView();
            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(operationTypesView);

            this.view.createPropertiesModalWindow("Operation types", sections);
        });
        globalMenu.getItems().addAll(operationTypesMenuItem);
        
        var allOperationTypes = deployment.getOperationTypes();
        allOperationTypes.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        change.getRemoved().forEach(removedItem -> {
                            deployment.getNodes().forEach(node -> {
                                if(node instanceof DeploymentTarget) {
                                    ((DeploymentTarget) node).getStateOperations().forEach(stateOperation -> {
                                        stateOperation.getOperationEntries().removeIf(operationEntry -> removedItem.equals(operationEntry.getOperationType()));
                                    });
                                }
                            });
                        });
                    }
                }
            }
        });
        
        MenuItem redundancyGroupsMenuItem = new MenuItem("Redundancy groups");
        redundancyGroupsMenuItem.setOnAction((e) -> {
            var redundancyGroupsView = createRedundancyGroupsView();
            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(redundancyGroupsView);

            this.view.createPropertiesModalWindow("Redundancy groups", sections);
        });
        globalMenu.getItems().addAll(redundancyGroupsMenuItem);
        
        deploymentDiagramView.addMenu(globalMenu);
    }

    private void redundancyGroupInit(DeploymentTarget newDT, DeploymentTargetView newDTView) {
        newDT.redundancyGroupProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null) {
                    newDTView.getNameProperty().bind(newDT.getNameProperty());
                }
                else{
                    newDTView.getNameProperty().bind(Bindings.format("[%d.] ", newDT.getRedundancyGroup().getGroupID()).concat(newDT.getNameProperty()));
                }
            }
            
        });
    }
    
    private EditableListView createOperationTypesView(){
        var deploymentDiagram = model.getDeploymentDiagram();
        var operationTypes = deploymentDiagram.getOperationTypes();
        var operationTypesView = new EditableListView("Operation Types:", operationTypes);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                deploymentDiagram.addOperationType(new OperationType("New operation"));
            }
        };

        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (OperationType) operationTypesView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) operationTypesView.getScene().getWindow(), 
                            "Confirm", "The operation type \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        deploymentDiagram.removeOperationType(selected);
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (OperationType) operationTypesView.getSelected();
                view.createStringModalWindow("Rename", "New name", selected.nameProperty(), null);
                operationTypesView.refresh();
            }
        };
        
        operationTypesView.createButton("Add", addBtnHandler, false);
        operationTypesView.createButton("Remove", removeBtnHandler, true);
        operationTypesView.createButton("Edit", editBtnHandler, true);
        return operationTypesView;
    }

    private EditableListView createRedundancyGroupsView(){
        var deploymentDiagram = model.getDeploymentDiagram();
        var redundancyGroups = deploymentDiagram.getRedundancyGroups();
        var redundancyGroupsView = new EditableListView("Redundancy Groups:", redundancyGroups);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                deploymentDiagram.createRedundancyGroup();
            }
        };

        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (RedundancyGroup) redundancyGroupsView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) redundancyGroupsView.getScene().getWindow(), 
                            "Confirm", "The redundancy group \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        deploymentDiagram.removeRedundancyGroup(selected);
                    }
                }
            }
        };

        redundancyGroupsView.createButton("Add", addBtnHandler, false);
        redundancyGroupsView.createButton("Remove", removeBtnHandler, true);
        return redundancyGroupsView;
    }


    private void deploymentTargetAnnotationsInit(DeploymentTarget DT){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        var DTView = deploymentDiagramView.getDeploymentTargetView(DT.getObjectInfo().getID());
        
        DTView.getStatesAnnotation().setItems(DT.getStates());
        DTView.getStateTransitionsAnnotation().setItems(DT.getStateTransitions());
        DTView.getStateOperationsAnnotation().setItems(DT.getStateOperations());
        
        State stateUp = new State("UP");
//        stateUp.setLocked(true);

        State stateDown = new State("DOWN");
        stateDown.setLocked(true);
        stateDown.setStateDOWN(true);

        DT.addState(stateUp);
        DT.addState(stateDown);
        DT.setDefaultState(stateUp);

        StateTransition upDownTransition = new StateTransition(stateUp, stateDown, "Failure", 0.01);
//        upDownTransition.setLocked(true);
        
        StateTransition downUpTransition = new StateTransition(stateDown, stateUp, "Restart", 0.5);
//        downUpTransition.setLocked(true);
        
        DT.addStateTransition(upDownTransition);
        DT.addStateTransition(downUpTransition);
    }
    
    private void deploymentTargetMenuInit(DeploymentTargetView deploymentTargetView){
        var deploymentDiagram = this.model.getDeploymentDiagram();
        var deploymentTargetObjectID = deploymentTargetView.getObjectInfo().getID();
        var deploymentTarget = deploymentDiagram.getDeploymentTarget(deploymentTargetObjectID);
        
        if(deploymentTarget == null){
            System.err.println("Deployment target with id " + deploymentTargetObjectID + " was not found!");
            return;
        }
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) deploymentTargetView.getScene().getWindow(), 
                        "Confirm", "The deployment target \"" + Utils.shortenString(deploymentTarget.getNameProperty().getValue(), 50) + "\" will be deleted. Proceed?");
            confirmWindow.showAndWait();
            if(confirmWindow.getResult()){
                deploymentDiagram.removeNode(deploymentTargetObjectID);
            }
        });
        deploymentTargetView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemRename = new MenuItem("Rename");
        menuItemRename.setOnAction((e) -> {
            this.view.createStringModalWindow("Rename", "New name", deploymentTarget.getNameProperty(), Utils.SPNP_NAME_RESTRICTION_REGEX);
        });
        deploymentTargetView.addMenuItem(menuItemRename);

        
//        MenuItem menuItemToggleAnnotations = createToggleAnnotationsMenuItem(deploymentTargetView);
//        deploymentTargetView.addMenuItem(menuItemToggleAnnotations);
        deploymentTargetView.createToggleAnnotationsMenuItem();


        MenuItem menuItemAddArtifact = new MenuItem("Add artifact");

        menuItemAddArtifact.setOnAction((e) -> {
            deploymentDiagram.createArtifact(deploymentTarget);
        });
        
        deploymentTargetView.addMenuItem(menuItemAddArtifact);
        
        MenuItem menuItemAddDT = new MenuItem("Add deployment target");
        
        menuItemAddDT.setOnAction((e) -> {
            deploymentDiagram.createDeploymentTarget(deploymentTarget);
        });
        
        deploymentTargetView.addMenuItem(menuItemAddDT);


        MenuItem menuItemConnect = new MenuItem("Connect");
        menuItemConnect.setOnAction((e) -> {
            this.view.getDeploymentDiagramView().startConnection(deploymentTargetView);
        });
        deploymentTargetView.addMenuItem(menuItemConnect);
       
        SeparatorMenuItem separator = new SeparatorMenuItem();
        deploymentTargetView.addMenuItem(separator);
        
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction((e) -> {
            var stateTransitionsView = createStateTransitionsProperties(deploymentTarget);
            var stateOperationsView = createStateOperationsProperties(deploymentTarget);
            var statesView = createStatesProperties(deploymentTarget, () -> {
                stateTransitionsView.refresh();
                stateOperationsView.refresh();
            });
            var redundancyGroupView = createRedundancyGroupView(deploymentTarget);

            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(statesView);
            sections.add(stateTransitionsView);
            sections.add(stateOperationsView);
            sections.add(redundancyGroupView);

            this.view.createPropertiesModalWindow("\"" + deploymentTarget.getNameProperty().getValue() + "\" properties", sections);
        });
        deploymentTargetView.addMenuItem(menuProperties);
        
        // Remove State Transitions and State Operations when corresponding State is removed
        deploymentTarget.addStatesChangeListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        change.getRemoved().forEach(removedItem -> {
                            deploymentTarget.getStateTransitions().removeIf(transition -> 
                                            transition.getStateFrom().equals(removedItem) || 
                                            transition.getStateTo().equals(removedItem));
                            deploymentTarget.getStateOperations().removeIf(operation -> 
                                            operation.getState().equals(removedItem));
          
                        });
                    }
                }
            }
        });
    }
    
    private void artifactMenuInit(ArtifactView artifactView){
        var deploymentDiagram = model.getDeploymentDiagram();
        var artifact = deploymentDiagram.getNode(artifactView.getObjectInfo().getID());
        MenuItem menuItemDelete = new MenuItem("Delete");

        menuItemDelete.setOnAction((e) -> {
            BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) artifactView.getScene().getWindow(), 
                        "Confirm", "The artifact \"" + Utils.shortenString(artifact.getNameProperty().getValue(), 50) + "\" will be deleted. Proceed?");
            confirmWindow.showAndWait();
            if(confirmWindow.getResult()){
                this.model.getDeploymentDiagram().removeNode(artifactView.getObjectInfo().getID());
            }
        });
        artifactView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemRename = new MenuItem("Rename");
        menuItemRename.setOnAction((e) -> {
            this.view.createStringModalWindow("Rename", "New name", artifact.getNameProperty(), Utils.SPNP_NAME_RESTRICTION_REGEX);
        });
        artifactView.addMenuItem(menuItemRename);
    }

    private EditableListView createStatesProperties(DeploymentTarget deploymentTarget, Runnable refreshCallback){
        var states = deploymentTarget.getStates();
        var statesView = new EditableListView("States:", states);
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                deploymentTarget.addState(new State("New state"));
            }
        };

        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (State) statesView.getSelected();
                if(selected != null){
                    if(selected.isLocked()) {
                        var alert = Utils.createAlertDialog("Operation error", "Operation not allowed!", "This state can not be removed.");
                        alert.showAndWait();
                    }
                    else{
                        BooleanModalWindow confirmWindow = 
                                new BooleanModalWindow((Stage) statesView.getScene().getWindow(), 
                                "Confirm", "The state \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                        confirmWindow.showAndWait();
                        if(confirmWindow.getResult()){
                            states.remove(selected);
                        }
                    }
                }
            }
        };

        var renameBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (State) statesView.getSelected();
                if(selected != null){
                    if(selected.isLocked()) {
                        var alert = Utils.createAlertDialog("Operation error", "Operation not allowed!", "This state can not be edited.");
                        alert.showAndWait();
                    }
                    else {
                        StringModalWindow renameWindow = new StringModalWindow((Stage) statesView.getScene().getWindow(), 
                                "Rename state", "Type new name of the state \"" + Utils.shortenString(selected.toString(), 50) + "\":", selected.nameProperty());
                        renameWindow.setStringRestrictionRegex(Utils.SPNP_NAME_RESTRICTION_REGEX);
                        renameWindow.showAndWait();
                        statesView.refresh();
                        refreshCallback.run();
                    }
                }
            }
        };

        var setDefaultBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (State) statesView.getSelected();
                if(selected != null){
                    deploymentTarget.setDefaultState(selected);
                    statesView.refresh();
                    refreshCallback.run();
                }
            }
        };

        statesView.createButton("Add", addBtnHandler, false);
        statesView.createButton("Remove", removeBtnHandler, true);
        statesView.createButton("Rename", renameBtnHandler, true);
        statesView.createButton("Set default", setDefaultBtnHandler, true);
        
        return statesView;
    }


    private EditableListView createStateTransitionsProperties(DeploymentTarget deploymentTarget){
        var states = deploymentTarget.getStates();
        var transitions = deploymentTarget.getStateTransitions();
        var transitionsView = new EditableListView("State Transitions:", transitions);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                // Button is disabled when there are not at least 2 states
                var state1 = states.get(0);
                var state2 = states.get(1);
                deploymentTarget.addStateTransition(new StateTransition(state1, state2, "New Transition", 1.0));
            }
        };
        
        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateTransition) transitionsView.getSelected();
                if(selected != null){
                    if(selected.isLocked()) {
                        var alert = Utils.createAlertDialog("Operation error", "Operation not allowed!", "This transition can not be removed.");
                        alert.showAndWait();
                    }
                    else {
                        BooleanModalWindow confirmWindow = 
                                new BooleanModalWindow((Stage) transitionsView.getScene().getWindow(), 
                                "Confirm", "The transition \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                        confirmWindow.showAndWait();
                        if(confirmWindow.getResult()){
                            transitions.remove(selected);
                        }
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateTransition) transitionsView.getSelected();
                if(selected != null){
                    EditTransitionModalWindow editWindow = new EditTransitionModalWindow(   (Stage) transitionsView.getScene().getWindow(),
                                                                                            "Edit transition",
                                                                                            selected.nameProperty(),
                                                                                            selected.rateProperty(),
                                                                                            selected.fromStateProperty(),
                                                                                            selected.toStateProperty(),
                                                                                            states
                                                                                            );
                    editWindow.showAndWait();
                    transitionsView.refresh();
                }
            }
        };

        var addBtn = transitionsView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(states).lessThan(2));
        transitionsView.createButton("Remove", removeBtnHandler, true);
        transitionsView.createButton("Edit", editBtnHandler, true);
        return transitionsView;
    }
    
    private EditableListView createStateOperationsProperties(DeploymentTarget deploymentTarget){
        var statesWithNoOperations = deploymentTarget.getStatesWithoutOperations();
        var operations = deploymentTarget.getStateOperations();
        var operationsView = new EditableListView("Supported Operations:", operations);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                // Button is disabled when there is not at least 1 state available
                deploymentTarget.addStateOperation(new StateOperation(statesWithNoOperations.get(0)));
            }
        };
        
        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateOperation) operationsView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) operationsView.getScene().getWindow(), 
                            "Confirm", "The operation \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        deploymentTarget.removeStateOperation(selected);
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateOperation) operationsView.getSelected();
                if(selected != null){
                    EditableListView operationEntriesView = createStateOperationEntriesProperties(selected);
                    
                    EditOperationModalWindow editWindow = new EditOperationModalWindow(   (Stage) operationsView.getScene().getWindow(),
                                                                                            "Edit operation",
                                                                                            selected.stateProperty(),
                                                                                            statesWithNoOperations,
                                                                                            operationEntriesView
                                                                                            );
                    editWindow.showAndWait();
                    operationsView.refresh();
                }
            }
        };
        
        var addBtn = operationsView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(statesWithNoOperations).lessThan(1));
        operationsView.createButton("Remove", removeBtnHandler, true);
        operationsView.createButton("Edit", editBtnHandler, true);
        return operationsView;
    }

    private ObservableList getAvailableOperationTypes(ObservableList allOperationTypes, ObservableList<OperationEntry> operationEntries) {
        var availableOperationTypes = FXCollections.observableArrayList(allOperationTypes);
        operationEntries.forEach(opEntry -> {
            availableOperationTypes.remove(opEntry.getOperationType());
        });
        return availableOperationTypes;
    }

    private EditableListView createStateOperationEntriesProperties(StateOperation operation){
        var allOperationTypes = model.getDeploymentDiagram().getOperationTypes();
        var operationEntries = operation.getOperationEntries();
        var operationEntriesView = new EditableListView("Operations:", operation.getOperationEntries());

        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var operationEntry = new OperationEntry(null, -1);
                var availableOperationTypes = getAvailableOperationTypes(allOperationTypes, operationEntries);
                var editWindow = new EditOperationEntryModalWindow( (Stage) operationEntriesView.getScene().getWindow(),
                                                    "Edit Operation Entry",
                                                    availableOperationTypes,
                                                    operationEntry.operationTypeProperty(),
                                                    operationEntry.speedLimitProperty()
                                                    );
                editWindow.showAndWait();
                if(operationEntry.getOperationType() != null){
                    boolean alreadyIn = false;
                    for(var opEntry : operationEntries){
                        if(opEntry.getOperationType() == operationEntry.getOperationType()) {
                            alreadyIn = true;
                            break;
                        }
                    }
                    if(!alreadyIn)
                        operationEntries.add(operationEntry);
                }
                operationEntriesView.refresh();
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (OperationEntry) operationEntriesView.getSelected();
                var availableOperationTypes = getAvailableOperationTypes(allOperationTypes, operationEntries);
                var editWindow = new EditOperationEntryModalWindow( (Stage) operationEntriesView.getScene().getWindow(),
                                                    "Edit Operation Entry",
                                                    availableOperationTypes,
                                                    selected.operationTypeProperty(),
                                                    selected.speedLimitProperty()
                                                    );
                editWindow.showAndWait();

                if(selected.getOperationType() == null){
                    operationEntries.remove(selected);
                }

                operationEntriesView.refresh();
            }
        };

        
        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (OperationEntry) operationEntriesView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) operationEntriesView.getScene().getWindow(), 
                            "Confirm", "The operation entry \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        operationEntries.remove(selected);
                    }
                }
            }
        };
        
        var addButton = operationEntriesView.createButton("Add", addBtnHandler, false);
        addButton.disableProperty().bind(Bindings.size(operationEntries).greaterThanOrEqualTo(Bindings.size(allOperationTypes)));

        operationEntriesView.createButton("Edit", editBtnHandler, true);
        operationEntriesView.createButton("Remove", removeBtnHandler, true);
        return operationEntriesView;
    }

    private EditableListView createRedundancyGroupView(DeploymentTarget deploymentTarget){
        var deploymentDiagram = model.getDeploymentDiagram();
        var redundancyGroups = deploymentDiagram.getRedundancyGroups();
        var redundancyGroupsView = new EditableListView("Redundancy group:", redundancyGroups);
        
        var selectBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (RedundancyGroup) redundancyGroupsView.getSelected();
                if(selected != null){
                    var currentRG = deploymentTarget.getRedundancyGroup();
                    if(currentRG != null)
                        currentRG.removeNode(deploymentTarget);
                    deploymentTarget.setRedundancyGroup(selected);
                    selected.addNode(deploymentTarget);
                }
            }
        };
        
        var clearBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var currentRG = deploymentTarget.getRedundancyGroup();
                if(currentRG != null) {
                    currentRG.removeNode(deploymentTarget);
                    deploymentTarget.setRedundancyGroup(null);
                }
            }
        };

        redundancyGroupsView.createButton("Select", selectBtnHandler, true);
        var clearButton = redundancyGroupsView.createButton("Clear", clearBtnHandler, false);
        clearButton.disableProperty().bind(deploymentTarget.redundancyGroupProperty().isNull());
        
        return redundancyGroupsView;
    }

}
