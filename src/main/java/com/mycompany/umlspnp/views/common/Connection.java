/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import java.util.function.BiFunction;
import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

/**
 *
 * @author 10ondr
 */
public class Connection extends Group {
    private final ConnectionSlot source;
    private final ConnectionSlot destination;
    private final Line line;
    
    public Connection(ConnectionSlot source, ConnectionSlot destination){
        this.source = source;
        this.destination = destination;

        this.line = new Line();
        
        line.startXProperty().bind(this.source.getBindingPosX());
        line.startYProperty().bind(this.source.getBindingPosY());
        
        line.endXProperty().bind(this.destination.getBindingPosX());
        line.endYProperty().bind(this.destination.getBindingPosY());

        this.getChildren().add(line);
    }

    /*
    public void setConnectionSource(ConnectionSlot newSource){
        this.source = newSource;
    }
    
    public void setConnectionDestination(ConnectionSlot newDest){
        this.destination = newDest;
    }
    */

}
