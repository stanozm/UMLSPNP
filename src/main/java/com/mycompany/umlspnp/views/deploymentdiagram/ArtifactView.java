/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.views.deploymentdiagram;

import com.mycompany.umlspnp.views.common.NamedRectangle;
import javafx.scene.paint.Color;

/**
 *
 * @author 10ondr
 */
public class ArtifactView extends NamedRectangle {

    public ArtifactView(double x, double y, double width, double height) {
        super(x, y, width, height, "New artifact");
        
        this.setFill(Color.WHITE);
    }
}
