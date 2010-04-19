package org.ala.bie.rego.model;

import org.springframework.beans.factory.annotation.Configurable;

privileged aspect Contact_Roo_Configurable {
    
    declare @type: Contact: @Configurable;    
    
}
