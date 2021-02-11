/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 *
 * @author 10ondr
 */
public class ConnectionContainer {
    private BasicElement first;
    private BasicElement second;
    
    private final ObjectProperty<Pair<BasicElement,BasicElement>> connectionProperty = new SimpleObjectProperty();
    
    public ConnectionContainer(){
        
    }
    
    public void setFirstElement(BasicElement element){
        this.first = element;
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
        if(first instanceof NamedRectangle)
            return ((NamedRectangle) first).getObjectInfo().getID();
        return null;
    }
    
    public Number getSecondElementID(){
        if(second instanceof NamedRectangle)
            return ((NamedRectangle) second).getObjectInfo().getID();
        return null;
    }
    
    public void clear(){
        first = null;
        second = null;
        connectionProperty.setValue(new Pair(null, null));
    }
}
