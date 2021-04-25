/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.ObservableString;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class StateTransition extends ObservableString {
    private final ObjectProperty<State> from = new SimpleObjectProperty<>();
    private final ObjectProperty<State> to = new SimpleObjectProperty<>();
    private final StringProperty transitionName = new SimpleStringProperty();
    private final DoubleProperty rate = new SimpleDoubleProperty();
    
    private boolean isLocked = false; // Locked transition can not be edited or removed
    
    public StateTransition(State fromState, State toState, String name, double rate){
        this.setName(name);
        this.setRate(rate);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        var stateChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(oldValue != null) {
                    var oldState = (State) oldValue;
                    oldState.getStringRepresentation().removeListener(stringChangeListener);
                }
                if(newValue != null) {
                    var newState = (State) newValue;
                    newState.getStringRepresentation().addListener(stringChangeListener);
                }
            }
        };
        
        this.transitionName.addListener(stringChangeListener);
        this.rate.addListener(stringChangeListener);
        this.from.addListener(stringChangeListener);
        this.from.addListener(stateChangeListener);
        this.to.addListener(stringChangeListener);
        this.to.addListener(stateChangeListener);
        
        this.from.setValue(fromState);
        this.to.setValue(toState);
    }

    public void setLocked(boolean value) {
        this.isLocked = value;
    }
    
    public boolean isLocked() {
        return this.isLocked;
    }
    
    public State getStateFrom(){
        return this.from.getValue();
    }
    
    public State getStateTo(){
        return this.to.getValue();
    }
    
    public ObjectProperty fromStateProperty(){
        return this.from;
    }
    
    public ObjectProperty toStateProperty(){
        return this.to;
    }

    public void setName(String name){
        this.transitionName.setValue(name);
    }
    
    public StringProperty nameProperty(){
        return this.transitionName;
    }
    
    public void setRate(double rate){
        this.rate.setValue(rate);
    }
    
    public DoubleProperty rateProperty(){
        return this.rate;
    }

    @Override
    public String toString() {
        String fromStateString = "INVALID";
        String toStateString = "INVALID";
        if(getStateFrom() != null)
            fromStateString = getStateFrom().toString();
        if(getStateTo() != null)
            toStateString = getStateTo().toString();
        return String.format("[" + fromStateString + "->" + toStateString + "]: " + transitionName.getValue() + ", Rate: " + rate.getValue().toString());
    }
}
