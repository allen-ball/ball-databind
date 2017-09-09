/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Abstract class with static {@link Set} and {@link SortedMap} members of
 * all {@link ObjectMapper} features.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ObjectMapperFeature {

    /**
     * The {@link Set} of {@link ObjectMapper} features:
     * {@link MapperFeature}s,
     * {@link com.fasterxml.jackson.core.JsonGenerator.Feature}s,
     * {@link com.fasterxml.jackson.core.JsonParser.Feature}s,
     * {@link DeserializationFeature}s, and {@link SerializationFeature}s.
     */
    public static final Set<Enum<?>> SET;

    /**
     * The {@link SortedMap} of feature names to their corresponding
     * {@link Enum} (see {@link #SET}).
     */
    public static final SortedMap<String,Enum<?>> MAP;

    static {
        LinkedHashSet<Enum<?>> set = new LinkedHashSet<>();

        for (EnumSet<? extends Enum<?>> enumSet :
                 Arrays.asList(EnumSet.allOf(DeserializationFeature.class),
                               EnumSet.allOf(JsonGenerator.Feature.class),
                               EnumSet.allOf(JsonParser.Feature.class),
                               EnumSet.allOf(MapperFeature.class),
                               EnumSet.allOf(SerializationFeature.class))) {
            set.addAll(enumSet);
        }

        SET = Collections.unmodifiableSet(set);

        TreeMap<String,Enum<?>> map =
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (Enum<?> feature : SET) {
            map.put(feature.name(), feature);
        }

        MAP = Collections.unmodifiableSortedMap(map);
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
    public static void configure(ObjectMapper mapper,
                                 Enum<?> feature, boolean value) {
        if (feature instanceof MapperFeature) {
            mapper.configure((MapperFeature) feature, value);
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
                                               + feature.getClass().getName()
                                               + "." + feature.name() + "'");
        }
    }

    private ObjectMapperFeature() { }
}
