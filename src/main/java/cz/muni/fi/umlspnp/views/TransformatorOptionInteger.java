package cz.muni.fi.umlspnp.views;

import javafx.scene.control.TextField;

/**
 * Integer SPNP option view.
 * 
 */
public class TransformatorOptionInteger extends TextField implements TransformatorOption {
    private final String key;

    public TransformatorOptionInteger(String key, String initValue) {
        super(initValue);
        
        this.key = key;
    }
    
    @Override
    public String getKey() {
        return key;
    }
    
    public Integer getOptionValue() {
        return Integer.parseInt(getText());
    }
}

