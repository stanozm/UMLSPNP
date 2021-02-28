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
public class MessageSize extends ObservableString {
    private final IntegerProperty messageSize = new SimpleIntegerProperty();
    
    public MessageSize(int value){
        messageSize.set(value);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        messageSize.addListener(stringChangeListener);
    }

    public void setValue(int newValue){
        messageSize.set(newValue);
    }
    
    public IntegerProperty messageSizeProperty(){
        return messageSize;
    }
    
    @Override
    public String toString() {
        return messageSize.getValue().toString();
    }
    
}
