/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.sequencediagram;

import com.mycompany.umlspnp.views.common.AnnotationOwner;
import com.mycompany.umlspnp.views.common.ConnectionSlot;
import com.mycompany.umlspnp.views.common.ConnectionView;
import javafx.scene.Group;

/**
 *
 * @author 10ondr
 */
public class MessageView extends ConnectionView implements AnnotationOwner{

    public MessageView(int modelObjectID, ConnectionSlot source, ConnectionSlot destination, Group diagramRoot) {
        super(modelObjectID, source, destination, diagramRoot);
    }

    @Override
    public void setAnnotationsDisplayed(boolean value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean areAnnotationsDisplayed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
