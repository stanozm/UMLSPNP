package cz.muni.fi.umlspnp.controllers.deploymentdiagram;

import cz.muni.fi.umlspnp.common.Utils;
import cz.muni.fi.umlspnp.controllers.BaseController;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.OperationEntry;
import cz.muni.fi.umlspnp.models.OperationType;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentTarget;
import cz.muni.fi.umlspnp.models.deploymentdiagram.RedundancyGroup;
import cz.muni.fi.umlspnp.models.deploymentdiagram.State;
import cz.muni.fi.umlspnp.models.deploymentdiagram.StateOperation;
import cz.muni.fi.umlspnp.models.deploymentdiagram.StateTransition;
import cz.muni.fi.umlspnp.views.MainView;
import cz.muni.fi.umlspnp.views.common.layouts.EditableListView;
import cz.muni.fi.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import cz.muni.fi.umlspnp.views.deploymentdiagram.EditOperationEntryModalWindow;
import cz.muni.fi.umlspnp.views.deploymentdiagram.EditOperationModalWindow;
import cz.muni.fi.umlspnp.views.deploymentdiagram.EditTransitionModalWindow;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 *  Controller which handles all functionalities within the deployment target
 * and provides a model-view binding.
 * 
 */
public class DeploymentTargetController extends BaseController<DeploymentTarget, DeploymentTargetView> {
    
    public DeploymentTargetController (MainModel mainModel,
                                       MainView mainView,
                                       DeploymentTarget model,
                                       DeploymentTargetView view) {
        super(mainModel, mainView, model, view);
        
        view.getNameProperty().bind(model.getNameProperty());
        redundancyGroupInit();
        deploymentTargetMenuInit();
        deploymentTargetAnnotationsInit();
    }
    
    private void redundancyGroupInit() {
        model.redundancyGroupProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null) {
                    view.getNameProperty().bind(model.getNameProperty());
                }
                else{
                    view.getNameProperty().bind(
                            Bindings.format("[%d.] ", model.getRedundancyGroup().getGroupID())
                            .concat(model.getNameProperty()));
                }
            }
        });
    }
    
    private void deploymentTargetMenuInit(){
        var deploymentDiagram = mainModel.getDeploymentDiagram();
        var deploymentDiagramView = mainView.getDeploymentDiagramView();
        var deploymentTargetObjectID = model.getObjectInfo().getID();
  
        var deletePromptText = String.format("The deployment target \"%s\" will be deleted. Proceed?",
                                       Utils.shortenString(model.getNameProperty().getValue(), 50));

        view.createConfirmMenu("Delete", deletePromptText,
                               () -> {deploymentDiagram.removeNode(deploymentTargetObjectID);});
        
        view.createStringMenu("Rename", "Rename deployment target", "New name",
                              model.getNameProperty(), Utils.SPNP_NAME_RESTRICTION_REGEX);

        view.createToggleAnnotationsMenu();

        view.createMenuItem("Add artifact", (e) -> {
            deploymentDiagram.createArtifact(model);
        });

        view.createMenuItem("Add deployment target", (e) -> {
            deploymentDiagram.createDeploymentTarget(model);
        });

        view.createMenuItem("Connect", (e) -> {
            deploymentDiagramView.startConnection(view);
        });

        view.createMenuSeparator();

        var stateTransitionsView = createStateTransitionsProperties();
        var stateOperationsView = createStateOperationsProperties();
        var statesView = createStatesProperties(() -> {
            stateTransitionsView.refresh();
            stateOperationsView.refresh();
        });
        var redundancyGroupView = createRedundancyGroupView();
        
        ArrayList<EditableListView> sections = new ArrayList<>();
        sections.add(statesView);
        sections.add(stateTransitionsView);
        sections.add(stateOperationsView);
        sections.add(redundancyGroupView);
        var propertiesWindowName = String.format("\"%s\" properties", model.getNameProperty().getValue());
        view.createPropertiesMenu(propertiesWindowName, sections);
    }
    
    private EditableListView createStateTransitionsProperties(){
        var states = model.getStates();
        var transitions = model.getStateTransitions();
        var transitionsView = new EditableListView("State Transitions:", transitions);
        
        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            if(states.size() >= 2) {
                var state1 = states.get(0);
                var state2 = states.get(1);
                model.addStateTransition(new StateTransition(state1, state2, "New Transition", 1.0));
            }
        };
        
        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (StateTransition) transitionsView.getSelected();
            if(selected != null){
                if(selected.isLocked()) {
                    view.showAlert("Operation error", "Operation not allowed!", "This transition can not be removed.");
                }
                else {
                    var promptText = String.format("The transition \"%s\" will be deleted. Proceed?", Utils.shortenString(selected.toString(), 50));
                    Runnable callback = () -> {
                        model.removeStateTransition(selected);
                    };
                    view.createBooleanModalWindow("Confirm", promptText, callback, null);
                }
            }
        };

        var editBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
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
        };

        var addBtn = transitionsView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(states).lessThan(2));
        transitionsView.createButton("Remove", removeBtnHandler, true);
        transitionsView.createButton("Edit", editBtnHandler, true);
        return transitionsView;
    }

    private EditableListView createStateOperationsProperties(){
        var statesWithNoOperations = model.getStatesWithoutOperations();
        var operations = model.getStateOperations();
        var operationsView = new EditableListView("Supported Operations:", operations);
        
        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            if(statesWithNoOperations.size() >= 1)
                model.addStateOperation(new StateOperation(statesWithNoOperations.get(0)));
        };
        
        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (StateOperation) operationsView.getSelected();
            if(selected != null){
                var promptText = String.format("The operation \"%s\" will be deleted. Proceed?", Utils.shortenString(selected.toString(), 50));
                Runnable callback = () -> {
                    model.removeStateOperation(selected);
                };
                view.createBooleanModalWindow("Confirm", promptText, callback, null);
            }
        };

        var editBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
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
        };
        
        var addBtn = operationsView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(statesWithNoOperations).lessThan(1));
        operationsView.createButton("Remove", removeBtnHandler, true);
        operationsView.createButton("Edit", editBtnHandler, true);
        return operationsView;
    }

    private ObservableList<OperationType> getAvailableOperationTypes(ObservableList<OperationType> allOperationTypes,
                                                                     List<OperationEntry> operationEntries) {
        return allOperationTypes.filtered(operationType -> {
            for(var operationEntry : operationEntries) {
                if(operationEntry.getOperationType().equals(operationType))
                    return false;
            }
            return true;
        });
    }
    
    private EditableListView createStateOperationEntriesProperties(StateOperation operation){
        var allOperationTypes = mainModel.getDeploymentDiagram().getOperationTypes();
        var operationEntries = operation.getOperationEntries();
        var operationEntriesView = new EditableListView("Operations:", operation.getOperationEntries());

        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
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
        };

        var editBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (OperationEntry) operationEntriesView.getSelected();
            var availableOperationTypes = getAvailableOperationTypes(allOperationTypes, operationEntries);
            var editWindow = new EditOperationEntryModalWindow( (Stage) operationEntriesView.getScene().getWindow(),
                    "Edit Operation Entry",
                    availableOperationTypes,
                    selected.operationTypeProperty(),
                    selected.speedLimitProperty()
            );
            editWindow.showAndWait();

            if(selected.getOperationType() == null)
                operationEntries.remove(selected);
            operationEntriesView.refresh();
        };

        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (OperationEntry) operationEntriesView.getSelected();
            if(selected != null){
                var promptText = String.format("The operation entry \"%s\" will be deleted. Proceed?", Utils.shortenString(selected.toString(), 50));
                Runnable callback = () -> {
                    operationEntries.remove(selected);
                };
                view.createBooleanModalWindow("Confirm", promptText, callback, null);
            }
        };
        
        var addButton = operationEntriesView.createButton("Add", addBtnHandler, false);
        addButton.disableProperty().bind(Bindings.size(operationEntries).greaterThanOrEqualTo(Bindings.size(allOperationTypes)));

        operationEntriesView.createButton("Edit", editBtnHandler, true);
        operationEntriesView.createButton("Remove", removeBtnHandler, true);
        return operationEntriesView;
    }

    private EditableListView createStatesProperties(Runnable refreshCallback){
        var states = model.getStates();
        var statesView = new EditableListView("States:", states);

        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            model.addState(new State("New state"));
        };

        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (State) statesView.getSelected();
            if(selected != null){
                if(selected.isLocked()) {
                    view.showAlert("Operation error", "Operation not allowed!", "This state can not be removed.");
                }
                else {
                    var promptText = String.format("The state \"%s\" will be deleted. Proceed?", Utils.shortenString(selected.toString(), 50));
                    Runnable callback = () -> {
                        model.removeState(selected);
                    };
                    view.createBooleanModalWindow("Confirm", promptText, callback, null);
                }
            }
        };

        var renameBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (State) statesView.getSelected();
            if(selected != null){
                if(selected.isLocked()) {
                    view.showAlert("Operation error", "Operation not allowed!", "This state can not be edited.");
                }
                else {
                    var promptText = String.format("New name of the state \"%s\":", Utils.shortenString(selected.toString(), 50));
                    view.createStringModalWindow("Rename state", promptText, selected.nameProperty(), Utils.SPNP_NAME_RESTRICTION_REGEX);
                    statesView.refresh();
                    refreshCallback.run();
                }
            }
        };

        var setDefaultBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (State) statesView.getSelected();
            if(selected != null){
                model.setDefaultState(selected);
                statesView.refresh();
                refreshCallback.run();
            }
        };

        statesView.createButton("Add", addBtnHandler, false);
        statesView.createButton("Remove", removeBtnHandler, true);
        statesView.createButton("Rename", renameBtnHandler, true);
        statesView.createButton("Set default", setDefaultBtnHandler, true);
        return statesView;
    }
    
    private EditableListView createRedundancyGroupView(){
        var deploymentDiagram = mainModel.getDeploymentDiagram();
        var redundancyGroups = deploymentDiagram.getRedundancyGroups();
        var redundancyGroupsView = new EditableListView("Redundancy group:", redundancyGroups);
        
        var selectBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (RedundancyGroup) redundancyGroupsView.getSelected();
            if(selected != null){
                var currentRG = model.getRedundancyGroup();
                if(currentRG != null)
                    currentRG.removeNode(model);
                model.setRedundancyGroup(selected);
                selected.addNode(model);
            }
        };

        var clearBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var currentRG = model.getRedundancyGroup();
            if(currentRG != null) {
                currentRG.removeNode(model);
                model.setRedundancyGroup(null);
            }
        };

        redundancyGroupsView.createButton("Select", selectBtnHandler, true);
        var clearButton = redundancyGroupsView.createButton("Clear", clearBtnHandler, false);
        clearButton.disableProperty().bind(model.redundancyGroupProperty().isNull());
        
        return redundancyGroupsView;
    }
    
    private void deploymentTargetAnnotationsInit(){
        view.getStatesAnnotation().setItems(model.getStates());
        view.getStateTransitionsAnnotation().setItems(model.getStateTransitions());
        view.getStateOperationsAnnotation().setItems(model.getStateOperations());
        
        State stateUp = new State("UP");
//        stateUp.setLocked(true);

        State stateDown = new State("DOWN");
        stateDown.setLocked(true);
        stateDown.setStateDOWN(true);

        model.addState(stateUp);
        model.addState(stateDown);
        model.setDefaultState(stateUp);

        StateTransition upDownTransition = new StateTransition(stateUp, stateDown, "Failure", 0.01);
//        upDownTransition.setLocked(true);
        
        StateTransition downUpTransition = new StateTransition(stateDown, stateUp, "Restart", 0.5);
//        downUpTransition.setLocked(true);
        
        model.addStateTransition(upDownTransition);
        model.addStateTransition(downUpTransition);
        
        if(Utils.__DEBUG_CREATE_SAMPLE_DATA)
            createSampleAnnotations();
    }
    
    /**
     * Creates sample annotations for a specified deployment target.
     * @param DT Deployment Target to have the sample annotations added.
     */
    private void createSampleAnnotations(){
        var deployment = mainModel.getDeploymentDiagram();

        var stateUp = model.getStates().get(0);
        var stateDown = model.getStates().get(1);

        var opTypes = deployment.getOperationTypes();
        var opTypesSize = opTypes.size();
        
        StateOperation operationsUp = new StateOperation(stateUp);
        StateOperation operationsDown = new StateOperation(stateDown);
        
        if(opTypesSize >= 1){
            operationsUp.addOperationEntry(opTypes.get(0), null);
        }
        if(opTypesSize >= 2){
            operationsUp.addOperationEntry(opTypes.get(1), null);
            operationsDown.addOperationEntry(opTypes.get(1), 50);
        }
        model.addStateOperation(operationsUp);
        model.addStateOperation(operationsDown);
    }
}
