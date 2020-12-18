/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.common;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author 10ondr
 */
public class ObservableString {
    private final StringProperty stringRepresentation = new SimpleStringProperty();
    
    public ObservableString(){
    
    }
    
    public StringExpression getStringRepresentation(){
        return stringRepresentation;
    }
    
    protected void updateStringRepresentation(){
        stringRepresentation.setValue(this.toString());
        stringRepresentation.getValue(); // Updates the value somehow
    }
}
