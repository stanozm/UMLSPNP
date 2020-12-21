/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.ObservableString;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class StateOperation extends ObservableString {
    private final ObjectProperty<State> state;
    private final ObservableMap<String, StateEffect> operations;
    
    public StateOperation(State state){
        this.state = new SimpleObjectProperty(state);
        this.operations = FXCollections.observableHashMap();

        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        var stringMapChangeListener = new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                updateStringRepresentation();
            }
        };
        
        this.state.addListener(stringChangeListener);
        this.operations.addListener(stringMapChangeListener);
        
        this.updateStringRepresentation();
    }
    
    public State getState(){
        return this.state.getValue();
    }

    @Override
    public String toString() {
        String res_str = String.format("[" + getState().toString() + "]");
        if(operations.size() < 1){
            res_str += " None";
        }
        else{
            for(String operation : operations.keySet()){
                res_str += ", ";
                res_str += operation;
                StateEffect effect = operations.get(operation);
                if(effect != null){
                    res_str += String.format(" <" + effect.getName().replace("%", "%%") + ">");
                }
            }
        }
        return res_str;
    }
    
    public void addOperation(String name, StateEffect effect){
        operations.put(name, effect);
        this.updateStringRepresentation();
    }
}
