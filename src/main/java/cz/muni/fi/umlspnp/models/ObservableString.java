package cz.muni.fi.umlspnp.models;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *  Provides more general string observation with arbitrary structure.
 *
 */
public abstract class ObservableString {
    protected final StringProperty stringRepresentation = new SimpleStringProperty();
    
    public ObservableString(){
    
    }
    
    public StringExpression stringRepresentationProperty(){
        return stringRepresentation;
    }
    
    protected final void updateStringRepresentation(){
        stringRepresentation.setValue(this.toString());
        stringRepresentation.getValue();
    }
}
