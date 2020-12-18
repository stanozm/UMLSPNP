/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.ObservableString;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class State extends ObservableString{
    private final StringProperty stateName = new SimpleStringProperty();
    private final BooleanProperty isDefaultState = new SimpleBooleanProperty();
    
    public State(String name){
        this.setName(name);

        setDefault(false);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        stateName.addListener(stringChangeListener);
        isDefaultState.addListener(stringChangeListener);
    }

    public void setName(String name){
        this.stateName.setValue(name);
    }
    
    public StringProperty nameProperty(){
        return this.stateName;
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
            return String.format("*" + this.nameProperty().getValue());
        else
            return this.nameProperty().getValue();
    }
}
