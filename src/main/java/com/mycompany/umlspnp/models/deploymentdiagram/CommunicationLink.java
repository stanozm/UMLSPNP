/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.BasicNode;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

/**
 *
 * @author 10ondr
 */
public class CommunicationLink extends BasicNode {
    private final DeploymentTarget target1;
    private final DeploymentTarget target2;
    
    // Annotations
    private final ObservableList<LinkType> linkType;
    private final ObservableList<LinkFailure> linkFailures;
    
    public CommunicationLink(DeploymentTarget target1, DeploymentTarget target2){
        this.target1 = target1;
        this.target2 = target2;

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
                new Callback<LinkFailure, Observable[]>() {
                    @Override
                    public Observable[] call(LinkFailure param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
    }
    
    public DeploymentTarget getFirst(){
        return target1;
    }
    
    public DeploymentTarget getSecond(){
        return target2;
    }
    
    public ObservableList getLinkType(){
        return linkType;
    }
    
    public void setLinkType(LinkType newLinkType){
        if(linkType.size() > 0)
            linkType.remove(0);
        linkType.add(newLinkType);
    }
    
    public ObservableList getLinkFailures(){
        return linkFailures;
    }
    
    public void addLinkFailure(LinkFailure newLinkFailure){
        linkFailures.add(newLinkFailure);
    }
    
    public void cleanup(){
        if(target1 != null)
            target1.removeConnection(this);
        if(target2 != null)
            target2.removeConnection(this);
    }
}
