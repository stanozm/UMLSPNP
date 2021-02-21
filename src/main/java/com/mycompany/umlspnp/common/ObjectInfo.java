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
    private int tier = -1;  // indicates how high in the parent-child hierarchy a node is
                            // root node has a tier 0
    
    public ObjectInfo(int objectID){
        this.objectID = objectID;
    }
    
    public int getID(){
        return this.objectID;
    }
    
    public void setTier(int id){
        tier = id;
    }
    
    public int getTier(){
        return this.tier;
    }
}
