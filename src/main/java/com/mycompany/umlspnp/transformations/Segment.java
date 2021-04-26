/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.PetriNet;

/**
 *
 * @author 10ondr
 */
public class Segment {
    protected final PetriNet petriNet;
    protected final int transitionPriority;
    
    public Segment(PetriNet petriNet, int transitionPriority) {
        this.petriNet = petriNet;
        this.transitionPriority = transitionPriority;
    }
}
