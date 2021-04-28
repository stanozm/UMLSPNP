package cz.muni.fi.umlspnp.models.sequencediagram;

import cz.muni.fi.umlspnp.models.BasicNode;
import java.util.ArrayList;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * Lifeline activation which contains the individual messages
 * as specified by the sequence diagram specification 
 *
 */
public class Activation extends BasicNode {
    private final Lifeline lifeline;
    private final ObservableMap<Number, Message> messages;
    
    // Only available while transforming into SPNP
    private ArrayList<Message> sortedMessages = null;
    
    public Activation(Lifeline lifeline) {
        this.lifeline = lifeline;
        this.messages = FXCollections.observableHashMap();
    }

    public Lifeline getLifeline() {
        return lifeline;
    }
    
    public void addMessage(Message newMessage){
        messages.put(newMessage.getObjectInfo().getID(), newMessage);
    }

    public void removeMessage(Message removedMessage){
        messages.remove(removedMessage.getObjectInfo().getID());
    }
    
    public Collection<Message> getMessages(){
        return messages.values();
    }
    
    public void addMessagesChangeListener(MapChangeListener listener){
        messages.addListener(listener);
    }
    
    public void setSortedMessages(ArrayList<Message> sortedMessages) {
        this.sortedMessages = sortedMessages;
    }

    public ArrayList<Message> getSortedMessages() {
        return sortedMessages;
    }
}
