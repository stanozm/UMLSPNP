/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class StateOperation {
    private final State state;
    private final ObservableMap<String, StateEffect> operations;

    public StateOperation(State state){
        this.state = state;
        this.operations = FXCollections.observableHashMap();
    }

    public State getState(){
        return this.state;
    }

    @Override
    public String toString() {
        String res_str = String.format("[" + state.toString() + "]");
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
    }
}
