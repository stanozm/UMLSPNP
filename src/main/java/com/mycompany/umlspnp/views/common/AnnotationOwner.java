package com.mycompany.umlspnp.views.common;

import javafx.scene.control.MenuItem;

/**
 * Interface implemented by all elements that can contain annotations.
 *
 */
public interface AnnotationOwner {
    public void setAnnotationsDisplayed(boolean value);
    public boolean areAnnotationsDisplayed();

    public default MenuItem createToggleAnnotationsMenuItem(){
        String hideAnnotationsString = "Hide annotations";
        String showAnnotationsString = "Show annotations";
        MenuItem menuItemToggleAnnotations = new MenuItem(hideAnnotationsString);
        menuItemToggleAnnotations.setOnAction((e) -> {
            this.setAnnotationsDisplayed(!this.areAnnotationsDisplayed());
            if(this.areAnnotationsDisplayed()){
                menuItemToggleAnnotations.setText(hideAnnotationsString);
            }
            else{
                menuItemToggleAnnotations.setText(showAnnotationsString);
            }
        });
        return menuItemToggleAnnotations;
    }
}
