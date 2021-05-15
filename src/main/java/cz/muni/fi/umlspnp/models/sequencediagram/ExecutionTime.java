package cz.muni.fi.umlspnp.models.sequencediagram;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.models.ObservableString;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *  Represents the execution time of a execution (leaf) message.
 *
 */
public class ExecutionTime extends ObservableString{
    @Expose(serialize = true)
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
