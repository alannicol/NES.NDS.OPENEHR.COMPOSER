package org.nds.openehr.composer.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import org.nds.openehr.composer.mixin.PathableMixIn;
import org.nds.openehr.composer.mixin.UIDBasedIdMixIn;

import java.io.IOException;

public class JSONUtil {
    private static volatile ObjectMapper objectMapper;

    public JSONUtil() {
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            configureObjectMapper(objectMapper);
        }

        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(new MapperFeature[]{MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL});

        objectMapper.addMixIn(Pathable.class, PathableMixIn.class);
        objectMapper.addMixIn(UIDBasedId.class, UIDBasedIdMixIn.class);

        TypeResolverBuilder typeResolverBuilder = (new JSONUtil.ArchieTypeResolverBuilder()).init(JsonTypeInfo.Id.NAME, new OpenEHRTypeNaming()).typeProperty("_type").typeIdVisibility(true).inclusion(JsonTypeInfo.As.PROPERTY);
        objectMapper.addHandler(new DeserializationProblemHandler() {
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                return propertyName.equalsIgnoreCase("_type") ? true : super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });
        objectMapper.setDefaultTyping(typeResolverBuilder);
    }

    static class ArchieTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {
        public ArchieTypeResolverBuilder() {
            super(ObjectMapper.DefaultTyping.NON_FINAL);
        }

        public boolean useForType(JavaType t) {
            return OpenEHRBase.class.isAssignableFrom(t.getRawClass());
        }
    }
}
