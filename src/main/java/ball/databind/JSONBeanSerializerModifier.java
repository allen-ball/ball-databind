package ball.databind;
/*-
 * ##########################################################################
 * Data Binding Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2016 - 2021 Allen D. Ball
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
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
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

    @ToString
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
    }
}
