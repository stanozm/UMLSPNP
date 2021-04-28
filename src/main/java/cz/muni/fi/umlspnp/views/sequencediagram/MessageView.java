package cz.muni.fi.umlspnp.views.sequencediagram;

import cz.muni.fi.umlspnp.views.common.Annotation;
import cz.muni.fi.umlspnp.views.common.AnnotationOwner;
import cz.muni.fi.umlspnp.views.common.ConnectionSlot;
import cz.muni.fi.umlspnp.views.common.ConnectionView;
import cz.muni.fi.umlspnp.views.common.layouts.EditableListView;
import cz.muni.fi.umlspnp.views.common.layouts.PropertiesModalWindow;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *  View which renders the message in a sequence diagram.
 * It is a type of connection with an arrow on the destination (target) end.
 *
 */
public class MessageView extends ConnectionView implements AnnotationOwner{
    private final Label messageLabel;
    
    private final Annotation executionTimeAnnotation;
    private final Annotation messageSizeAnnotation;
    private final Annotation operationTypeAnnotation;
    private final Annotation failureTypesAnnotation;
    
    private boolean annotationsDisplayed = true;
    
    private final boolean sourceIsDestination;
    
    private final ObjectProperty<LoopView> loopProperty = new SimpleObjectProperty<>(null);
    
    public MessageView(int modelObjectID, ConnectionSlot source, ConnectionSlot destination, boolean sourceIsDestination, Group diagramRoot) {
        super(modelObjectID, source, destination, diagramRoot, true, sourceIsDestination);
        
        messageInit();
        
        this.sourceIsDestination = sourceIsDestination;
        
        messageLabel = new Label();
        
        if(sourceIsDestination)
            messageLabel.translateXProperty().bind(arrow.getCenterX());
        else
            messageLabel.translateXProperty().bind(arrow.getCenterX().subtract(messageLabel.widthProperty().divide(2)));
        messageLabel.translateYProperty().bind(arrow.getCenterY().subtract(25));
        
        this.getChildren().add(messageLabel);
        
        executionTimeAnnotation = new Annotation(0, 50, this.arrow.getCenterX(), this.arrow.getCenterY(), "Execution Time");
        executionTimeAnnotation.setFill(Color.LIGHTYELLOW);
        
        operationTypeAnnotation = new Annotation(100, 50, this.arrow.getCenterX(), this.arrow.getCenterY(), "Operation Type");
        operationTypeAnnotation.setFill(Color.ORANGE);
        
        failureTypesAnnotation = new Annotation(0, 100, this.arrow.getCenterX(), this.arrow.getCenterY(), "Failure Types");
        failureTypesAnnotation.setFill(Color.PINK);
        
        messageSizeAnnotation = new Annotation(100, 100, this.arrow.getCenterX(), this.arrow.getCenterY(), "Message Size");
        messageSizeAnnotation.setFill(Color.LIGHTBLUE);
        
        annotationInit(executionTimeAnnotation);
        annotationInit(operationTypeAnnotation);
        annotationInit(failureTypesAnnotation);
        annotationInit(messageSizeAnnotation);
    }
    
    private void messageInit() {
        this.source.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                processMessageMoved();
            }
        });
        
        this.destination.localToSceneTransformProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                processMessageMoved();
            }
        });
    }
    
    public void processMessageMoved(){
        double startX = arrow.getLine().getStartX();
        double endX = arrow.getLine().getEndX();
        
        // Changes would be cyclic without an offset that is equal or greater to the width of both lifeline rectangles
        double offset = source.getParentWidth() + destination.getParentWidth() + 5;
        
        if(sourceIsDestination){
            source.setMovementLimit(ConnectionSlot.LimitMovement.onlyRight);
            destination.setMovementLimit(ConnectionSlot.LimitMovement.onlyRight);
        }
        else if(startX < endX - offset){
            source.setMovementLimit(ConnectionSlot.LimitMovement.onlyRight);
            destination.setMovementLimit(ConnectionSlot.LimitMovement.onlyLeft);
        }
        else if(startX > endX + offset){
            source.setMovementLimit(ConnectionSlot.LimitMovement.onlyLeft);
            destination.setMovementLimit(ConnectionSlot.LimitMovement.onlyRight);
        }
    }
    
    public StringProperty nameProperty(){
        return messageLabel.textProperty();
    }
    
    public void setName(String newName){
        messageLabel.setText(newName);
    }
    
    public ObjectProperty<LoopView> loopProperty() {
        return loopProperty;
    }
    
    public Annotation getExecutionTimeAnnotation(){
        return executionTimeAnnotation;
    }
    
    public Annotation getOperationTypeAnnotation(){
        return operationTypeAnnotation;
    }
    
    public Annotation getFailureTypesAnnotation(){
        return failureTypesAnnotation;
    }
    
    public Annotation getMessageSizeAnnotation(){
        return messageSizeAnnotation;
    }
    
    private void annotationInit(Annotation newAnnotation){
        arrow.getLine().layoutBoundsProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                var annotationLine = newAnnotation.getLine();

                var oldLineCenter = (Bounds) oldValue;
                var newLineCenter = (Bounds) newValue;
                
                annotationLine.setEndX(newLineCenter.getCenterX());
                annotationLine.setEndY(newLineCenter.getCenterY());
                
                newAnnotation.setTranslateX(newAnnotation.getTranslateX() + (newLineCenter.getCenterX() - oldLineCenter.getCenterX()));
                newAnnotation.setTranslateY(newAnnotation.getTranslateY() + (newLineCenter.getCenterY() - oldLineCenter.getCenterY()));
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

            executionTimeAnnotation.setDisplayed(value);
        }
    }

    @Override
    public boolean areAnnotationsDisplayed(){
        return annotationsDisplayed;
    }
    
    public void setInLoop(LoopView loopView){
        loopProperty.setValue(loopView);

        if(loopView != null){
            this.arrow.setStroke(Color.RED);
        }
        else{
            this.arrow.setStroke(Color.BLACK);
        }
    }
    
    public void createToggleAnnotationsMenu() {
        addMenuItem(createToggleAnnotationsMenuItem());
    }
    
    public void createMessagePropertiesMenu(EventHandler<ActionEvent> handler) {
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction(handler);
        addMenuItem(menuProperties);
    }
}
