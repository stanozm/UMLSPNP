package com.mycompany.umlspnp.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *  A globally identified operation type for Messages in sequence diagram.
 *
 */
public class OperationType extends ObservableString {
    private final StringProperty name;
    
    public OperationType(String name){
        this.name = new SimpleStringProperty(name);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };

        this.name.addListener(stringChangeListener);
    }

    public StringProperty nameProperty(){
        return this.name;
    }

    public String getName(){
        return nameProperty().getValue();
    }

    @Override
    public String toString() {
        return this.name.getValue();
    }
}
