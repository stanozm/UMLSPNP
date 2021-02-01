/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.ObservableString;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class LinkFailure extends ObservableString {
    private final StringProperty failureName = new SimpleStringProperty();
    private final DoubleProperty rate = new SimpleDoubleProperty();
    
    public LinkFailure(String name, double rate){
        this.setName(name);
        this.setRate(rate);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        this.failureName.addListener(stringChangeListener);
        this.rate.addListener(stringChangeListener);
    }

    public void setName(String name){
        this.failureName.setValue(name);
    }
    
    public StringProperty nameProperty(){
        return this.failureName;
    }
    
    public void setRate(double rate){
        this.rate.setValue(rate);
    }
    
    public DoubleProperty rateProperty(){
        return this.rate;
    }
    
    @Override
    public String toString() {
        return String.format("[" + failureName.getValue() + "]:, Rate: " + rate.getValue().toString());
    }
}
