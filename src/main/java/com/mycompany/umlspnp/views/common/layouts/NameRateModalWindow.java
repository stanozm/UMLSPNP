package com.mycompany.umlspnp.views.common.layouts;

import java.util.regex.Pattern;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

/**
 * A modal window framework which provides functionalities to modal 
 * windows that edit a name and rate pair.
 * 
 */
public abstract class NameRateModalWindow extends ModalWindow {
    private final boolean isNormalized;
    
    public NameRateModalWindow(Stage parentStage, String windowName, boolean normalized) {
        super(parentStage, windowName);

        this.isNormalized = normalized;
    }
    
    protected boolean checkNameRateInputs(StringProperty nameProperty, StringProperty rateProperty){
        return checkNameInput(nameProperty) && checkRateInput(rateProperty);
    }

    private boolean checkNameInput(StringProperty nameProperty){        
        if(nameProperty.isEmpty().getValue()){
            showAlert("Name is not valid.");
            return false;
        }
        
        var regex = Pattern.compile("^([a-zA-Z])[a-zA-Z0-9\\s_]*$");
        if(!regex.matcher(nameProperty.getValue()).matches()) {
            showAlert("Name must start with a letter and contain only english letters, numbers, whitespace and underscore.");
            return false;
        }
        
        return true;
    }
    
    private boolean checkRateInput(StringProperty rateProperty){
        String errorMessage = null;
        
        try {
            double rate = parseRate(rateProperty);
            if(isNormalized && rate > 1.0)
                errorMessage = "Rate is out of range (0.0 to 1.0).";
            else if(rate < 0.0)
                errorMessage = "Rate has to be greater or equal to 0.";
        }
        catch(Exception e) {
            errorMessage = "Error while parsing rate value.";
        }
        
        if(errorMessage != null){
            showAlert(errorMessage);
            return false;
        }
        return true;
    }
    
    protected double parseRate(StringProperty rateProperty){
        return Double.parseDouble(rateProperty.getValue());
    }
}
