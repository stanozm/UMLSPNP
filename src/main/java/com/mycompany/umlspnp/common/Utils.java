/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.common;

/**
 *
 * @author 10ondr
 */
public class Utils {
    private static int objectIDCounter = 0;
    
    public static int generateObjectID(){
        return ++objectIDCounter;
    }
}
