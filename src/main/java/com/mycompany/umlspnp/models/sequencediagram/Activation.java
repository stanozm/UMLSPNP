/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.BasicNode;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class Activation extends BasicNode {
    private final Lifeline lifeline; // TODO better solution
    private final ObservableMap<Number, Message> messages;
    
    public Activation(Lifeline lifeline) {
        this.lifeline = lifeline;
        this.messages = FXCollections.observableHashMap();
    }

    public Lifeline getLifeline() {
        return lifeline;
    }
    
    public void addMessage(Message newMessage){
        messages.put(newMessage.getObjectInfo().getID(), newMessage);
    }

    public void removeMessage(Message removedMessage){
        messages.remove(removedMessage.getObjectInfo().getID());
    }
    
    public Collection<Message> getMessages(){
        return messages.values();
    }
    
    public void addMessagesChangeListener(MapChangeListener listener){
        messages.addListener(listener);
    }
}
