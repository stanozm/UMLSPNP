/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.OperationEntry;
import com.mycompany.umlspnp.models.common.ObservableString;
import com.mycompany.umlspnp.models.common.OperationType;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;

/**
 *
 * @author 10ondr
 */
public class StateOperation extends ObservableString {
    private final ObjectProperty<State> state;
    private final ObservableList<OperationEntry> operationEntries;
    
    public StateOperation(State state){
        this.state = new SimpleObjectProperty(state);
        this.operationEntries = FXCollections.observableArrayList(
                new Callback<OperationEntry, Observable[]>() {
                    @Override
                    public Observable[] call(OperationEntry param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });

        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        var stringListChangeListener = new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                updateStringRepresentation();
            }

        };
        
        this.state.addListener(stringChangeListener);
        this.operationEntries.addListener(stringListChangeListener);
        
        this.updateStringRepresentation();
    }
    
    public State getState(){
        return this.state.getValue();
    }
    
    public ObjectProperty<State> stateProperty(){
        return this.state;
    }
    
    public ObservableList<OperationEntry> getOperationEntries(){
        return this.operationEntries;
    } 

    @Override
    public String toString() {
        String res_str = String.format("[" + getState().toString() + "]");
        if(operationEntries.size() < 1){
            res_str += " None";
        }
        else{
            for(OperationEntry operation : operationEntries){
                res_str += ", ";
                res_str += operation;
            }
        }
        return res_str;
    }
    
    public void addOperationEntry(OperationEntry newOperation){
        this.operationEntries.add(newOperation);
        this.updateStringRepresentation();
    }
    
    public void addOperationEntry(OperationType operationType, Integer processingSpeed){
        addOperationEntry(new OperationEntry(operationType, processingSpeed));
    }
}
