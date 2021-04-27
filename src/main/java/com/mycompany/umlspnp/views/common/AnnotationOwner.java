package com.mycompany.umlspnp.views.common;

/**
 * Interface implemented by all elements that can contain annotations.
 *
 */
public interface AnnotationOwner {
    public void setAnnotationsDisplayed(boolean value);
    public boolean areAnnotationsDisplayed();
}
