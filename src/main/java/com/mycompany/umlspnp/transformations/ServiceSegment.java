/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.transformations;

import cz.muni.fi.spnp.core.models.places.StandardPlace;

/**
 *
 * @author 10ondr
 */
public interface ServiceSegment {
    public ServiceCall getServiceCall();
    public StandardPlace getEndPlace();
    public void transform();
}
