/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.common;

/**
 *
 * @author 10ondr
 */
public class Connection<T> extends BasicNode {
    protected final T target1;
    protected final T target2;
    
    public Connection(T target1, T target2){
        this.target1 = target1;
        this.target2 = target2;
    }
    
    public T getFirst(){
        return target1;
    }
    
    public T getSecond(){
        return target2;
    }
    
    public T getOther(T first){
        if(target1.equals(first))
            return target2;
        return target1;
    }
}
