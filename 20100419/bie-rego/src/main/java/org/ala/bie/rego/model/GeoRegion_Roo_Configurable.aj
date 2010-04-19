package org.ala.bie.rego.model;

import org.springframework.beans.factory.annotation.Configurable;

privileged aspect GeoRegion_Roo_Configurable {
    
    declare @type: GeoRegion: @Configurable;    
    
}
