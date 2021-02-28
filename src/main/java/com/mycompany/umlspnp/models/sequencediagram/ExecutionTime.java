/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.ObservableString;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class ExecutionTime extends ObservableString{
    private final IntegerProperty executionTime = new SimpleIntegerProperty();
    
    public ExecutionTime(int value){
        executionTime.set(value);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        executionTime.addListener(stringChangeListener);
    }

    public void setValue(int newValue){
        executionTime.set(newValue);
    }
    
    public IntegerProperty executionTimeProperty(){
        return executionTime;
    }
    
    @Override
    public String toString() {
        return executionTime.getValue().toString();
    }
}
