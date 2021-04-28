package cz.muni.fi.umlspnp.transformations;

import cz.muni.fi.umlspnp.models.deploymentdiagram.Artifact;
import cz.muni.fi.umlspnp.models.sequencediagram.Message;
import java.util.ArrayList;
import java.util.List;

/**
 *  A node in the Service Call tree containing references to the message
 * and artifact and the order on its parental level (tier) in the tree.
 *
 */
public class ServiceCallTreeNode {
    private final Message message;
    private final Artifact artifact;
    private int order = 0;

    private ServiceCallTreeNode parent = null;
    private final List<ServiceCallTreeNode> children = new ArrayList<>();

    private boolean processed = false;
    private boolean markedForLabelCheck = false;
    private boolean markedForLoopCheck = false;

    public ServiceCallTreeNode(Artifact artifact) {
        this.artifact = artifact;
        this.message = null;
    }

    public ServiceCallTreeNode(Artifact artifact, Message message) {
        this.artifact = artifact;
        this.message = message;
    }
    
    public ServiceCallTreeNode getParent() {
        return parent;
    }

    public void setParent(ServiceCallTreeNode newParent) {
        if(this.parent != null)
            this.parent.removeChild(this);
        if(newParent != null)
            newParent.addChild(this);
        this.parent = newParent;
    }

    public List<ServiceCallTreeNode> getChildren() {
        return children;
    }
    
    public void addChild(ServiceCallTreeNode newChild) {
        this.children.add(newChild);
    }

    public boolean removeChild(ServiceCallTreeNode newChild) {
        return this.children.remove(newChild);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Message getMessage() {
        return message;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean value) {
        this.processed = value;
    }

    public boolean isMarkedForLabelCheck() {
        return markedForLabelCheck;
    }

    public void setMarkedForLabelCheck(boolean value) {
        this.markedForLabelCheck = value;
    }

    public boolean isMarkedForLoopCheck() {
        return markedForLoopCheck;
    }

    public void setMarkedForLoopCheck(boolean value) {
        this.markedForLoopCheck = value;
    }

    public ServiceCallTreeNode getNodeWithMessage(Message message) {
        if(this.getMessage() == message)
            return this;

        for(var child : getChildren()) {
            var serviceCallNode = child.getNodeWithMessage(message);
            if(serviceCallNode != null)
                return serviceCallNode;
        }
        return null;
    }
    
    public String getCompoundOrderString() {
        var result = new StringBuilder();
        ServiceCallTreeNode node = this;
        do {
            result.append(String.format(".%d", node.getOrder() + 1));
            node = node.parent;
            if(node == null)
                break;
        }
        while(!node.isRoot());
        return result.reverse().toString();
    }
    
    @Override
    public String toString() {
        var result = new StringBuilder();
        if(this.isRoot())
            result.append("[ROOT]");
        else
            result.append(String.format("%s   %s", getCompoundOrderString(), message.nameProperty().getValue()));
        result.append(String.format(" (node \"%s\"):%n", artifact.getNameProperty().getValue()));
        result.append(String.format("---------%n"));
        result.append(String.format("children:%n"));
        children.forEach(child -> {
            result.append(String.format("\t%s   %s", child.getCompoundOrderString(), child.getMessage().nameProperty().getValue()));
            result.append(String.format(" (node \"%s\")", child.getArtifact().getNameProperty().getValue()));
            if(child.isLeaf())
                result.append(" [LEAF]");
            result.append(System.lineSeparator());
        });
        result.append(System.lineSeparator());
        result.append(System.lineSeparator());
        children.forEach(child -> {
            if(!child.isLeaf())
                result.append(child.toString());
        });
        return result.toString();
    }
}
