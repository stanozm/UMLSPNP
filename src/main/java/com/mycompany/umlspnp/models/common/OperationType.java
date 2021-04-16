/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.common;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class OperationType extends ObservableString {
    private final StringProperty name;
    
    public OperationType(String name){
        this.name = new SimpleStringProperty(name);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };

        this.name.addListener(stringChangeListener);
    }

    public StringProperty nameProperty(){
        return this.name;
    }

    public String getName(){
        return nameProperty().getValue();
    }

    @Override
    public String toString() {
        return this.name.getValue();
    }
}
