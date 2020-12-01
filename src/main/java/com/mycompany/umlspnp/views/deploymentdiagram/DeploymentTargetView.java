/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.common.ObjectInfo;
import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.views.common.Box;
import com.mycompany.umlspnp.views.common.ConnectionSlot;
import java.util.ArrayList;
import javafx.scene.control.MenuItem;

/**
 *
 * @author 10ondr
 */
public class DeploymentTargetView extends Box{
    private final ObjectInfo objectInfo;
    private final ArrayList<ConnectionSlot> slots;
    private final ArrayList<ArtifactView> artifacts;

    public DeploymentTargetView(double x, double y, double width, double height, double zOffset, int modelObjectID) {
        super(x, y, width, height, zOffset, "New deployment target");
        this.objectInfo = new ObjectInfo(modelObjectID);
        this.slots = new ArrayList<>();
        this.artifacts = new ArrayList<>();
    }

    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }

    public void addMenuItem(MenuItem newMenuItem){
        this.getContextMenu().getItems().add(newMenuItem);
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
    
    public void CreateArtifact(){
        var av = new ArtifactView(borderOffset.getValue(), borderOffset.getValue() + getZOffset().getValue(), 150, 150);
        
        if(av.getWidth() + borderOffset.getValue() * 2 > this.getWidth()){
            this.changeDimensions(av.getWidth() + borderOffset.getValue() * 2, this.getHeight());
        }
        if(av.getHeight() + borderOffset.getValue() * 2 > this.getHeight()){
            this.changeDimensions(this.getWidth(), av.getHeight() + borderOffset.getValue() * 2);
        }

        av.setParentBorderOffset(borderOffset);
        av.setMaxX(this.widthProperty());
        av.setMaxY(this.heightProperty());

        MenuItem menuItem1 = new MenuItem("Add edge");

        menuItem1.setOnAction((e) -> {
            av.changeDimensions(av.getWidth() + 10, av.getHeight() + 10);
        });

        av.getContextMenu().getItems().addAll(menuItem1);
        
        artifacts.add(av);
        this.getChildren().add(av);
    }
}
