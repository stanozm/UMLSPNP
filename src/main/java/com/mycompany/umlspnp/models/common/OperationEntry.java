/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.common;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class OperationEntry extends ObservableString {
    private final StringProperty name;
    private final IntegerProperty speedLimit;
    
    public OperationEntry(String name, Integer speedLimit){
        this.name = new SimpleStringProperty(name);
        this.speedLimit = new SimpleIntegerProperty();
        if(speedLimit == null)
            this.speedLimit.setValue(-1);
        else
            this.speedLimit.setValue(speedLimit);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        this.name.addListener(stringChangeListener);
        this.speedLimit.addListener(stringChangeListener);
    }
    
    public StringProperty nameProperty(){
        return this.name;
    }
    
    public IntegerProperty speedLimitProperty(){
        return this.speedLimit;
    }
    
    public String getName(){
        return nameProperty().getValue();
    }
    
    public Integer getSpeedLimit(){
        return speedLimitProperty().getValue();
    }
    
    @Override
    public String toString() {
        if (this.speedLimit.getValue() < 0)
            return this.name.getValue();
        else
            return this.name.getValue() + "<processing speed " + this.speedLimit.getValue() + "%>";
    }
}
