package cz.muni.fi.umlspnp.common;

import com.google.gson.annotations.Expose;

/**
 * Holds general info about an object.
 * 
 */
public class ObjectInfo {
    @Expose(serialize = true)
    private int objectID;
    
    public ObjectInfo(int objectID){
        this.objectID = objectID;
    }
    
    public void setID(int id){
        this.objectID = id;
    }

    public int getID(){
        return this.objectID;
    }
}
