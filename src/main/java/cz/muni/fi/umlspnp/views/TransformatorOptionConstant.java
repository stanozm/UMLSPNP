package cz.muni.fi.umlspnp.views;

import javafx.scene.control.ComboBox;

/**
 * Constant SPNP option view.
 *
 */
public class TransformatorOptionConstant extends ComboBox implements TransformatorOption {
    private final String key;

    public TransformatorOptionConstant(String key) {
        super();
        
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
    
    public String getOptionValue() {
        return (String) getSelectionModel().getSelectedItem();
    }
}
