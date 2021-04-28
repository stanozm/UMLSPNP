package com.mycompany.umlspnp.controllers.deploymentdiagram;

import com.mycompany.umlspnp.models.OperationEntry;
import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.controllers.BaseController;
import com.mycompany.umlspnp.views.*;
import com.mycompany.umlspnp.models.*;
import com.mycompany.umlspnp.models.OperationType;
import com.mycompany.umlspnp.models.deploymentdiagram.*;
import com.mycompany.umlspnp.views.common.layouts.BooleanModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentDiagramView;
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
 *  and provides a model-view binding.
 *
 */
public class DeploymentDiagramController extends BaseController<DeploymentDiagram, DeploymentDiagramView>{
    private final List<CommunicationLinkController> communicationLinkControllers;
    private final List<DeploymentTargetController> deploymentTargetControllers;
    private final List<ArtifactController> artifactControllers;
    
    public DeploymentDiagramController(MainModel mainModel, MainView mainView){
        super(mainModel, mainView, mainModel.getDeploymentDiagram(), mainView.getDeploymentDiagramView());

        communicationLinkControllers = new ArrayList<>();
        deploymentTargetControllers = new ArrayList<>();
        artifactControllers = new ArrayList<>();

        // Creation and removal of artifacts and deployment targets
        nodeManagerInit();
        
        // Creation and removal of communication links
        communicationLinkManagerInit();
        
        // Connection container for connecting two deployment targets with a communication link
        connectionContainerInit();
        
        // Node menu
        nodeMenuInit();
        
        // Global properties menu
        globalMenuInit();
    }

    /**
     * Creates sample deployment diagram nodes and communications links.
     */
    public void createSampleData() {
        model.addOperationType(new OperationType("ReadDeviceData"));
        model.addOperationType(new OperationType("WriteDeviceData"));
        
        var A = model.createDeploymentTarget(null);
        var ST_A_1 = new State("ST_A_1");
        A.addState(ST_A_1);
        A.addState(new State("ST_A_2"));
        A.addState(new State("ST_A_3"));
        var ST_A_1_op = new StateOperation(ST_A_1);
        A.addStateOperation(ST_A_1_op);
        var A_OP_1 = new OperationType("A_OP_1");
        model.addOperationType(A_OP_1);
        ST_A_1_op.addOperationEntry(new OperationEntry(A_OP_1, null));
        A.getNameProperty().setValue("A");
        
        var AA = model.createDeploymentTarget(A);
        AA.getNameProperty().setValue("AA");
        var ST_B_1 = new State("ST_B_1");
        AA.addState(ST_B_1);
        AA.addState(new State("ST_B_2"));
        AA.addState(new State("ST_B_3"));
        var ST_B_1_op = new StateOperation(ST_B_1);
        AA.addStateOperation(ST_B_1_op);
        var B_OP_1 = new OperationType("B_OP_1");
        model.addOperationType(B_OP_1);
        ST_B_1_op.addOperationEntry(new OperationEntry(B_OP_1, null));


        var AAA = model.createDeploymentTarget(AA);
        AAA.getNameProperty().setValue("AAA");
        
        var B = model.createDeploymentTarget(null);
        B.getNameProperty().setValue("B");
        var BB = model.createDeploymentTarget(B);
        BB.getNameProperty().setValue("BB");
        var BBB = model.createDeploymentTarget(BB);
        BBB.getNameProperty().setValue("BBB");
        
        model.createCommunicationLink(A, B);
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
                    var firstDTView = view.getDeploymentTargetView(firstElementID.intValue());
                    firstDTView.setSelected(true);
                    
                    if(secondElementID != null){
                        if(connectionContainer.getFirstElement() instanceof DeploymentTargetView){
                            var firstDT = model.getDeploymentTarget(firstElementID.intValue());
                            var secondDT = model.getDeploymentTarget(secondElementID.intValue());
                            if(model.areNodesConnected(firstDT, secondDT)){
                                System.err.println(String.format(
                                        "Error: Nodes \"%s\" and \"%s\" are already connected!",
                                        firstDT.getNameProperty().getValue(),
                                        secondDT.getNameProperty().getValue()));
                            }
                            else{
                                model.createCommunicationLink(firstDT, secondDT);
                            }
                        }
                        firstDTView.setSelected(false);
                        connectionContainer.clear();
                    }
                }
            }
        });
    }
    
    private void communicationLinkManagerInit() {
        model.addCommunicationLinksChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    if(change.getValueAdded() instanceof CommunicationLink) {
                        var newConnection = (CommunicationLink) change.getValueAdded();
                        var firstID = newConnection.getFirst().getObjectInfo().getID();
                        var secondID = newConnection.getSecond().getObjectInfo().getID();
                        var newConnectionView = view.createConnection(firstID, secondID, newConnection.getObjectInfo().getID());

                        var controller = new CommunicationLinkController(mainModel, mainView, newConnection, newConnectionView);
                        communicationLinkControllers.add(controller);
                    }
                }
                if(change.wasRemoved()){
                    if(change.getValueRemoved() instanceof CommunicationLink) {
                        var removedConnection = (CommunicationLink) change.getValueRemoved();
                        view.removeConnection(removedConnection.getObjectInfo().getID());
                        communicationLinkControllers.removeIf((controller) -> controller.getModel().equals(removedConnection));
                    }
                }
            }
        });
    }
    
    private void nodeManagerInit() {
        model.addAllNodesChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof DeploymentTarget){
                        var newDT = (DeploymentTarget) newNode;
                        
                        DeploymentTargetView newDTParent = null;
                        if(newDT.getParent() != null)
                            newDTParent = view.getDeploymentTargetView(newDT.getParent().getObjectInfo().getID());

                        var newDTView = view.createDeploymentTargetView(newDTParent, newDT.getObjectInfo().getID());
                        var controller = new DeploymentTargetController(mainModel, mainView, newDT, newDTView);
                        deploymentTargetControllers.add(controller);
                    }
                    else if(newNode instanceof Artifact){
                        Artifact newArtifact = (Artifact) newNode;
                        var newArtifactParent = view.getDeploymentTargetView(newArtifact.getParent().getObjectInfo().getID());
                        var newArtifactView = view.CreateArtifact(newArtifactParent, newArtifact.getObjectInfo().getID());
                        
                        var controller = new ArtifactController(mainModel, mainView, newArtifact, newArtifactView);
                        artifactControllers.add(controller);
                    }
                }
                else if(change.wasRemoved()){
                    var removedItem = change.getValueRemoved();
                    if(removedItem instanceof DeploymentTarget) {
                        deploymentTargetControllers.removeIf(controller -> controller.getModel().equals(removedItem));
                    }
                    else if(removedItem instanceof Artifact) {
                        artifactControllers.removeIf(controller -> controller.getModel().equals(removedItem));
                    }
                    var removedNode = (Artifact) change.getValueRemoved();
                    view.removeNode(removedNode.getObjectInfo().getID());
                }
            }
        });
    }
    
    private void nodeMenuInit() {
        Menu addNodeMenu = new Menu("Add Node");
        MenuItem deviceMenuItem = new MenuItem("Deployment target");
        
        EventHandler<ActionEvent> menuEventHandler = (ActionEvent tt) -> {
            if(tt.getSource().equals(deviceMenuItem))
                model.createDeploymentTarget(null);
        };
        
        deviceMenuItem.setOnAction(menuEventHandler);
        addNodeMenu.getItems().addAll(deviceMenuItem);
        view.addMenu(addNodeMenu);
    }
    
    private void globalMenuInit() {
        Menu globalMenu = new Menu("Global");
        MenuItem operationTypesMenuItem = new MenuItem("Operation types");
        operationTypesMenuItem.setOnAction((e) -> {
            var operationTypesView = createOperationTypesView();
            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(operationTypesView);

            this.mainView.createPropertiesModalWindow("Operation types", sections);
        });
        globalMenu.getItems().addAll(operationTypesMenuItem);

        MenuItem redundancyGroupsMenuItem = new MenuItem("Redundancy groups");
        redundancyGroupsMenuItem.setOnAction((e) -> {
            var redundancyGroupsView = createRedundancyGroupsView();
            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(redundancyGroupsView);

            this.mainView.createPropertiesModalWindow("Redundancy groups", sections);
        });
        globalMenu.getItems().addAll(redundancyGroupsMenuItem);
        
        view.addMenu(globalMenu);
        
        // Remove corresponding annotation data when the operation type is deleted
        globalOperationTypesInit();
    }
    
    private void globalOperationTypesInit() {
        var allOperationTypes = model.getOperationTypes();
        allOperationTypes.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasRemoved()) {
                        change.getRemoved().forEach(removedItem -> {
                            model.getNodes().forEach(node -> {
                                if(node instanceof DeploymentTarget) {
                                    ((DeploymentTarget) node).getStateOperations().forEach(stateOperation -> {
                                        stateOperation.getOperationEntries().removeIf(operationEntry -> 
                                                removedItem.equals(operationEntry.getOperationType()));
                                    });
                                }
                            });
                        });
                    }
                }
            }
        });
    }
    
    private EditableListView createOperationTypesView(){
        var operationTypes = model.getOperationTypes();
        var operationTypesView = new EditableListView("Operation Types:", operationTypes);
        
        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            model.addOperationType(new OperationType("New operation"));
        };

        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (OperationType) operationTypesView.getSelected();
            if(selected != null){
                var promptText = String.format("The operation type \"%s\" will be deleted. Proceed?",
                                               Utils.shortenString(selected.toString(), 50));
                BooleanModalWindow confirmWindow = new BooleanModalWindow(
                                                    (Stage) operationTypesView.getScene().getWindow(),
                                                    "Confirm",
                                                    promptText);
                confirmWindow.showAndWait();
                if(confirmWindow.getResult()){
                    model.removeOperationType(selected);
                }
            }
        };

        var editBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (OperationType) operationTypesView.getSelected();
            mainView.createStringModalWindow("Rename", "New name", selected.nameProperty(), null);
            operationTypesView.refresh();
        };
        
        operationTypesView.createButton("Add", addBtnHandler, false);
        operationTypesView.createButton("Remove", removeBtnHandler, true);
        operationTypesView.createButton("Edit", editBtnHandler, true);
        return operationTypesView;
    }

    private EditableListView createRedundancyGroupsView(){
        var redundancyGroups = model.getRedundancyGroups();
        var redundancyGroupsView = new EditableListView("Redundancy Groups:", redundancyGroups);
        
        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            model.createRedundancyGroup();
        };

        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (RedundancyGroup) redundancyGroupsView.getSelected();
            if(selected != null){
                var promptText = String.format("The redundancy group \"%s\" will be deleted. Proceed?",
                                               Utils.shortenString(selected.toString(), 50));
                BooleanModalWindow confirmWindow = new BooleanModalWindow(
                                                        (Stage) redundancyGroupsView.getScene().getWindow(),
                                                        "Confirm",
                                                        promptText);
                confirmWindow.showAndWait();
                if(confirmWindow.getResult()){
                    model.removeRedundancyGroup(selected);
                }
            }
        };

        redundancyGroupsView.createButton("Add", addBtnHandler, false);
        redundancyGroupsView.createButton("Remove", removeBtnHandler, true);
        return redundancyGroupsView;
    }
}
