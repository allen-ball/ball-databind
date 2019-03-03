/*
 * $Id$
 *
 * Copyright 2017 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.io.IOException;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.Objects.requireNonNull;

/**
 * {@link JSONBean} {@link BeanSerializerModifier} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class JSONBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config,
                                              BeanDescription description,
                                              JsonSerializer<?> serializer) {
        return ((JSONBean.class.isAssignableFrom(description.getBeanClass()))
                    ? new SerializerImpl(serializer)
                    : serializer);
    }

    private class SerializerImpl extends JsonSerializer<Object> {
        private final JsonSerializer<Object> serializer;

        @SuppressWarnings("unchecked")
        public SerializerImpl(JsonSerializer<?> serializer) {
            super();

            this.serializer =
                (JsonSerializer<Object>) requireNonNull(serializer, "serializer");
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
