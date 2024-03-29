package ball.databind;
/*-
 * ##########################################################################
 * Data Binding Utilities
 * %%
 * Copyright (C) 2016 - 2022 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import ball.util.EnumLookupMap;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedMap;

import static java.util.Collections.unmodifiableSortedMap;

/**
 * Abstract class with static {@link SortedMap} ({@link EnumLookupMap})
 * member of all {@link ObjectMapper} features.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public abstract class ObjectMapperFeature {

    /**
     * The {@link SortedMap} of feature names to their corresponding
     * {@link Enum}.
     */
    public static final SortedMap<String,Enum<?>> MAP =
        unmodifiableSortedMap(new EnumLookupMap(DeserializationFeature.class,
                                                JsonGenerator.Feature.class,
                                                JsonParser.Feature.class,
                                                MapperFeature.class,
                                                SerializationFeature.class));

    /**
     * Static method to configure an {@link JsonMapper.Builder} feature.
     *
     * @param   builder         The {@link JsonMapper.Builder} to configure.
     * @param   feature         The feature {@link Enum}.
     * @param   value           The {@link Boolean} value.
     *
     * @throws  IllegalArgumentException
     *                          If the feature {@link Enum} is not
     *                          recognized.
     */
    public static void configure(JsonMapper.Builder builder, Enum<?> feature, boolean value) {
        if (feature instanceof MapperFeature) {
            builder.configure((MapperFeature) feature, value);
        } else if (feature instanceof JsonGenerator.Feature) {
            builder.configure((JsonGenerator.Feature) feature, value);
        } else if (feature instanceof JsonParser.Feature) {
            builder.configure((JsonParser.Feature) feature, value);
        } else if (feature instanceof DeserializationFeature) {
            builder.configure((DeserializationFeature) feature, value);
        } else if (feature instanceof SerializationFeature) {
            builder.configure((SerializationFeature) feature, value);
        } else {
            throw new IllegalArgumentException("Unrecognized feature `"
                                               + feature.getClass().getName() + "." + feature.name() + "'");
        }
    }

    /**
     * Static method to configure an {@link ObjectMapper} feature.
     *
     * @param   mapper          The {@link ObjectMapper} to configure.
     * @param   feature         The feature {@link Enum}.
     * @param   value           The {@link Boolean} value.
     *
     * @throws  IllegalArgumentException
     *                          If the feature {@link Enum} is not
     *                          recognized.
     */
    public static void configure(ObjectMapper mapper, Enum<?> feature, boolean value) {
        if (feature instanceof MapperFeature) {
            /*
             * mapper.configure((MapperFeature) feature, value);
             *
             * is deprecated.  Going forward, the JsonMapper.Builder shoud be configured with this MapperFeature but
             * attempt the call via reflection for backward compatibility.
             */
            try {
                mapper.getClass()
                    .getMethod("configure", MapperFeature.class, Boolean.TYPE)
                    .invoke(mapper, feature, value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            }
        } else if (feature instanceof JsonGenerator.Feature) {
            mapper.configure((JsonGenerator.Feature) feature, value);
        } else if (feature instanceof JsonParser.Feature) {
            mapper.configure((JsonParser.Feature) feature, value);
        } else if (feature instanceof DeserializationFeature) {
            mapper.configure((DeserializationFeature) feature, value);
        } else if (feature instanceof SerializationFeature) {
            mapper.configure((SerializationFeature) feature, value);
        } else {
            throw new IllegalArgumentException("Unrecognized feature `"
                                               + feature.getClass().getName() + "." + feature.name() + "'");
        }
    }

    private ObjectMapperFeature() { }
}
