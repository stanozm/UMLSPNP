package cz.muni.fi.umlspnp.models.deploymentdiagram;

import cz.muni.fi.umlspnp.models.NamedNode;
import java.util.HashSet;
import java.util.Set;
import javafx.util.Pair;

/**
 * Artifact (Component) as specified by the formal deployment diagram specification.
 *  Artifact is the basic node which can have no other children in the deployment diagram
 * and it must have one itself.
 *
 */
public class Artifact extends NamedNode{
    private final DeploymentTarget DTparent;
    
    public Artifact(String name, DeploymentTarget parent){
        super(name);
        
        DTparent = parent;
    }
    
    public DeploymentTarget getParent(){
        return DTparent;
    }
    
    /**
     * 
     * @return Set of pairs of connected nodes and communication links they are connected with.
     */
    public Set<Pair<CommunicationLink, Artifact>> getConnectedNodes(){
        var connectedNodes = new HashSet<Pair<CommunicationLink, Artifact>>();
        if(this.DTparent != null){
            connectedNodes.add(new Pair<>(null, this.DTparent));
            connectedNodes.addAll(this.DTparent.getConnectedNodes(false, false));
        }
        
        return connectedNodes;
    }

    /**
     * 
     * @param directionUp True means direction towards children, false means direction towards parent.
     * @param shallow If true, only the parental hierarchy is considered, otherwise all 
     * nodes connected via a communication link are also considered.
     * @return Set of pairs of connected nodes and communication links they are connected with.
     */
    public Set<Pair<CommunicationLink, Artifact>> getConnectedNodes(boolean directionUp, boolean shallow){
        if(directionUp) {
            var connectedNodes = new HashSet();
            // NOTE: Add connected nodes if it will be possible to create artifact connections
            return connectedNodes;
        }
        else {
            var connectedNodes = new HashSet<Pair<CommunicationLink, Artifact>>();
            if(this.DTparent != null){
                connectedNodes.add(new Pair<>(null, this.DTparent));
                connectedNodes.addAll(this.DTparent.getConnectedNodes(false, shallow));
            }
            return connectedNodes;
        }
    }
    
    public Set<Pair<CommunicationLink, Artifact>> getConnectedNodesShallow() {
        var upwards = getConnectedNodes(true, true);
        var downwards = getConnectedNodes(false, true);

        upwards.addAll(downwards);
        return upwards;
    }
}
