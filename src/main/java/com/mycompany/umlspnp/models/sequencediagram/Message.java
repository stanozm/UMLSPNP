/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.Connection;
import com.mycompany.umlspnp.models.common.ConnectionFailure;
import com.mycompany.umlspnp.models.common.OperationEntry;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.Callback;

/**
 *
 * @author 10ondr
 */
public class Message extends Connection<Activation> {
    private final IntegerProperty order = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    
    private final ObservableList<ExecutionTime> executionTime; // Exactly 1 item
    private final ObservableList<MessageSize> messageSize; // Exactly 1 item
    private final ObservableList<ConnectionFailure> messageFailures;
    
    private OperationEntry operationType;
    private final FilteredList<OperationEntry> operationTypeList; // At most 1 item
    
    
    public Message(Activation from, Activation to) {
        super(from, to);
        
        name.setValue("newMessage()");
        
        executionTime = FXCollections.observableArrayList(
                new Callback<ExecutionTime, Observable[]>() {
                    @Override
                    public Observable[] call(ExecutionTime param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        messageSize = FXCollections.observableArrayList(
                new Callback<MessageSize, Observable[]>() {
                    @Override
                    public Observable[] call(MessageSize param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        messageFailures = FXCollections.observableArrayList(
                new Callback<ConnectionFailure, Observable[]>() {
                    @Override
                    public Observable[] call(ConnectionFailure param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        operationTypeList = new FilteredList<>(from.getLifeline().getOperationEntries(),
                                               item -> item.equals(operationType));

        setExecutionTime(1);
    }
    
    public Activation getFrom() {
        return this.getFirst();
    }
    
    public Activation getTo() {
        return this.getSecond();
    }
    
    public IntegerProperty orderProperty() {
        return order;
    }
    
    public Integer getOrder() {
        return order.getValue();
    }
    
    public void setOrder(int value) {
        order.set(value);
    }
    
    public StringProperty nameProperty(){
        return name;
    }
    
    public final void setExecutionTime(int newTime){
        if(executionTime.size() > 0){
            executionTime.get(0).setValue(newTime);
        }
        else{
            executionTime.add(new ExecutionTime(newTime));
        }
    }
    
    public ExecutionTime getExecutionTime(){
        return executionTime.get(0);
    }
    
    public int getExecutionTimeValue() {
        return executionTime.get(0).executionTimeProperty().getValue();
    }
    
    public void removeExecutionTime(){
        executionTime.clear();
    }
    
    public ObservableList getExecutionTimeList(){
        return executionTime;
    }
    
    public final void setMessageSize(int newMessageSize){
        if(messageSize.size() > 0){
            messageSize.get(0).setValue(newMessageSize);
        }
        else{
            messageSize.add(new MessageSize(newMessageSize));
        }
    }
    
    public MessageSize getMessageSize(){
        return messageSize.get(0);
    }
    
    public void removeMessageSize(){
        messageSize.clear();
    }
    
    public ObservableList<MessageSize> getMessageSizeList(){
        return messageSize;
    }
    
    public ObservableList<ConnectionFailure> getMessageFailures(){
        return messageFailures;
    }
    
    public void addMessageFailure(ConnectionFailure newMessageFailure){
        messageFailures.add(newMessageFailure);
    }
    
    public ObservableList<OperationEntry> getOperationEntries(){
        return getFirst().getLifeline().getOperationEntries();
    }
    
    public void setOperationType(OperationEntry newEntry){
        operationType = newEntry;
        operationTypeList.setPredicate(item -> item.equals(operationType)); // Refresh filtered list
    }
    
    public OperationEntry getOperationType(){
        return operationTypeList.get(0);
    }
    
    public void removeOperationType(){
        setOperationType(null);
    }
    
    public FilteredList<OperationEntry> getOperationTypeList(){
        return operationTypeList;
    }

    public final boolean isLeafMessage() {
        var sendingActivation = this.getFrom();
        var receivingActivation = this.getTo();

        if(sendingActivation == receivingActivation)
            return true;
        
        for(var message : receivingActivation.getMessages()) {
            if(message.getFrom() == receivingActivation && message.getTo() != receivingActivation && message.getOrder() >= this.getOrder())
                return false;
        }
        return true;
    }
}
