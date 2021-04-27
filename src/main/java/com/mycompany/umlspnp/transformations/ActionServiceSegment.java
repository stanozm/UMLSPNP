package com.mycompany.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.places.StandardPlace;
import java.util.Collection;

/**
 *  Interface that represents the execution part of the net - either 
 * the execution (leaf) service segment or the communication segment.
 *
 */
public interface ActionServiceSegment {
    public StandardPlace getEndPlace();
    public Collection<StandardPlace> getFailPlaces();
    public void setFlushTransitionGuardDependentPlace(StandardPlace dependentPlace);
    public void transform();
}
