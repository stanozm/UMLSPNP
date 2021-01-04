/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.common;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
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
    
    public static String shortenString(String text, int maxLength){
        String shortenedString = text;
        if (text.length() > maxLength){
            shortenedString = text.substring(0, maxLength).concat("...");
        }
        return shortenedString;
    }
    
    public static Point2D getPositionRelativeTo(Node child, Node parent, Point2D coordinates){
        Point2D finalPosition = coordinates;
        var currentNode = child;
        while(currentNode.getParent() != null){
            finalPosition = currentNode.localToParent(finalPosition);
            currentNode = currentNode.getParent();
            if(currentNode.equals(parent))
                return finalPosition;
        }
        return null;
    }
}
