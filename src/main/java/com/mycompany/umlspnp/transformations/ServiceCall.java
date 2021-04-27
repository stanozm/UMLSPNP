package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.spnp.core.models.places.StandardPlace;

/**
 *  A container which ties a message from the model to its generated
 * Petri net representation in the control service segment.
 *
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

    public boolean isCommunicationServiceCall() {
        return actionSegment instanceof CommunicationSegment;
    }
}
