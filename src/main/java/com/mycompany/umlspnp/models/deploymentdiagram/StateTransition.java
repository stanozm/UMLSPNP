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
    private final ObjectProperty<State> from;
    private final ObjectProperty<State> to;
    private final StringProperty transitionName = new SimpleStringProperty();
    private final DoubleProperty rate = new SimpleDoubleProperty();
    
    public StateTransition(State from, State to, String name, double rate){
        this.from = new SimpleObjectProperty(from);
        this.to = new SimpleObjectProperty(to);
        this.setName(name);
        this.setRate(rate);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        this.transitionName.addListener(stringChangeListener);
        this.rate.addListener(stringChangeListener);
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
    
    public void setStateFrom(State newState){
        this.from.setValue(newState);
        this.updateStringRepresentation();
    }
    
    public void setStateTo(State newState){
        this.to.setValue(newState);
        this.updateStringRepresentation();
    }
    
    @Override
    public String toString() {
        return String.format("[" + getStateFrom().toString() + "->" + getStateTo().toString() + "]: " + transitionName.getValue() + ", Rate: " + rate.getValue().toString());
    }
}
