package com.mycompany.umlspnp.views.common;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 *  A container which holds the information about two elements which are being
 * selected for connection.
 *
 */
public class ConnectionContainer {
    private BasicElement first;
    private BasicElement second;
    
    private final ObjectProperty<Pair<BasicElement,BasicElement>> connectionProperty = new SimpleObjectProperty();
    
    public ConnectionContainer(){
        
    }
    
    public void setFirstElement(BasicElement element){
        this.first = element;
        connectionProperty.setValue(new Pair(first, second));
    }
    
    public BasicElement getFirstElement(){
        return first;
    }
    
    public void setSecondElement(BasicElement element){
        this.second = element;
        connectionProperty.setValue(new Pair(first, second));
    }
    
    public BasicElement getSecondElement(){
        return second;
    }
    
    public Class getType(){
        return first.getClass();
    }
    
    public ObjectProperty connectionProperty(){
        return connectionProperty;
    }

    public Number getFirstElementID(){
        if(first instanceof BasicElement)
            return ((BasicElement) first).getObjectInfo().getID();
        return null;
    }
    
    public Number getSecondElementID(){
        if(second instanceof BasicElement)
            return ((BasicElement) second).getObjectInfo().getID();
        return null;
    }
    
    public void clear(){
        first = null;
        second = null;
        connectionProperty.setValue(new Pair(null, null));
    }
}
