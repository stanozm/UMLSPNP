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
    private int groupID = -1;
    
    public ObjectInfo(int objectID){
        this.objectID = objectID;
    }
    
    public int getID(){
        return this.objectID;
    }
    
    public void setGroupID(int id){
        groupID = id;
    }
    
    public int getGroupID(){
        return this.groupID;
    }
}
