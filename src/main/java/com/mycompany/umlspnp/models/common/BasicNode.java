/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.common;

import com.mycompany.umlspnp.common.ObjectInfo;
import com.mycompany.umlspnp.common.Utils;

/**
 *
 * @author 10ondr
 */
public class BasicNode {
    private final ObjectInfo objectInfo;
    
    public BasicNode(){
        this.objectInfo = new ObjectInfo(Utils.generateObjectID());
    }
    
    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }
}
