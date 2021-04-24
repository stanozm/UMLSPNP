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
    private ActionServiceSegment actionSegment;
    
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

    public void setActionSegment(ActionServiceSegment actionSegment) {
        this.actionSegment = actionSegment;
    }
    
    public ActionServiceSegment getActionSegment() {
        return actionSegment;
    }
    
    public boolean isExecutionServiceCall() {
        return actionSegment instanceof ServiceLeafSegment;
    }

    // TODO communication segment does not implement the interface yet
    public boolean isCommunicationServiceCall() {
        return actionSegment instanceof CommunicationSegment;
    }
}
