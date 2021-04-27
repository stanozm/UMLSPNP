package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.Annotation;
import com.mycompany.umlspnp.views.common.AnnotationOwner;
import com.mycompany.umlspnp.views.common.Box;
import com.mycompany.umlspnp.views.common.NamedRectangle;
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.paint.Color;

/**
 * View rendering the deployment target in the deployment diagram.
 *
 */
public class DeploymentTargetView extends Box implements AnnotationOwner {
    private final HashMap<Number, NamedRectangle> innerNodes = new HashMap();
    
    private final Annotation statesAnnotation;
    private final Annotation stateTransitionsAnnotation;
    private final Annotation stateOperationsAnnotation;
    
    private boolean annotationsDisplayed = true;

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
    
    @Override
    public void setAnnotationsDisplayed(boolean value){
        if(annotationsDisplayed != value){
            annotationsDisplayed = value;

            statesAnnotation.setDisplayed(value);
            stateTransitionsAnnotation.setDisplayed(value);
            stateOperationsAnnotation.setDisplayed(value);
        }
    }
    
    @Override
    public boolean areAnnotationsDisplayed(){
        return annotationsDisplayed;
    }
}
