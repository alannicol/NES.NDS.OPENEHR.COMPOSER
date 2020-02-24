package org.nds.openehr.composer.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMTypeInfo;

import java.io.IOException;

public class OpenEHRTypeNaming extends ClassNameIdResolver {
    private ModelInfoLookup rmInfoLookup = ArchieRMInfoLookup.getInstance();
    private ModelInfoLookup aomInfoLookup = ArchieAOMInfoLookup.getInstance();

    protected OpenEHRTypeNaming() {
        super(TypeFactory.defaultInstance().constructType(OpenEHRBase.class), TypeFactory.defaultInstance());
    }

    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    public String idFromValue(Object value) {
        RMTypeInfo typeInfo = this.rmInfoLookup.getTypeInfo(value.getClass());
        if (typeInfo == null) {
            typeInfo = this.aomInfoLookup.getTypeInfo(value.getClass());
        }

        return typeInfo != null ? typeInfo.getRmName() : this.rmInfoLookup.getNamingStrategy().getTypeName(value.getClass());
    }

    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        return this._typeFromId(id, context);
    }

    protected JavaType _typeFromId(String typeName, DatabindContext ctxt) throws IOException {
        Class result = this.rmInfoLookup.getClass(typeName);
        if (result == null) {
            result = this.aomInfoLookup.getClass(typeName);
        }

        if (result != null) {
            TypeFactory typeFactory = ctxt == null ? this._typeFactory : ctxt.getTypeFactory();
            return typeFactory.constructSpecializedType(this._baseType, result);
        } else {
            return super._typeFromId(typeName, ctxt);
        }
    }
}
