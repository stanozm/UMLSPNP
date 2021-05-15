package cz.muni.fi.umlspnp.models.deploymentdiagram;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.models.ObservableString;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *  A Communication Link type which contains a name and probability rate.
 *
 */
public class LinkType extends ObservableString {
    @Expose(serialize = true)
    private final StringProperty name = new SimpleStringProperty();
    @Expose(serialize = true)
    private final DoubleProperty rate = new SimpleDoubleProperty();
    
    public LinkType(String name, double rate){
        this.setName(name);
        this.setRate(rate);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        this.name.addListener(stringChangeListener);
        this.rate.addListener(stringChangeListener);
    }

    public final String getName() {
        return this.name.get();
    }
    
    public final void setName(String name){
        this.name.setValue(name);
    }
        
    public StringProperty nameProperty(){
        return this.name;
    }
    
    public final double getRate() {
        return this.rate.get();
    }
    
    public final void setRate(double rate){
        this.rate.setValue(rate);
    }
    
    public DoubleProperty rateProperty(){
        return this.rate;
    }
    
    @Override
    public String toString() {
        return String.format("Link type: " + name.getValue() + " (Transfer rate: " + rate.getValue().toString() + ")");
    }
}
