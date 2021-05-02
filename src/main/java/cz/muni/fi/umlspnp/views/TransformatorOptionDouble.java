package cz.muni.fi.umlspnp.views;

import javafx.scene.control.TextField;

/**
 * Double SPNP option view.
 *
 */
public class TransformatorOptionDouble extends TextField implements TransformatorOption {
    private final String key;

    public TransformatorOptionDouble(String key, String initValue) {
        super(initValue);
        
        this.key = key;
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    public Double getOptionValue() {
        return Double.parseDouble(getText());
    }
}
