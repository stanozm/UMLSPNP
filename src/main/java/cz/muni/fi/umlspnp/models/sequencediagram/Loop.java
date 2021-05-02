package cz.muni.fi.umlspnp.models.sequencediagram;

import cz.muni.fi.umlspnp.models.BasicNode;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *  A loop which can encapsulate messages and has a maximal number of iterations.
 *
 */
public class Loop extends BasicNode {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty iterations = new SimpleIntegerProperty();
    private final DoubleProperty restartRate = new SimpleDoubleProperty();

    private final Set<Message> messages = new HashSet<>();

    public Loop() {
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                var rate = getRestartRate();
                var rateString = rate > 0.0 ? rate.toString() : "immediate";
                name.setValue(String.format("Loop " + iterations.getValue().toString() + "x [restart %s]", rateString));
            }
        };
        
        iterations.addListener(stringChangeListener);
        restartRate.addListener(stringChangeListener);
        
        setIterations(2);
    }
    
    public StringProperty nameProperty(){
        return name;
    }
    
    public IntegerProperty iterationsProperty(){
        return iterations;
    }

    public Integer getIterations(){
        return iterations.getValue();
    }
    
    public final void setIterations(int newValue){
        if(newValue >= 2){
            iterations.setValue(newValue);
        }
        else {
            System.err.println("Error: iterations has to be greater or equal to 2");
        }
    }
    
    public DoubleProperty restartRateProperty() {
        return restartRate;
    }
    
    public Double getRestartRate() {
        return restartRate.getValue();
    }
    
    public void setRestartRate(Double rate) {
        restartRate.setValue(rate);
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean removeMessage(Message message) {
        return messages.remove(message);
    }
}
