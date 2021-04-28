package cz.muni.fi.umlspnp.views.common;

import cz.muni.fi.umlspnp.common.ObjectInfo;
import cz.muni.fi.umlspnp.common.Utils;
import cz.muni.fi.umlspnp.views.common.layouts.BooleanModalWindow;
import cz.muni.fi.umlspnp.views.common.layouts.EditableListView;
import cz.muni.fi.umlspnp.views.common.layouts.IntegerModalWindow;
import cz.muni.fi.umlspnp.views.common.layouts.PropertiesModalWindow;
import cz.muni.fi.umlspnp.views.common.layouts.StringModalWindow;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

/**
 *  Provides a base class for a element that will be rendered in one of the
 * diagrams.
 * Each of these elements move in a virtual grid for better alignment.
 *
 */
public abstract class BasicElement extends Group{
    private final ObjectInfo objectInfo;
    protected double gridSize = 10;
    
    protected final ContextMenu contextMenu;
    
    public BasicElement(int modelObjectID){
        this.objectInfo = new ObjectInfo(modelObjectID);
        
        contextMenu = new ContextMenu();
    }
    
    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }
    
    public void createBooleanModalWindow(String windowName,
                                         String promptText,
                                         Runnable callbackTrue,
                                         Runnable callbackFalse) {
        BooleanModalWindow confirmWindow = new BooleanModalWindow((Stage) getScene().getWindow(), windowName, promptText);
        confirmWindow.showAndWait();
        if(confirmWindow.getResult()){
            if(callbackTrue != null)
                callbackTrue.run();
        }
        else if(callbackFalse != null) {
                callbackFalse.run();
        }
    }
    
    public void createStringModalWindow(String windowName,
                                        String promptText,
                                        StringProperty property,
                                        String restrictionRegex) {
        var modal = new StringModalWindow((Stage) getScene().getWindow(), windowName, promptText, property);
        if(restrictionRegex != null)
            modal.setStringRestrictionRegex(restrictionRegex);
        modal.showAndWait();
    }
    
    public void createIntegerModalWindow(String windowName,
                                         String promptText,
                                         Integer min,
                                         Integer max,
                                         IntegerProperty property) {
        var modal = new IntegerModalWindow((Stage) getScene().getWindow(), windowName,
                                            promptText, min, max, property);
        modal.showAndWait();
    }
    
    public ContextMenu getContextMenu(){
        return contextMenu;
    }

    public void addMenuItem(MenuItem newMenuItem){
        this.getContextMenu().getItems().add(newMenuItem);
    }
    
    public void createMenuSeparator() {
        SeparatorMenuItem separator = new SeparatorMenuItem();
        addMenuItem(separator);
    }
    
    public void createMenuItem(String itemName, EventHandler<ActionEvent> handler) {
        var menuItem = new MenuItem(itemName);
        menuItem.setOnAction(handler);
        addMenuItem(menuItem);
    }

    public void createBooleanMenu(String menuName, String windowName, String promptText,
                                  Runnable callbackTrue, Runnable callbackFalse) {
        createMenuItem(menuName, (e) -> {
            createBooleanModalWindow(windowName, promptText, callbackTrue, callbackFalse);
        });
    }
    
    public void createStringMenu(String menuName, String windowName, String promptText,
                                 StringProperty property, String restrictionRegex) {
        createMenuItem(menuName, (e) -> {
            createStringModalWindow(windowName, promptText, property, restrictionRegex);
        });
    }
    
    public void createIntegerMenu(String menuName, String windowName, String promptText,
                                  Integer min, Integer max, IntegerProperty property) {
        createMenuItem(menuName, (e) -> {
            createIntegerModalWindow(windowName, promptText, min, max, property);
        });
    }
    
    public void createConfirmMenu(String menuName, String promptText, Runnable callback) {
        createBooleanMenu(menuName, "Confirm", promptText, callback, null);
    }
    
    public void createPropertiesMenu(String windowName, List<EditableListView> sections) {
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction((e) -> {
            var modal = new PropertiesModalWindow((Stage) getScene().getWindow(), windowName, sections);
            modal.showAndWait();
        });
        addMenuItem(menuProperties);
    }

    public void showAlert(String windowName, String title, String text) {
        var alert = Utils.createAlertDialog(windowName, title, text);
        alert.showAndWait();
    }
    
    protected void lockMovement(Node childElement){
        this.translateXProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                double diff = (double) newValue - (double) oldValue;
                childElement.setTranslateX(childElement.getTranslateX() + diff);
            }
        });
        
        this.translateYProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                double diff = (double) newValue - (double) oldValue;
                childElement.setTranslateY(childElement.getTranslateY() + diff);
            }
        });
    }
    
    public void setGridSize(double newGridSize) {
        this.gridSize = newGridSize;
    }
}
