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
    
    private static final ElementContainer allElements = new ElementContainer<LifelineView, MessageView>();
    
    public SequenceDiagramView(){
        this.root = new Group();
        
        diagramPane.getChildren().add(root);
    }
    
    public LifelineView createLifelineView(int modelObjectID){
        var newLifelineView = new LifelineView(modelObjectID, 0, 10, 0, 0);
        
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
        boolean success = allElements.removeNode(modelObjectID);
        if(!success)
            return false;
        
        var removedLifline = getLifelineView(modelObjectID);
        return root.getChildren().remove(removedLifline);
    }
}
