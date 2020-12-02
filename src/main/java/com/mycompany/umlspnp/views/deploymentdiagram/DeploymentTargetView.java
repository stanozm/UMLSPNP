/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.common.ObjectInfo;
import com.mycompany.umlspnp.views.common.Box;
import com.mycompany.umlspnp.views.common.ConnectionSlot;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.control.MenuItem;

/**
 *
 * @author 10ondr
 */
public class DeploymentTargetView extends Box{
    private final ObjectInfo objectInfo;
    private final ArrayList<ConnectionSlot> slots;
    private final HashMap<Number, ArtifactView> artifacts;

    public DeploymentTargetView(double x, double y, double width, double height, double zOffset, int modelObjectID) {
        super(x, y, width, height, zOffset, "New deployment target", modelObjectID);
        this.objectInfo = new ObjectInfo(modelObjectID);
        this.slots = new ArrayList<>();
        this.artifacts = new HashMap();
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
    
    public ArtifactView CreateArtifact(int modelObjectID){
        var av = new ArtifactView(borderOffset.getValue(), borderOffset.getValue() + getZOffset().getValue(), 150, 150, modelObjectID);
        
        if(av.getWidth() + borderOffset.getValue() * 2 > this.getWidth()){
            this.changeDimensions(av.getWidth() + borderOffset.getValue() * 2, this.getHeight());
        }
        if(av.getHeight() + borderOffset.getValue() * 2 > this.getHeight()){
            this.changeDimensions(this.getWidth(), av.getHeight() + borderOffset.getValue() * 2);
        }

        av.setParentBorderOffset(borderOffset);
        av.setMaxX(this.widthProperty());
        av.setMaxY(this.heightProperty());

        artifacts.put(modelObjectID, av);
        this.getChildren().add(av);
        
        return av;
    }
    
    public boolean deleteArtifact(int objectID){
        ArtifactView AV = artifacts.get(objectID);

        if(AV != null){
            boolean result = artifacts.remove(objectID) != null;
            if(result){
                this.getChildren().remove(AV);
            }
            return result;
        }
        return false;
    }
}
