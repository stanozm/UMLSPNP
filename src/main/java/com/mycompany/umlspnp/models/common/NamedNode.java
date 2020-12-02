/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.common;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author 10ondr
 */
public class NamedNode extends BasicNode{
    private final StringProperty name = new SimpleStringProperty();
    
    public NamedNode(String name){
        super();
        
        this.name.setValue(name);
    }

    public StringProperty getNameProperty(){
        return name;
    }
}
