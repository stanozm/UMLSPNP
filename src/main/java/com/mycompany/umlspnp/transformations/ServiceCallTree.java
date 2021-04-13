/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import com.mycompany.umlspnp.models.sequencediagram.Activation;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;

/**
 *
 * @author 10ondr
 */
public class ServiceCallTree {
    private ServiceCallNode root = null;

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

        root = new ServiceCallNode(highestLifeline.getArtifact());
        processActivation(root, highestActivation);
    }

    private void processActivation(ServiceCallNode parent, Activation activation) {
        var parentMessage = parent.getMessage();
        int parentMessageOrder = 0;
        if(parentMessage != null)
            parentMessageOrder = parentMessage.getOrder();

        var sortedMessages = activation.getSortedMessages();
        for (var message : sortedMessages) {
            if(message.getOrder() >= parentMessageOrder) {
                if(message.isSelfMessage() || message.getTo() != activation) {
                    var node = new ServiceCallNode(message.getTo().getLifeline().getArtifact(), message);
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

    public ServiceCallNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        if(root == null)
            return "ROOT: null";

        return root.toString();
    }
}
