module cz.muni.fi.umlspnp {
    requires javafx.controls;
    requires com.google.gson;
    requires fx.gson;
    requires spnp.core.models;
    requires spnp.core.transformators;

    exports cz.muni.fi.umlspnp;
    exports cz.muni.fi.umlspnp.common;
    exports cz.muni.fi.umlspnp.models;
    exports cz.muni.fi.umlspnp.models.deploymentdiagram;
    exports cz.muni.fi.umlspnp.models.sequencediagram;
    
    opens cz.muni.fi.umlspnp.common;
    opens cz.muni.fi.umlspnp.models;
    opens cz.muni.fi.umlspnp.models.deploymentdiagram;
    opens cz.muni.fi.umlspnp.models.sequencediagram;
    
    exports cz.muni.fi.umlspnp.views;
    exports cz.muni.fi.umlspnp.views.common;
    exports cz.muni.fi.umlspnp.views.common.layouts;
    exports cz.muni.fi.umlspnp.views.deploymentdiagram;
    exports cz.muni.fi.umlspnp.views.sequencediagram;
    
    opens cz.muni.fi.umlspnp.views;
    opens cz.muni.fi.umlspnp.views.common;
    opens cz.muni.fi.umlspnp.views.common.layouts;
    opens cz.muni.fi.umlspnp.views.deploymentdiagram;
    opens cz.muni.fi.umlspnp.views.sequencediagram;
}
