package cz.muni.fi.umlspnp.views.sequencediagram;

import cz.muni.fi.umlspnp.views.common.NamedRectangle;
import java.util.Collection;
import java.util.HashMap;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *  View which renders the lifeline in a sequence diagram.
 * It shows as a rectangle with a vertical line (span line) on which 
 * the individual activations are positioned.
 *
 */
public class LifelineView extends NamedRectangle {
    private final Line spanLine;
    private final HashMap<Number, ActivationView> activationViews;
    
    protected final Label highestLevelLabel;
    
    public LifelineView(double x, double y, double width, double height, int modelObjectID) {
        super(x, y, width, height, "Unlinked lifeline", modelObjectID);
        
        this.setResizable(false, false);
        this.setDraggable(true, false);
        
        spanLine = new Line();
        initSpanLine();
        
        activationViews = new HashMap<>();
        
        highestLevelLabel = new Label();
        initHighestLevelLabel();

        this.getChildren().add(highestLevelLabel);
        this.getChildren().add(spanLine);
    }
 
    private void initActivation(ActivationView activationView){
        activationView.setGridSize(1.0);
        activationView.setPropagateEvents(false);
        activationView.setFill(Color.WHITE);
        activationView.setMinHeight(30);
        activationView.setResizable(true, false);
        activationView.setDraggable(false, true);
        activationView.setTranslateX(this.getWidth() / 2 - activationView.getWidth() / 2);
        activationView.setTranslateY(this.getHeight() + 30);
        activationView.translateYProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                double minY = spanLine.getStartY() + 10;
                double maxY = spanLine.getEndY() - 10;
                if((Double) newValue < minY || (Double) newValue + activationView.getHeight() > maxY)
                    activationView.setTranslateY((Double) oldValue);
            }});
    }

    private void initSpanLine(){
        spanLine.getStrokeDashArray().addAll(7.0, 4.0);
        spanLine.startXProperty().bind(this.widthProperty().divide(2));
        spanLine.startYProperty().bind(this.heightProperty());
        spanLine.endXProperty().bind(spanLine.startXProperty());
        spanLine.endYProperty().bind(new SimpleDoubleProperty(900));
    }

    private void initHighestLevelLabel() {
        highestLevelLabel.setText("(Highest level lifeline)");
        highestLevelLabel.setTranslateY(-20);
        highestLevelLabel.translateXProperty().bind(this.widthProperty().divide(2).subtract(highestLevelLabel.widthProperty().divide(2)));
        highestLevelLabel.setVisible(false);
    }

    public void setIsHighest(boolean value) {
        this.highestLevelLabel.setVisible(value);
        this.setBoldHeader(value);
    }

    public ActivationView createActivationView(int modelObjectID) {
        var activationView = new ActivationView(modelObjectID, 0, 0, 10, 150);
        activationViews.put(modelObjectID, activationView);
        initActivation(activationView);

        this.getChildren().add(activationView);
        return activationView;
    }

    public ActivationView removeActivationView(int modelObjectID) {
        var activationView = getActivationView(modelObjectID);
        if(activationView == null)
            return null;
        
        this.getChildren().remove(activationView);
        return activationViews.remove(modelObjectID);
    }
    
    public ActivationView getActivationView(int modelObjectID) {
        return activationViews.get(modelObjectID);
    }

    public Collection<ActivationView> getActivationViews() {
        return activationViews.values();
    }
}
