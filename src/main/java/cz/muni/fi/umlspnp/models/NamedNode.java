package cz.muni.fi.umlspnp.models;

import com.google.gson.annotations.Expose;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *  Represents some named entity in the Deployment and Sequence diagram.
 *
 */
public abstract class NamedNode extends BasicNode{
    @Expose(serialize = true)
    private final StringProperty name = new SimpleStringProperty();
    
    public NamedNode(String name){
        super();
        
        this.name.setValue(name);
    }

    public StringProperty getNameProperty(){
        return name;
    }
}
