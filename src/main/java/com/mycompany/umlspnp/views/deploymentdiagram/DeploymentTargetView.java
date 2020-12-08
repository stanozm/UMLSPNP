/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.common.ObjectInfo;
import com.mycompany.umlspnp.models.common.NamedNode;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import com.mycompany.umlspnp.views.common.Box;
import com.mycompany.umlspnp.views.common.ConnectionSlot;
import com.mycompany.umlspnp.views.common.NamedRectangle;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author 10ondr
 */
public class DeploymentTargetView extends Box{
    private final ObjectInfo objectInfo;
    private final ArrayList<ConnectionSlot> slots = new ArrayList<>();
    private final HashMap<Number, NamedRectangle> innerNodes = new HashMap();

    public DeploymentTargetView(double x, double y, double width, double height, double zOffset, int modelObjectID) {
        super(x, y, width, height, zOffset, "New deployment target", modelObjectID);
        this.objectInfo = new ObjectInfo(modelObjectID);
    }

    public ConnectionSlot getEmptySlot(){
        var cs = new ConnectionSlot(4.0, this.getZOffset(), this.translateXProperty(), this.translateYProperty(), this.widthProperty(), this.heightProperty());
        slots.add(cs);
        this.getChildren().add(cs);
        return cs;
    }

    @Override
    public void changeDimensions(double newWidth, double newHeight){
        super.changeDimensions(newWidth, newHeight);
        
        this.slots.forEach(slot -> {
            slot.refreshPosition();
        });
    }

    private void addInnerNode(NamedRectangle child){
        if(child.getWidth() + borderOffset.getValue() * 2 > this.getWidth()){
            this.changeDimensions(child.getWidth() + borderOffset.getValue() * 2, this.getHeight());
        }
        if(child.getHeight() + borderOffset.getValue() * 2 > this.getHeight()){
            this.changeDimensions(this.getWidth(), child.getHeight() + borderOffset.getValue() * 2);
        }

        child.setParentBorderOffset(borderOffset);
        child.setMaxX(this.widthProperty());
        child.setMaxY(this.heightProperty());
        
        innerNodes.put(child.getObjectInfo().getID(), child);
        this.getChildren().add(child);
    }
    
    public ArtifactView CreateArtifact(int modelObjectID){
        var newArtifact = new ArtifactView(borderOffset.getValue(), borderOffset.getValue() + getZOffset().getValue(), 150, 150, modelObjectID);
        addInnerNode(newArtifact);
        return newArtifact;
    }
    
    public DeploymentTargetView CreateDeploymentTarget(int modelObjectID){
        var newDeploymentTarget = new DeploymentTargetView(0, 10, 150, 150, 10, modelObjectID);
        
        addInnerNode(newDeploymentTarget);

        return newDeploymentTarget;
    }
    
    public boolean deleteInnerNode(int objectID){
        var innerNode = innerNodes.get(objectID);

        if(innerNode != null){
            boolean result = innerNodes.remove(objectID) != null;
            if(result){
                this.getChildren().remove(innerNode);
            }
            return result;
        }
        return false;
    }
    
    public NamedRectangle getInnerNode(int objectID){
        return innerNodes.get(objectID);
    }

    public NamedRectangle getInnerNodeRecursive(int objectID){
        var node = getInnerNode(objectID);
        if(node != null)
            return node;
        for(var item : innerNodes.values()){
            if(item instanceof DeploymentTargetView){
                var innerNode = ((DeploymentTargetView) item).getInnerNodeRecursive(objectID);
                if(innerNode != null)
                    return innerNode;
            }
        }
        return null;
    }
}
