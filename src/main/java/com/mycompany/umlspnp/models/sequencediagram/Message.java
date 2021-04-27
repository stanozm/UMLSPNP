package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.Connection;
import com.mycompany.umlspnp.models.ConnectionFailure;
import com.mycompany.umlspnp.models.OperationType;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A message as described in the sequence diagram specification.
 * All messages have a global order (representing their relative vertical position),
 * name and properties as execution time, message size, message failures and operation type.
 *
 */
public class Message extends Connection<Activation> {
    private final IntegerProperty order = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    
    private final ObservableList<ExecutionTime> executionTime; // Exactly 1 item
    private final ObservableList<MessageSize> messageSize; // Exactly 1 item
    private final ObservableList<ConnectionFailure> messageFailures;
    private final ObservableList<OperationType> operationTypeList; // At most 1 item
    
    
    public Message(Activation from, Activation to) {
        super(from, to);
        
        name.setValue("newMessage()");
        
        executionTime = FXCollections.observableArrayList((ExecutionTime param) -> new Observable[]{
            param.getStringRepresentation()
        });
        
        messageSize = FXCollections.observableArrayList((MessageSize param) -> new Observable[]{
            param.getStringRepresentation()
        });
        
        messageFailures = FXCollections.observableArrayList((ConnectionFailure param) -> new Observable[]{
            param.getStringRepresentation()
        });

        operationTypeList = FXCollections.observableArrayList((OperationType param) -> new Observable[]{
            param.getStringRepresentation()
        });

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
        if(messageSize.size() < 1)
            return null;
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

    public final void setOperationType(OperationType operationType){
        removeOperationType();
        operationTypeList.add(operationType);
    }
    
    public OperationType getOperationType() {
        if(operationTypeList.size() < 1)
            return null;
        return operationTypeList.get(0);
    }
    
    public void removeOperationType(){
        operationTypeList.clear();
    }
    
    public ObservableList<OperationType> getOperationTypeList(){
        return operationTypeList;
    }

    public final boolean isSelfMessage() {
        return this.getFrom() == this.getTo();
    }

    public final boolean isLeafMessage() {
        if(this.isSelfMessage())
            return true;

        var receivingActivation = this.getTo();
        for(var message : receivingActivation.getMessages()) {
            if((message.isSelfMessage() || message.getTo() != receivingActivation) && message.getOrder() >= this.getOrder())
                return false;
        }
        return true;
    }
}
