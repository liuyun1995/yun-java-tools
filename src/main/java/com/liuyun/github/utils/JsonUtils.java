package com.liuyun.github.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T fromJson(String content, Class<T> valueType) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String content, TypeReference<?> valueTypeRef) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        try {
            return (T) objectMapper.readValue(content, valueTypeRef);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
