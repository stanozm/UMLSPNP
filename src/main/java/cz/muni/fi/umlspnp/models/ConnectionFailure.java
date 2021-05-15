package cz.muni.fi.umlspnp.models;

import com.google.gson.annotations.Expose;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A connection failure type with its name and rate.
 * Used for a Communication Link failure in deployment diagram and Message failure
 * in sequence diagram.
 * 
 */
public class ConnectionFailure extends ObservableString {
    @Expose(serialize = true)
    private final StringProperty failureName = new SimpleStringProperty();
    @Expose(serialize = true)
    private final DoubleProperty rate = new SimpleDoubleProperty();
    
    public ConnectionFailure(String name, double rate){
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

    public final void setName(String name){
        this.failureName.setValue(name);
    }
    
    public StringProperty nameProperty(){
        return this.failureName;
    }
    
    public final void setRate(double rate){
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
