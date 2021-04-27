package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.deploymentdiagram.CommunicationLink;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import com.mycompany.umlspnp.models.deploymentdiagram.State;
import com.mycompany.umlspnp.models.sequencediagram.Loop;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import cz.muni.fi.spnp.core.models.PetriNet;
import cz.muni.fi.spnp.core.models.places.Place;
import cz.muni.fi.spnp.core.models.places.StandardPlace;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.util.Pair;

/**
 *  Constants and functions used during the SPNP transformation process.
 *
 */
public class SPNPUtils {
    public static final int SPNP_MAX_NAME_LENGTH = 20;
    public static int placeCounter = 0;
    public static int transitionCounter = 0;
    public static int arcCounter = 0;

    public static int functionCounter = 0;

    public static void resetCounters() {
        placeCounter = 0;
        transitionCounter = 0;
        arcCounter = 0;
        functionCounter = 0;
    }
    
    public static Place getPlaceFromNet(PetriNet petriNet, String placeName) {
        for(Place place : petriNet.getPlaces()) {
            if(place.getName().equals(placeName))
                return place;
        }
        return null;
    }

    public static String prepareName(String name, int maxLength) {
        var result = name.replaceAll("\\s+", "").replaceAll(com.mycompany.umlspnp.common.Utils.SPNP_NAME_RESTRICTION_REPLACE_REGEX, "");
        if(result.length() > maxLength) {
            result = result.substring(0, maxLength);
        }
        return result;
    }
    
    public static String createPlaceName(String nodeName, String placeName) {
        var suffix = String.format("_%d", placeCounter);
        var prefix = String.format("P%s_%s", prepareName(nodeName, 8), prepareName(placeName, 8));
        return String.format("%s%s", prepareName(prefix, SPNP_MAX_NAME_LENGTH - suffix.length()), suffix);
    }
    
    public static String createTransitionName(String nodeName, String transitionName) {
        var suffix = String.format("_%d", transitionCounter);
        var prefix = String.format("T%s_%s", prepareName(nodeName, 8), prepareName(transitionName, 8));
        return String.format("%s%s", prepareName(prefix, SPNP_MAX_NAME_LENGTH - suffix.length()), suffix);
    } 

    public static String createTransitionName(String transitionName) {
        var suffix = String.format("_%d", transitionCounter);
        var prefix = prepareName("T" + transitionName, SPNP_MAX_NAME_LENGTH - suffix.length());
        return String.format("%s%s", prefix, suffix);
    }
    
    public static String createFunctionName(String functionName) {
        return String.format("_%d_%s", functionCounter++, functionName);
    }

    public static String getCombinedName(String firstNodeName, String secondNodeName) {
        return String.format("%s_%s", prepareName(firstNodeName, 4), prepareName(secondNodeName, 4));
    }

    private static boolean isNodeInConnectedNodes(Artifact node, Set<Pair<CommunicationLink, Artifact>> connectedNodes) {
        for(var pair : connectedNodes) {
            if(pair.getValue() == node)
                return true;
        }
        return false;
    }

    public static CommunicationLink getMessageCommunicationLink(Message message) {
        var firstLifeline = message.getFrom().getLifeline();
        var secondLifeline = message.getTo().getLifeline();
        var firstArtifact = firstLifeline.getArtifact();
        var secondArtifact = secondLifeline.getArtifact();

        for(var pair : firstArtifact.getConnectedNodes()) {
            var connected = pair.getValue().getConnectedNodesShallow();
            if(pair.getValue() == secondArtifact || isNodeInConnectedNodes(secondArtifact, connected)) {
                return pair.getKey();
            }
        }
        return null;
    }

    public static StandardPlace getStatePlace(List<PhysicalSegment> physicalSegments, DeploymentTarget targetNode, State state) {
        for(var physicalSegment : physicalSegments) {
            if(physicalSegment.getNode() == targetNode) {
                var statePlace = physicalSegment.getStatePlace(state);
                if(statePlace != null)
                    return statePlace;
            }
        }
        return null;
    }

    public static StandardPlace getDownPlace(List<PhysicalSegment> physicalSegments, DeploymentTarget targetNode) {
        for(var state : targetNode.getStates()) {
            if(state.isStateDOWN())
                return getStatePlace(physicalSegments, targetNode, state);
        }
        return null;
    }
    
    public static List<ServiceCallTreeNode> getLoopHighestControlServiceCall(ControlServiceSegment controlServiceSegment, ServiceCallTreeNode treeRoot, Loop loop) {
        List<ServiceCallTreeNode> highestNodes = new ArrayList<>();
        var messages = loop.getMessages();

        messages.forEach(message -> {
            var node = treeRoot.getNodeWithMessage(message);
            ServiceCallTreeNode prevNode = null;
            var nodeMessage = node.getMessage();
            
            while(messages.contains(nodeMessage) && !node.isRoot()) {
                prevNode = node;
                node = node.getParent();
                nodeMessage = node.getMessage();
            }
            if(prevNode != null && !highestNodes.contains(prevNode))
                highestNodes.add(prevNode);
        });

        return highestNodes;
    }
    
    public static DeploymentTarget getDeploymentTargetFromArtifact(Artifact artifact) {
        if(artifact == null)
            return null;
        if(artifact instanceof DeploymentTarget)
            return (DeploymentTarget) artifact;
        return artifact.getParent();
    }
}
