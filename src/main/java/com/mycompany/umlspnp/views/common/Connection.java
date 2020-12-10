/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import javafx.scene.shape.Line;

/**
 *
 * @author 10ondr
 */
public class Connection extends BasicElement {
    private final ConnectionSlot source;
    private final ConnectionSlot destination;
    private final Line line;
    
    public Connection(int modelObjectID, ConnectionSlot source, ConnectionSlot destination){
        super(modelObjectID);
        
        this.source = source;
        this.destination = destination;

        this.line = new Line();
        
        line.startXProperty().bind(this.source.getBindingPosX());
        line.startYProperty().bind(this.source.getBindingPosY());
        
        line.endXProperty().bind(this.destination.getBindingPosX());
        line.endYProperty().bind(this.destination.getBindingPosY());

        this.getChildren().add(line);
    }

    public void removeSlots(){
        this.source.setDeleted(true);
        this.destination.setDeleted(true);
    }
}
