package cz.muni.fi.umlspnp.models.deploymentdiagram;

import cz.muni.fi.umlspnp.models.ConnectionFailure;
import cz.muni.fi.umlspnp.models.Connection;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;

/**
 * Connection between Deployment Targets in Deployment Diagram.
 *
 */
public class CommunicationLink extends Connection<DeploymentTarget> {
    private final ObservableList<LinkType> allLinkTypes;
    
    // Annotations
    private final ObservableList<LinkType> linkType; // NOTE: This list will always contain exactly one item
    private final ObservableList<ConnectionFailure> linkFailures;
    
    public CommunicationLink(DeploymentTarget target1, DeploymentTarget target2, ObservableList<LinkType> allLinkTypes){
        super(target1, target2);
        
        this.allLinkTypes = allLinkTypes;
        
        this.allLinkTypes.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if(change.wasRemoved()){
                        for(var removed : change.getRemoved()){
                            if (removed.equals(getLinkType())){
                                setDefaultLinkType();
                            }
                        }
                    }
                }
            }
        });

        linkType = FXCollections.observableArrayList(
                new Callback<LinkType, Observable[]>() {
                    @Override
                    public Observable[] call(LinkType param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        linkFailures = FXCollections.observableArrayList(
                new Callback<ConnectionFailure, Observable[]>() {
                    @Override
                    public Observable[] call(ConnectionFailure param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        setDefaultLinkType();
    }
 
    public ObservableList<LinkType> getLinkTypeList(){
        return linkType;
    }
    
    public LinkType getLinkType(){
        var linkTypeList = getLinkTypeList();
        if(linkTypeList.size() > 0)
            return linkTypeList.get(0);
        return null;
    }
    
    public void setLinkType(LinkType newLinkType){
        if(linkType.size() > 0)
            linkType.remove(0);
        linkType.add(newLinkType);
    }
    
    private void setDefaultLinkType(){
        if(allLinkTypes.size() > 0){
            setLinkType(allLinkTypes.get(0));
        }
        else{
            System.err.println("Error: There is no default link type.");
        }
    }
    
    public ObservableList<ConnectionFailure> getLinkFailures(){
        return linkFailures;
    }
    
    public void addLinkFailure(ConnectionFailure newLinkFailure){
        linkFailures.add(newLinkFailure);
    }
    
    public void cleanup(){
        if(target1 != null)
            target1.removeConnection(this);
        if(target2 != null)
            target2.removeConnection(this);
    }
}
