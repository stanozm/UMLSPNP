package cz.muni.fi.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.PetriNet;

/**
 *  A segment of the final Petri net which is further extended by more 
 * specific segment classes.
 *
 */
public abstract class Segment {
    protected final PetriNet petriNet;
    
    public Segment(PetriNet petriNet) {
        this.petriNet = petriNet;
    }
}
