/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

/**
 *
 * @author 10ondr
 */
public class StateEffect {
    private final String name;
    
    public StateEffect(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}
