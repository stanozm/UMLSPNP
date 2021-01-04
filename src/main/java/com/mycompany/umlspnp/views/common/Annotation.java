/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.common;

import com.mycompany.umlspnp.common.Utils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Callback;

/**
 *
 * @author 10ondr
 */
public class Annotation extends NamedRectangle {
    private double cellHeight = 25;
    private final ListView<Object> items = new ListView();
    
    private final Line line = new Line();
    
    public Annotation(double x, double y, DoubleExpression targetX, DoubleExpression targetY, String name) {
        super(x, y, 0, 0, name, -1);

        this.setBoldHeader(true);
        this.setResizable(false);

        line.getStrokeDashArray().addAll(5.0, 5.0);
        
        line.startXProperty().bind(this.translateXProperty().add(this.widthProperty().divide(2)));
        line.startYProperty().bind(this.translateYProperty().add(this.heightProperty().divide(2)));
        
        items.setTranslateX(5);
        items.setTranslateY(20);
        this.getChildren().add(items);
        this.setEmpty(true);
        
        items.setMouseTransparent(true);
        items.setFocusTraversable(false);
        
        items.setFixedCellSize(cellHeight);
        
        items.setPrefWidth(10);

        this.items.boundsInParentProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(items.getItems().size() < 1){
                    setEmpty(true);
                }
                else{
                    setEmpty(false);
                    resizeByContent();
                }
            }
        });
        
        this.items.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> param) {
                ListCell<Object> cell = new ListCell<Object>() {

                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText("");
                        } else {
                            setText(String.valueOf(item));
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (!empty){ 
                                    double strWidth = computePrefWidth(getPrefHeight());
                                    if(strWidth > items.getPrefWidth()){
                                        items.setPrefWidth(strWidth + 10);
                                    }
                                    else{
                                        refreshItemsWidth();
                                    }
                                }
                            }
                        });
                    }
                };
                return cell;
            }
        });
    }
    
    public Line getLine(){
        return this.line;
    }
    
    public void setItems(ObservableList newList){
        this.items.setItems(newList);

        this.items.prefHeightProperty().bind(Bindings.size(items.getItems()).multiply(cellHeight).add(cellHeight + 5));
    }
    
    @Override
    public void setFill(Color newColor){
        super.setFill(newColor);
        
        // Because the cell's text auto-contrast cannot be turned off
        // the background can't be truly transparent (otherwise text would stay white)
        items.setStyle( "-fx-background-color: transparent; "
                      + "-fx-control-inner-background: " + newColor.toString().replace("0x", "#") + "; "
                      + "-fx-control-inner-background-alt: " + newColor.toString().replace("0x", "#") + "; ");
    }

    public void setEmpty(boolean value){
        if(this.isVisible() != !value)
            this.setVisible(!value);
        if(this.line.isVisible() != !value)
            this.line.setVisible(!value);
    }
    
    private void resizeByContent(){
        Bounds newBounds = this.items.getBoundsInParent();
        double newWidth = 10.0 + Math.max(nameLabel.getWidth(), newBounds.getWidth());
        double newHeight = 3.0 + nameLabel.getHeight() + newBounds.getHeight();
        this.changeDimensions(newWidth, newHeight);
    }
    
    private void refreshItemsWidth(){
        String longest = "";
        for(var item : items.getItems()){
            String strItem = String.valueOf(item);
            if (strItem.length() > longest.length())
                longest = strItem;
        }
        
        items.setPrefWidth(Utils.getStringWidth(longest) + 30);
    }
}
