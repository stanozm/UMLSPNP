/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author 10ondr
 */
public class StateTransition {
    private final State from;
    private final State to;
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty rate = new SimpleDoubleProperty();
    
    public StateTransition(State from, State to, String name, double rate){
        this.from = from;
        this.to = to;
        this.setName(name);
        this.setRate(rate);
    }

    public State getStateFrom(){
        return this.from;
    }
    
    public State getStateTo(){
        return this.to;
    }

    public void setName(String name){
        this.name.setValue(name);
    }
    
    public StringProperty nameProperty(){
        return this.name;
    }
    
    public void setRate(double rate){
        this.rate.setValue(rate);
    }
    
    public DoubleProperty rateProperty(){
        return this.rate;
    }
    
    @Override
    public String toString() {
        return String.format("[" + from.toString() + "->" + to.toString() + "]: " + name.getValue() + ", Rate: " + rate.getValue().toString());
    }
}
