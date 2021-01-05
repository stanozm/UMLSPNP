/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.Annotation;
import com.mycompany.umlspnp.views.common.Box;
import com.mycompany.umlspnp.views.common.ConnectionSlot;
import com.mycompany.umlspnp.views.common.NamedRectangle;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.paint.Color;

/**
 *
 * @author 10ondr
 */
public class DeploymentTargetView extends Box{
    private final ArrayList<ConnectionSlot> slots = new ArrayList<>();
    private final HashMap<Number, NamedRectangle> innerNodes = new HashMap();
    
    private final Annotation statesAnnotation;
    private final Annotation stateTransitionsAnnotation;
    private final Annotation stateOperationsAnnotation;

    public DeploymentTargetView(double x, double y, double width, double height, double zOffset, Group diagramRoot, int modelObjectID) {
        super(x, y, width, height, zOffset, "New deployment target", modelObjectID);
        
        statesAnnotation = new Annotation(250, 10, this.getCenterX(), this.getCenterY(), "States");
        statesAnnotation.setFill(Color.LIGHTCYAN);
        
        stateTransitionsAnnotation = new Annotation(250, 125, this.getCenterX(), this.getCenterY(), "State Transitions");
        stateTransitionsAnnotation.setFill(Color.LIGHTPINK);
        
        stateOperationsAnnotation = new Annotation(250, 250, this.getCenterX(), this.getCenterY(), "Supported Operations");
        stateOperationsAnnotation.setFill(Color.OLDLACE);
        
        annotationInit(statesAnnotation, diagramRoot);
        annotationInit(stateTransitionsAnnotation, diagramRoot);
        annotationInit(stateOperationsAnnotation, diagramRoot);
    }

    public void addInnerNode(NamedRectangle child){
        child.setRestrictionsInParent(this);

        // Apply positioning restriction in parent
        child.setTranslateX(1);
        child.setTranslateY(1);

        innerNodes.put(child.getObjectInfo().getID(), child);
        this.getChildren().add(child);
    }

    public boolean removeInnerNode(int objectID){
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

    public Annotation getStatesAnnotation(){
        return statesAnnotation;
    }
    
    public Annotation getStateTransitionsAnnotation(){
        return stateTransitionsAnnotation;
    }
    
    public Annotation getStateOperationsAnnotation(){
        return stateOperationsAnnotation;
    }
    
    public ConnectionSlot getEmptySlot(){
        var cs = new ConnectionSlot(4.0, this.getZOffset(), this.widthProperty(), this.heightProperty());
        cs.deletedProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if((boolean) newValue){
                    slots.remove(cs);
                    getChildren().remove(cs);
                }
            }
        });
        slots.add(cs);
        this.getChildren().add(cs);
        return cs;
    }

    @Override
    public void changeDimensions(double newWidth, double newHeight){
        if(newWidth < getWidth()){
            for(var child : innerNodes.values()){
                if(newWidth < child.getTranslateX() + child.getWidth() + borderOffset.getValue()){
                    newWidth = getWidth();
                    break;
                }
            }
        }
        if(newHeight < getHeight()){
            for(var child : innerNodes.values()){        
                if(newHeight < child.getTranslateY() + child.getHeight() + borderOffset.getValue()){
                    newHeight = getHeight();
                    break;
                }
            }
        }
        
        super.changeDimensions(newWidth, newHeight);
        
        this.slots.forEach(slot -> {
            slot.refreshPosition();
        });
    }
    
    private void annotationInit(Annotation newAnnotation, Group diagramRoot){
        rect.boundsInLocalProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                var line = newAnnotation.getLine();

                line.setEndX(getWidth() / 2);
                line.setEndY(getHeight() / 2);
            }
        });

        getChildren().add(newAnnotation);
        getChildren().add(newAnnotation.getLine());
        newAnnotation.getLine().toBack();
        
        newAnnotation.setRestrictionsInParent(diagramRoot);
    }
}
