/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.ObservableString;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class ExecutionTime extends ObservableString{
    private final DoubleProperty executionTime = new SimpleDoubleProperty();
    
    public ExecutionTime(double value){
        executionTime.set(value);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        executionTime.addListener(stringChangeListener);
    }

    public void setValue(double newValue){
        executionTime.set(newValue);
    }
    
    public DoubleProperty executionTimeProperty(){
        return executionTime;
    }
    
    @Override
    public String toString() {
        return executionTime.getValue().toString();
    }
}
