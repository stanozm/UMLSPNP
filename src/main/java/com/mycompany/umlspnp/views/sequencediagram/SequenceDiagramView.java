/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.sequencediagram;

import com.mycompany.umlspnp.common.ElementContainer;
import com.mycompany.umlspnp.views.DiagramView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Group;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 *
 * @author 10ondr
 */
public class SequenceDiagramView extends DiagramView{
    private final Group root;
    
    private static final ElementContainer<LifelineView, MessageView> allElements = new ElementContainer<>();
    
    private final ObservableMap<Number, LoopView> loopViews;
    
    public SequenceDiagramView(){
        this.root = new Group();
        
        loopViews = FXCollections.observableHashMap();
        
        diagramPane.getChildren().add(root);
    }
    
    public LifelineView createLifelineView(int modelObjectID){
        var newLifelineView = new LifelineView(0, 0, 10, 0, modelObjectID);
        allElements.addNode(newLifelineView, modelObjectID);
        
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
    
    public LoopView createLoop(int objectID){
        var loop = new LoopView(10, 10, 200, 50, "Loop", objectID);

        loop.addChangeListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                for(var message : allElements.getConnections().values()){
                    var shape = (Path) Shape.intersect(message.getArrow().getLine(), loop.getRectangle());
                    if(shape.getElements().size() > 0){
                        message.setInLoop(true);
                    }
                    else{
                        message.setInLoop(false);
                    }
                }
            }
        });
        
        loopViews.put(objectID, loop);
        root.getChildren().add(loop);
        return loop;
    }
    
    public boolean removeLoop(int objectID){
        var removedLoop = loopViews.get(objectID);
        if(removedLoop == null)
            return false;

        root.getChildren().remove(removedLoop);
        return loopViews.remove(objectID) != null;
    }
}
