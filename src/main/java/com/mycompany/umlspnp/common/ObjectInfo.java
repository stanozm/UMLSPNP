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
public class ObjectInfo {
    private final int objectID;
    
    public ObjectInfo(int objectID){
        this.objectID = objectID;
    }
    
    public int getID(){
        return this.objectID;
    }
}
