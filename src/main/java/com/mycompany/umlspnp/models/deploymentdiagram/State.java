package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.ObservableString;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *  A Deployment Target node state.
 * State can be set as default, be locked to restrict editing and marked
 * as a DOWN state of the containing node.
 *
 */
public class State extends ObservableString{
    private final StringProperty stateName = new SimpleStringProperty();
    private final BooleanProperty isDefaultState = new SimpleBooleanProperty();
    
    private boolean isLocked = false; // Locked state can not be edited or removed
    private boolean isStateDOWN = false;
    
    public State(String name){
        this.setName(name);

        setDefault(false);
        
        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };
        
        stateName.addListener(stringChangeListener);
        isDefaultState.addListener(stringChangeListener);
    }

    public void setLocked(boolean value) {
        this.isLocked = value;
    }
    
    public boolean isLocked() {
        return this.isLocked;
    }

    public void setStateDOWN(boolean value) {
        this.isStateDOWN = value;
    }
    
    public boolean isStateDOWN() {
        return this.isStateDOWN;
    }
    
    public final void setName(String name){
        this.stateName.setValue(name);
    }
    
    public StringProperty nameProperty(){
        return this.stateName;
    }
    
    public final void setDefault(boolean value){
        this.isDefaultState.setValue(value);
    }
    
    public BooleanProperty isDefaultProperty(){
        return this.isDefaultState;
    }

    @Override
    public String toString() {
        if(this.isDefaultState.getValue())
            return String.format("*" + this.nameProperty().getValue());
        else
            return this.nameProperty().getValue();
    }
}
