package cz.muni.fi.umlspnp.models;

import cz.muni.fi.umlspnp.common.ObjectInfo;
import cz.muni.fi.umlspnp.common.Utils;

/**
 *  Basic object node which is further extended in Deployment and Sequence diagram models.
 *
 */
public abstract class BasicNode {
    private final ObjectInfo objectInfo;
    
    public BasicNode(){
        this.objectInfo = new ObjectInfo(Utils.generateObjectID());
    }
    
    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }
}
