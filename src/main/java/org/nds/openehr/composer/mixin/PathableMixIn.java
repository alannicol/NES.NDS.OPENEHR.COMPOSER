package org.nds.openehr.composer.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class PathableMixIn {
    @JsonIgnore
    abstract String getPath();
}
