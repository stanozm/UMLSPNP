/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import com.mycompany.umlspnp.common.ObjectInfo;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

/**
 *
 * @author 10ondr
 */
public class NamedRectangle extends BasicRectangle{
    private final ObjectInfo objectInfo;
    private final Label nameLabel;
    
    public NamedRectangle(double x, double y, double width, double height, String name, int modelObjectID) {
        super(x, y, width, height);
        
        this.objectInfo = new ObjectInfo(modelObjectID);
        
        nameLabel = new Label(name);
        nameLabel.translateXProperty().bind(rect.translateXProperty().add(rect.widthProperty().divide(2)).subtract(nameLabel.widthProperty().divide(2)));
        nameLabel.translateYProperty().bind(rect.yProperty());
        
        this.getChildren().add(nameLabel);
    }

    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }

    public void setName(String newName){
        nameLabel.setText(newName);
    }
    
    public StringProperty getNameProperty(){
        return nameLabel.textProperty();
    }
    
    public void bindLabelTo(StringProperty name){
        this.nameLabel.textProperty().bind(name);
    }
}
