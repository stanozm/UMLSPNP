package cz.muni.fi.umlspnp.views;

import cz.muni.fi.umlspnp.views.common.BasicElement;
import cz.muni.fi.umlspnp.views.common.BasicRectangle;
import cz.muni.fi.umlspnp.views.common.ConnectionContainer;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 *  Base view for a diagram.
 * 
 */
public abstract class DiagramView extends GridPane{
    protected final MenuBar diagramMenu = new MenuBar();
    protected final Pane diagramPane = new Pane();
    
    protected double originalPositionX, originalPositionY;
    
    protected final ConnectionContainer connectionContainer = new ConnectionContainer();
    
    public DiagramView(){
        diagramPane.setStyle("-fx-background-color: white");
        
        GridPane.setVgrow(diagramPane, Priority.ALWAYS);
        GridPane.setHgrow(diagramPane, Priority.ALWAYS);
        
        GridPane.setConstraints(diagramMenu, 0, 0);
        GridPane.setConstraints(diagramPane, 0, 1);

        setMousePressedCallback(MouseButton.SECONDARY, (e) -> {
            originalPositionX = e.getSceneX();
            originalPositionY = e.getSceneY();
        });
        
        this.getChildren().addAll(diagramMenu, diagramPane);
        diagramMenu.toFront();
    }
    
    public void addMenu(Menu newMenu){
        diagramMenu.getMenus().add(newMenu);
    }
    
    public void setActive(boolean value){
        diagramPane.setVisible(value);
    }
    
    protected final void setMousePressedCallback(MouseButton mouseButton, Consumer<MouseEvent> callback) {
        this.setOnMousePressed((e) -> {
            if(e.getButton() == mouseButton){
                callback.accept(e);
            }
            e.consume();
        });
    }
    
    protected final void setMouseDraggedCallback(MouseButton mouseButton, Consumer<MouseEvent> callback) {
        this.setOnMouseDragged((e) -> {
            if(e.getButton() == mouseButton){
                callback.accept(e);
            }
            e.consume();
        });
    }
    
    public ConnectionContainer getConnectionContainer(){
        return connectionContainer;
    }
    
    public void startConnection(BasicElement startingNode){
        connectionContainer.clear();
        connectionContainer.setFirstElement(startingNode);
    }
    
    public void registerNodeToSelect(BasicRectangle node, EventHandler eh){
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, eh);
    }
}
