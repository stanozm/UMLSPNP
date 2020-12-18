/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.common;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;

/**
 *
 * @author 10ondr
 */
public class Utils {
    private static int objectIDCounter = 0;
    
    public static int generateObjectID(){
        return ++objectIDCounter;
    }
    
    public static double getStringWidth(String text){
        final Text t = new Text(text);
        new Scene(new Group(t));
        t.applyCss();
        return t.getLayoutBounds().getWidth();
    }
}
