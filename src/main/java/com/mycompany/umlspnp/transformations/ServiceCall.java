/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.spnp.core.models.places.StandardPlace;

/**
 *
 * @author 10ondr
 */
public class ServiceCall {
    private final Message message;
    private final StandardPlace place;
    
    public ServiceCall(Message message, StandardPlace place) {
        this.message = message;
        this.place = place;
    }
    public Message getMessage() {
        return message;
    }
    
    public StandardPlace getPlace() {
        return place;
    }    
}
