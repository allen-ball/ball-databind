/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.io.IOException;

/**
 * {@link JSONBean} {@link BeanSerializerModifier} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class JSONBeanSerializerModifier extends BeanSerializerModifier {

    /**
     * Sole constructor.
     */
    public JSONBeanSerializerModifier() { super(); }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config,
                                              BeanDescription description,
                                              JsonSerializer<?> serializer) {
        return ((JSONBean.class.isAssignableFrom(description.getBeanClass()))
                    ? new SerializerImpl(serializer)
                    : serializer);
    }

    @Override
    public String toString() { return super.toString(); }

    private class SerializerImpl extends JsonSerializer<Object> {
        private final JsonSerializer<Object> serializer;

        @SuppressWarnings("unchecked")
        public SerializerImpl(JsonSerializer<?> serializer) {
            super();

            if (serializer != null) {
                this.serializer = (JsonSerializer<Object>) serializer;
            } else {
                throw new NullPointerException("serializer");
            }
        }

        @Override
        public void serialize(Object value, JsonGenerator generator,
                              SerializerProvider serializers) throws IOException {
            if (value instanceof JSONBean && ((JSONBean) value).node != null) {
                generator.writeTree(((JSONBean) value).node);
            } else {
                serializer.serialize(value, generator, serializers);
            }
        }

        @Override
        public String toString() { return super.toString(); }
    }
}
