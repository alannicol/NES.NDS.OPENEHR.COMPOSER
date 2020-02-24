package org.nds.openehr.composer.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class UIDBasedIdMixIn {
    @JsonIgnore
    abstract String getRoot();

    @JsonIgnore
    abstract String getExtension();
}
