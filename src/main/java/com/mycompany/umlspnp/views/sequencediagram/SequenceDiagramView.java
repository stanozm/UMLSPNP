/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.sequencediagram;

import com.mycompany.umlspnp.common.ElementContainer;
import com.mycompany.umlspnp.views.DiagramView;
import javafx.scene.Group;

/**
 *
 * @author 10ondr
 */
public class SequenceDiagramView extends DiagramView{
    private final Group root;
    
    private static final ElementContainer<LifelineView, MessageView> allElements = new ElementContainer<>();
    
    public SequenceDiagramView(){
        this.root = new Group();
        
        diagramPane.getChildren().add(root);
    }
    
    public LifelineView createLifelineView(int modelObjectID){
        var newLifelineView = new LifelineView(0, 0, 10, 0, modelObjectID);
        allElements.addNode(newLifelineView, modelObjectID);
        //registerNodeToSelect(newLifelineView);
        
        newLifelineView.setRestrictionsInParent(root);
        root.getChildren().add(newLifelineView);
        
        newLifelineView.changeDimensions(150, 40);
        return newLifelineView;
    }
    
    public LifelineView getLifelineView(int objectID){
        return (LifelineView) allElements.getNode(objectID);
    }
    
    public boolean removeLifelineView(int modelObjectID){
        var removedLifline = getLifelineView(modelObjectID);
        boolean success = allElements.removeNode(modelObjectID);
        if(!success)
            return false;

        return root.getChildren().remove(removedLifline);
    }
    
    public MessageView createMessage(LifelineView source, LifelineView destination, int messageModelID){
        var newMessageView = new MessageView(messageModelID, source.getSpanBox().getEmptySlot(), destination.getSpanBox().getEmptySlot(), root);

        allElements.addConnection(newMessageView, messageModelID);
        root.getChildren().add(newMessageView);
        newMessageView.refreshLinePosition();
        return newMessageView;
    }
    
    public MessageView createMessage(int sourceID, int destinationID, int connectionModelID){
        var source = getLifelineView(sourceID);
        var destination = getLifelineView(destinationID);
        return createMessage(source, destination, connectionModelID);
    }
    
    public boolean removeMessage(int messageModelID){
        var message = getConnection(messageModelID);

        if(message == null)
            return false;

        message.removeSlots();
        allElements.removeConnection(messageModelID);
        root.getChildren().remove(message);
        return true;
    }
    
    public MessageView getConnection(int objectID){
        return allElements.getConnection(objectID);
    }
    
}
