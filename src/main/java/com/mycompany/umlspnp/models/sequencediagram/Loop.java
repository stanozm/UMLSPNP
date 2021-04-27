package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.BasicNode;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.IntegerProperty;
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

    private final Set<Message> messages = new HashSet<>();

    public Loop() {
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                name.setValue(String.format("Loop " + iterations.getValue().toString() + "x"));
            }
        };
        
        iterations.addListener(stringChangeListener);
        
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
