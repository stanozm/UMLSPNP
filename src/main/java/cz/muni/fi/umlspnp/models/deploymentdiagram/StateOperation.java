package cz.muni.fi.umlspnp.models.deploymentdiagram;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.models.OperationEntry;
import cz.muni.fi.umlspnp.models.ObservableString;
import cz.muni.fi.umlspnp.models.OperationType;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *  Class containing information about the node state's supported operations.
 *
 */
public class StateOperation extends ObservableString {
    @Expose(serialize = true)
    private final ObjectProperty<State> state = new SimpleObjectProperty<>();
    @Expose(serialize = true)
    private final ObservableList<OperationEntry> operationEntries;
    
    public StateOperation(State state){
        this.operationEntries = FXCollections.observableArrayList((OperationEntry param) -> new Observable[]{
            param.stringRepresentationProperty()
        });

        var stringChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                updateStringRepresentation();
            }
        };

        var stateChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(oldValue != null) {
                    var oldState = (State) oldValue;
                    oldState.stringRepresentationProperty().removeListener(stringChangeListener);
                }
                if(newValue != null) {
                    var newState = (State) newValue;
                    newState.stringRepresentationProperty().addListener(stringChangeListener);
                }
            }
        };
        
        var stringListChangeListener = new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                updateStringRepresentation();
            }
        };
        
        this.state.addListener(stringChangeListener);
        this.state.addListener(stateChangeListener);
        this.operationEntries.addListener(stringListChangeListener);

        this.state.setValue(state);

        this.updateStringRepresentation();
    }
    
    public State getState(){
        return this.state.getValue();
    }
    
    public ObjectProperty<State> stateProperty(){
        return this.state;
    }
    
    public ObservableList<OperationEntry> getOperationEntries(){
        return this.operationEntries;
    } 

    public void addOperationEntry(OperationEntry newOperation){
        this.operationEntries.add(newOperation);
        this.updateStringRepresentation();
    }
    
    public void addOperationEntry(OperationType operationType, Integer processingSpeed){
        addOperationEntry(new OperationEntry(operationType, processingSpeed));
    }

    @Override
    public String toString() {
        String stateString = "INVALID";
        if(getState() != null)
            stateString = getState().toString();
        String res_str = String.format("[" + stateString + "]");
        if(operationEntries.size() < 1){
            res_str += " None";
        }
        else{
            for(OperationEntry operation : operationEntries){
                res_str += ", ";
                res_str += operation;
            }
        }
        return res_str;
    }
}
