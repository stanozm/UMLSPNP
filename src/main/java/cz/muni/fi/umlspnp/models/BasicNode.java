package cz.muni.fi.umlspnp.models;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.common.BaseObject;
import cz.muni.fi.umlspnp.common.ObjectInfo;
import cz.muni.fi.umlspnp.common.Utils;

/**
 *  Basic object node which is further extended in Deployment and Sequence diagram models.
 *
 */
public abstract class BasicNode implements BaseObject {
    @Expose(serialize = true)
    private final ObjectInfo objectInfo;
    
    public BasicNode(){
        this.objectInfo = new ObjectInfo(Utils.generateObjectID());
    }

    public void setId(Integer id){
        this.objectInfo.setID(id);
        Utils.updateId(id);
    }
    
    @Override
    public ObjectInfo getObjectInfo(){
        return this.objectInfo;
    }
}
