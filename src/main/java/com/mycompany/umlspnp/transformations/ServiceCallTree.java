package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.sequencediagram.Activation;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;

/**
 * An abstract tree which represents the message call hierarchy and is 
 * constructed for several purposes during the transformation process.
 *
 */
public class ServiceCallTree {
    private ServiceCallTreeNode root = null;

    public ServiceCallTree(SequenceDiagram sequenceDiagram) {
        buildTree(sequenceDiagram);
    }

    private void buildTree(SequenceDiagram sequenceDiagram) {
        var highestLifeline = sequenceDiagram.getHighestLevelLifeline();
        if(highestLifeline == null)
            return;
        var activations = highestLifeline.getSortedActivations();
        if(activations.size() < 1)
            return;
        var highestActivation = activations.get(0);
        var messages = highestActivation.getSortedMessages();

        if(messages.size() < 1)
            return;

        root = new ServiceCallTreeNode(highestLifeline.getArtifact());
        processActivation(root, highestActivation);
    }

    private void processActivation(ServiceCallTreeNode parent, Activation activation) {
        var parentMessage = parent.getMessage();
        int parentMessageOrder = 0;
        if(parentMessage != null)
            parentMessageOrder = parentMessage.getOrder();

        var sortedMessages = activation.getSortedMessages();
        for (var message : sortedMessages) {
            if(message.getOrder() >= parentMessageOrder) {
                if(message.isSelfMessage() || message.getTo() != activation) {
                    var node = new ServiceCallTreeNode(message.getTo().getLifeline().getArtifact(), message);
                    node.setParent(parent);
                    int parentOrder;
                    if(parentMessage == null)
                        parentOrder = 0;
                    else
                        parentOrder = sortedMessages.indexOf(parentMessage) + 1;
                    node.setOrder(sortedMessages.indexOf(message) - parentOrder);
                    if(!message.isLeafMessage()) {
                        processActivation(node, message.getTo());
                    }
                }
                else if(message.getOrder() > parentMessageOrder) {
                    // Encountered next caller message - all following messages are its responsibility
                    break;
                }
            }
        }
    }

    public ServiceCallTreeNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        if(root == null)
            return "ROOT: null";

        return root.toString();
    }
}
