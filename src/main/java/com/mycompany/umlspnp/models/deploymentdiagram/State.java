/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author 10ondr
 */
public class State {
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty isDefaultState = new SimpleBooleanProperty();
    
    public State(String name){
        this.setName(name);
    }
    
    public void setName(String name){
        this.name.setValue(name);
    }
    
    public StringProperty nameProperty(){
        return this.name;
    }
    
    public void setDefault(boolean value){
        this.isDefaultState.setValue(value);
    }
    
    public BooleanProperty isDefaultProperty(){
        return this.isDefaultState;
    }

    @Override
    public String toString() {
        if(this.isDefaultState.getValue())
            return String.format("*" + this.name.getValue());
        else
            return this.name.getValue();
    }
}
