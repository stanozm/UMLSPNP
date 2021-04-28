package cz.muni.fi.umlspnp.views.common;

import cz.muni.fi.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

/**
 *  A basic rectangle complemented by a string label.
 *
 */
public class NamedRectangle extends BasicRectangle{
    protected final Label nameLabel;
    
    public NamedRectangle(double x, double y, double width, double height, String name, int modelObjectID) {
        super(modelObjectID, x, y, width, height);
        
        nameLabel = new Label(name);
        nameLabel.translateXProperty().bind(rect.translateXProperty().add(rect.widthProperty().divide(2)).subtract(nameLabel.widthProperty().divide(2)));
        nameLabel.translateYProperty().bind(rect.yProperty());

        // Label clipping when its width is not sufficient to display it
        widthProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                Platform.runLater(() -> {
                    nameLabel.setMaxWidth(widthProperty().getValue() - 10.0);
                });     
            }
        });

        this.getChildren().add(nameLabel);
    }

    public final void setBoldHeader(boolean value){
        if(value){
            nameLabel.setStyle("-fx-font-weight: bold");
        }
        else{
            nameLabel.setStyle("-fx-font-weight: normal");
        }
    }
    
    public final void setName(String newName){
        nameLabel.setText(newName);
    }
    
    public StringProperty getNameProperty(){
        return nameLabel.textProperty();
    }
    
    public void bindLabelTo(StringProperty name){
        this.nameLabel.textProperty().bind(name);
    }
    
    public DeploymentTargetView getParentDeploymentTargetview(){
        return (DeploymentTargetView) this.getParent();
    }
}
