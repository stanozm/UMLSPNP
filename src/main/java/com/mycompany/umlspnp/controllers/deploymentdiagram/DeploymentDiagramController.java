package com.mycompany.umlspnp.controllers.deploymentdiagram;

import com.mycompany.umlspnp.controllers.deploymentdiagram.DeploymentTargetController;
import com.mycompany.umlspnp.controllers.deploymentdiagram.CommunicationLinkController;
import com.mycompany.umlspnp.models.OperationEntry;
import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.views.*;
import com.mycompany.umlspnp.models.*;
import com.mycompany.umlspnp.models.OperationType;
import com.mycompany.umlspnp.models.deploymentdiagram.*;
import com.mycompany.umlspnp.views.common.layouts.BooleanModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.deploymentdiagram.ArtifactView;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 *  Controller which handles all functionalities within the deployment diagram
 * and binds deployment diagram model to its view.
 *
 */
public class DeploymentDiagramController {
    private final MainModel model;
    private final MainView view;
    
    private final List<CommunicationLinkController> communicationLinkContollers;
    private final List<DeploymentTargetController> deploymentTargetContollers;
    
    public DeploymentDiagramController(MainModel mainModel, MainView mainView){
        this.model = mainModel;
        this.view = mainView;
        
        communicationLinkContollers = new ArrayList<>();
        deploymentTargetContollers = new ArrayList<>();
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
                        communicationLinkContollers.add(controller);
                    }
                }
                if(change.wasRemoved()){
                    if(change.getValueRemoved() instanceof CommunicationLink) {
                        var removedConnection = (CommunicationLink) change.getValueRemoved();
                        deploymentDiagramView.removeConnection(removedConnection.getObjectInfo().getID());
                        communicationLinkContollers.removeIf((controller) -> controller.getModel().equals(removedConnection));
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
                        var controller = new DeploymentTargetController(model, view, newDT, newDTView);
                        deploymentTargetContollers.add(controller);
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
                    var removedItem = change.getValueRemoved();
                    if(removedItem instanceof DeploymentTarget) {
                        deploymentTargetContollers.removeIf(controller -> controller.getModel().equals(removedItem));
                    }
                    else if(removedItem instanceof Artifact) {
                        // ...
                    }
                    var removedNode = (Artifact) change.getValueRemoved();
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
}
