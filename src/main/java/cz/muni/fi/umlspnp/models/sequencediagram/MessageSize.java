package cz.muni.fi.umlspnp.models.sequencediagram;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.models.ObservableString;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Represents the size of a execution (leaf) message.
 *
 */
public class MessageSize extends ObservableString {
    // TODO double?
    @Expose(serialize = true)
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
