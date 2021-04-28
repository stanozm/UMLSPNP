package cz.muni.fi.umlspnp.views.sequencediagram;

import cz.muni.fi.umlspnp.views.common.NamedRectangle;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * View which renders a loop in the sequence diagram.
 *
 */
public class LoopView extends NamedRectangle {
    public LoopView(double x, double y, double width, double height, String name, int modelObjectID) {
        super(x, y, width, height, name, modelObjectID);
        
        this.setFill(Color.TRANSPARENT);
    }

    public void addChangeListener(ChangeListener cl){
        this.boundsInParentProperty().addListener(cl);
    }
    
    public Rectangle getRectangle(){
        return rect;
    }
}
