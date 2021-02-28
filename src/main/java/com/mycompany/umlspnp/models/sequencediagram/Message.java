/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.Connection;
import com.mycompany.umlspnp.models.common.OperationEntry;
import javafx.beans.Observable;
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
public class Message extends Connection<Lifeline> {
    private final StringProperty name = new SimpleStringProperty();
    
    private final ObservableList<ExecutionTime> executionTime; // Exactly 1 item
    private OperationEntry operationType;
    private final FilteredList<OperationEntry> operationTypeList; // At most 1 item
    
    public Message(Lifeline from, Lifeline to) {
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
        
        operationTypeList = new FilteredList<>(from.getOperationEntries(),
                                               item -> item.equals(operationType));

        setExecutionTime(1);
    }
    
    public Lifeline getFrom() {
        return this.getFirst();
    }
    
    public Lifeline getTo() {
        return this.getSecond();
    }
    
    public StringProperty nameProperty(){
        return name;
    }
    
    public final void setExecutionTime(double newTime){
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
    
    public void removeExecutionTime(){
        executionTime.remove(0);
    }
    
    public ObservableList getExecutionTimeList(){
        return executionTime;
    }
    
    public ObservableList getOperationEntries(){
        return getFirst().getOperationEntries();
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
}
