package cz.muni.fi.umlspnp.common;

/**
 * Holds general info about an object.
 * 
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
