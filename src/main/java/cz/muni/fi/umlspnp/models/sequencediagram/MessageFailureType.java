package cz.muni.fi.umlspnp.models.sequencediagram;

import cz.muni.fi.umlspnp.models.ConnectionFailure;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 *
 */
public class MessageFailureType extends ConnectionFailure {
    private final BooleanProperty causeHWfail;

    public MessageFailureType(String name, double rate, boolean HWfail) {
        super(name, rate);
        
        causeHWfail = new SimpleBooleanProperty(HWfail);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        causeHWfail.addListener(stringChangeListener);
    }
    
    public BooleanProperty causeHWfailProperty() {
        return causeHWfail;
    }
    
    @Override
    public String toString() {
        if(causeHWfail.getValue())
            return super.toString() + " <node fail>";
        return super.toString();
    }
}
