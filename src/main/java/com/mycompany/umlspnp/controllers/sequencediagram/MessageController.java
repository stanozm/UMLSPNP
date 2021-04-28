package com.mycompany.umlspnp.controllers.sequencediagram;

import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.models.ConnectionFailure;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.OperationType;
import com.mycompany.umlspnp.models.sequencediagram.ExecutionTime;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import com.mycompany.umlspnp.models.sequencediagram.MessageSize;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.common.layouts.EditFailureTypeModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.PropertiesModalWindow;
import com.mycompany.umlspnp.views.sequencediagram.LoopView;
import com.mycompany.umlspnp.views.sequencediagram.MessageView;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 *  Controller which handles all functionalities within the message
 * and provides a model-view binding.
 * 
 */

public class MessageController {
    private final MainModel mainModel;
    private final MainView mainView;

    private final Message model;
    private final MessageView view;
    
    public MessageController(  MainModel mainModel,
                               MainView mainView,
                               Message model,
                               MessageView view) {
        this.mainModel = mainModel;
        this.mainView = mainView;
        this.model = model;
        this.view = view;
        
        messageInit();
        messageMenuInit();
        messageAnnotationsInit();
    }
    
    public Message getModel() {
        return model;
    }
    
    public MessageView getView() {
        return view;
    }

    private void messageInit(){
        model.orderProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(model.isLeafMessage()) {
                    view.getExecutionTimeAnnotation().setDisplayed(true);
                    view.getFailureTypesAnnotation().setDisplayed(true);
                }
                else {
                    view.getExecutionTimeAnnotation().setDisplayed(false);
                    view.getFailureTypesAnnotation().setDisplayed(false);
                }
            }
        });

        view.loopProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(oldValue != null){
                    var loopID = ((LoopView) oldValue).getObjectInfo().getID();
                    var loop = mainModel.getSequenceDiagram().getLoop(loopID);
                    if(loop != null)
                        loop.removeMessage(model);
                }
                if(newValue != null) {
                    var loopID = ((LoopView) newValue).getObjectInfo().getID();
                    var loop = mainModel.getSequenceDiagram().getLoop(loopID);
                    if(loop != null)
                        loop.addMessage(model);
                }
            }
        });

        view.nameProperty().bind(model.orderProperty().asString().concat(". ").concat(model.nameProperty()));
    }
    
    private void messageMenuInit(){
        var sequenceDiagram = this.mainModel.getSequenceDiagram();
        var messageObjectID = view.getObjectInfo().getID();
        
        var deletePromptText = String.format("The message \"%s\" will be deleted. Proceed?",
                                       Utils.shortenString(model.nameProperty().getValue(), 50));

        view.createConfirmMenu("Delete", deletePromptText,
                               () -> {sequenceDiagram.removeMessage(messageObjectID);});

        view.createStringMenu("Rename", "Rename message", "New name",
                              model.nameProperty(), Utils.SPNP_NAME_RESTRICTION_REGEX);

        view.createToggleAnnotationsMenu();
        view.createMenuSeparator();
        
        var executionTimeView = createExecutionTimeProperties();
        var messageSizeView = createMessageSizeProperties();
        var operationTypeView = createOperationTypeProperties();
        var failureTypesView = createMessageFailureTypesProperties();

        var propertiesWindowName = String.format("\"%s\" properties", model.nameProperty().getValue());
        view.createMessagePropertiesMenu((e) -> {
            boolean messageIsLeaf = model.isLeafMessage();
            ArrayList<EditableListView> sections = new ArrayList<>();
            
            if(messageIsLeaf)
                sections.add(executionTimeView);
            if(model.getCommunicationLink() != null)
                sections.add(messageSizeView);
            sections.add(operationTypeView);
            if(messageIsLeaf)
                sections.add(failureTypesView);
            
            var modal = new PropertiesModalWindow((Stage) view.getScene().getWindow(),
                                                   propertiesWindowName, sections);
            modal.showAndWait();
        });
    }
    
    private EditableListView createExecutionTimeProperties(){
        var executionTimeList = model.getExecutionTimeList();
        var executionTimeView = new EditableListView("Execution time:", executionTimeList);

        var editBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (ExecutionTime) executionTimeView.getSelected();
            if(selected != null){
                view.createIntegerModalWindow("Edit execution time",
                                              "Execution time",
                                              0,
                                              null,
                                              selected.executionTimeProperty());
                executionTimeView.refresh();
            }
        };

        executionTimeView.createButton("Edit", editBtnHandler, true);
        return executionTimeView;
    }
    
    private EditableListView createMessageSizeProperties(){
        var messageSizeList = model.getMessageSizeList();
        var messageSizeView = new EditableListView("Message size:", messageSizeList);

        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            model.setMessageSize(1);
        };

        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            Runnable callback = () -> {
                model.removeMessageSize();
            };
            var promptText = "The message size will be deleted. Proceed?";
            view.createBooleanModalWindow("Confirm", promptText, callback, null);
        };
        
        var editBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (MessageSize) messageSizeView.getSelected();
            if(selected != null){
                view.createIntegerModalWindow("Edit message size",
                                              "Message size",
                                              1,
                                              null,
                                              selected.messageSizeProperty());
                messageSizeView.refresh();
            }
        };

        var addBtn = messageSizeView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(model.getMessageSizeList()).greaterThan(0));

        var removeBtn = messageSizeView.createButton("Remove", removeBtnHandler, false);
        removeBtn.disableProperty().bind(Bindings.size(model.getMessageSizeList()).lessThan(1));
        
        messageSizeView.createButton("Edit", editBtnHandler, true);
        
        return messageSizeView;
    }
    
    private EditableListView createOperationTypeProperties(){
        var operationEntriesList = mainModel.getDeploymentDiagram().getOperationTypes();
        var operationEntriesView = new EditableListView("Operation type:", operationEntriesList);

        var selectBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (OperationType) operationEntriesView.getSelected();
            if(selected != null){
                model.setOperationType(selected);
            }
        };
        
        var clearBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            model.removeOperationType();
        };

        operationEntriesView.createButton("Select", selectBtnHandler, true);
        
        var clearBtn = operationEntriesView.createButton("Clear", clearBtnHandler, false);
        clearBtn.disableProperty().bind(Bindings.size(model.getOperationTypeList()).lessThan(1));

        return operationEntriesView;
    }

    private EditableListView createMessageFailureTypesProperties(){
        var failures = model.getMessageFailures();
        var failuresView = new EditableListView("Failure types:", failures);
        
        var addBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            model.addMessageFailure(new ConnectionFailure("New failure", 0.01));
        };

        var removeBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (ConnectionFailure) failuresView.getSelected();
            if(selected != null){
                Runnable callback = () -> {
                    failures.remove(selected);
                };
                var promptText = String.format("The failure type \"%s\" will be deleted. Proceed?",
                                                Utils.shortenString(selected.toString(), 50));
                view.createBooleanModalWindow("Confirm", promptText, callback, null);
            }
        };

        var editBtnHandler = (EventHandler<ActionEvent>) (ActionEvent e) -> {
            var selected = (ConnectionFailure) failuresView.getSelected();
            if(selected != null){
                var editWindow = new EditFailureTypeModalWindow(
                                        (Stage) failuresView.getScene().getWindow(),
                                        "Edit failure type",
                                        selected.nameProperty(),
                                        selected.rateProperty());
                editWindow.showAndWait();
                failuresView.refresh();
            }
        };

        failuresView.createButton("Add", addBtnHandler, false);
        failuresView.createButton("Remove", removeBtnHandler, true);
        failuresView.createButton("Edit", editBtnHandler, true);
        
        return failuresView;
    }
    
    private void messageAnnotationsInit(){
        view.getExecutionTimeAnnotation().setItems(model.getExecutionTimeList());
        view.getOperationTypeAnnotation().setItems(model.getOperationTypeList());
        view.getFailureTypesAnnotation().setItems(model.getMessageFailures());
        view.getMessageSizeAnnotation().setItems(model.getMessageSizeList());
    }
    
    public void addSortMessagesCallback(Runnable callback) {
        view.getSourceConnectionSlot().localToSceneTransformProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                callback.run();
            }
        });
    }
}
