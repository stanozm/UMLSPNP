/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.Connection;

/**
 *
 * @author 10ondr
 */
public class Message extends Connection<Lifeline> {
    
    public Message(Lifeline from, Lifeline to) {
        super(from, to);
    }
    
    public Lifeline getFrom() {
        return this.getFirst();
    }
    
    public Lifeline getTo() {
        return this.getSecond();
    }
}
