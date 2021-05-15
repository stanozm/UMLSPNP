package cz.muni.fi.umlspnp.models;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Ties operation type with optional speed limit.
 * 
 */
public class OperationEntry extends ObservableString {
    @Expose(serialize = true)
    private final ObjectProperty<OperationType> operationType = new SimpleObjectProperty<>();
    @Expose(serialize = true)
    private final IntegerProperty speedLimit;
    
    public OperationEntry(OperationType operationType, Integer speedLimit){
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

        var operationTypeChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(oldValue != null) {
                    var oldOperationType = (OperationType) oldValue;
                    oldOperationType.stringRepresentationProperty().removeListener(stringChangeListener);
                }
                if(newValue != null) {
                    var newOperationType = (OperationType) newValue;
                    newOperationType.stringRepresentationProperty().addListener(stringChangeListener);
                }
            }
        };

        this.operationType.addListener(stringChangeListener);
        this.operationType.addListener(operationTypeChangeListener);
        this.speedLimit.addListener(stringChangeListener);
        
        this.operationType.setValue(operationType);
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
        if(getOperationType() != null)
            opString = getOperationType().getName();

        if (speedLimit.getValue() < 0)
            return opString;
        else
            return opString + "<processing speed " + speedLimit.getValue() + "%>";
    }
}
