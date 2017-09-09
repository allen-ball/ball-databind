/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Abstract base class for bean implementations that wrap a
 * {@link JsonNode}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class JSONBean {

    /**
     * Unconfigured {@link ObjectMapper} available for implementing subclass
     * methods.
     */
    protected static final ObjectMapper OM =
        new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(MapperFeature.USE_ANNOTATIONS, false)
        .configure(SerializationFeature.INDENT_OUTPUT, true);

    protected JsonNode node = null;

    /**
     * Sole constructor.
     */
    protected JSONBean() { }

    /**
     * Method to get this {@link JSONBean} as a {@link JsonNode}.
     *
     * @return  The {@link JSONBean} as a {@link JsonNode}.
     */
    public JsonNode asJsonNode() { return node; }

    @Override
    public String toString() {
        String string = null;

        if (node != null) {
            try {
                string = OM.writeValueAsString(node);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }

        return (string != null) ? string : super.toString();
    }

    /**
     * {@link JSONBean} {@link JsonSerializer} implementation.
     */
    public static class Serializer extends JsonSerializer<JSONBean> {

        /**
         * Sole constructor.
         */
        public Serializer() { super(); }

        @Override
        public void serialize(JSONBean value, JsonGenerator generator,
                              SerializerProvider serializers) throws IOException {
            generator.writeTree(value.asJsonNode());
        }

        @Override
        public String toString() { return super.toString(); }
    }
}
