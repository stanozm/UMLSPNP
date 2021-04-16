/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.common;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author 10ondr
 */
public class OperationEntry extends ObservableString {
    private final ObjectProperty<OperationType> operationType;
    private final IntegerProperty speedLimit;
    
    public OperationEntry(OperationType operationType, Integer speedLimit){
        this.operationType = new SimpleObjectProperty(operationType);

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

        this.operationType.addListener(stringChangeListener);
        this.speedLimit.addListener(stringChangeListener);
    }
    
    public StringProperty nameProperty(){
        var op = operationType.getValue();
        if(op != null)
            return op.nameProperty();
        return null;
    }

    public IntegerProperty speedLimitProperty(){
        return this.speedLimit;
    }
    
    public String getName(){
        return nameProperty().getValue();
    }
    
    public ObjectProperty<OperationType> operationTypeProperty() {
        return operationType;
    }
    
    public OperationType getOperationType() {
        return operationType.getValue();
    }
    
    public void setOperationType(OperationType newOp) {
        operationType.setValue(newOp);
    }
    
    public Integer getSpeedLimit(){
        return speedLimitProperty().getValue();
    }

    @Override
    public String toString() {
        String opString = "None";
        if(operationType.getValue() != null)
            opString = operationType.getValue().getName();

        if (speedLimit.getValue() < 0)
            return opString;
        else
            return opString + "<processing speed " + speedLimit.getValue() + "%>";
    }
}
