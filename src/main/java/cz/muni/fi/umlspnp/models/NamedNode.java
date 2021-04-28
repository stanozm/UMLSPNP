package cz.muni.fi.umlspnp.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *  Represents some named entity in the Deployment and Sequence diagram.
 *
 */
public abstract class NamedNode extends BasicNode{
    private final StringProperty name = new SimpleStringProperty();
    
    public NamedNode(String name){
        super();
        
        this.name.setValue(name);
    }

    public StringProperty getNameProperty(){
        return name;
    }
}
