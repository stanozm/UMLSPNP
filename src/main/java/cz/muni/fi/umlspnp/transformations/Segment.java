package cz.muni.fi.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.PetriNet;

/**
 *  A segment of the final Petri net which is further extended by more 
 * specific segment classes.
 *
 */
public abstract class Segment {
    protected final PetriNet petriNet;
    protected final boolean generateComments;
    
    public Segment(PetriNet petriNet, boolean generateComments) {
        this.petriNet = petriNet;
        this.generateComments = generateComments;
    }
}
