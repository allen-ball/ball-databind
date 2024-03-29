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
import ball.util.PropertiesImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.AbstractDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import static java.beans.Introspector.getBeanInfo;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * Class suitable for mapping polymorphic subtypes.  Subclasses can specify
 * the mapping in a {@link java.util.Properties} resource which will be
 * automatically loaded on instantiation.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public abstract class PolymorphicTypeMap extends TreeMap<Class<?>,Class<?>[]> {
    private static final long serialVersionUID = -4465979259676184876L;

    /**
     * Sole constructor.
     */
    protected PolymorphicTypeMap() {
        super(comparing(Class::getName));

        try {
            PropertiesImpl properties = new PropertiesImpl();
            String name = getClass().getSimpleName() + ".properties";
            InputStream in = getClass().getResourceAsStream(name);

            if (in != null) {
                try {
                    properties.load(in);
                } finally {
                    in.close();
                }
            }

            ClassLoader loader = getClass().getClassLoader();
            Package pkg = getClass().getPackage();

            for (String key : properties.stringPropertyNames()) {
                TreeSet<Class<?>> value = new TreeSet<>(comparing(Class::getName));

                for (String substring : properties.getProperty(key).split("[,\\p{Space}]+")) {
                    substring = substring.trim();

                    if (! StringUtils.isEmpty(substring)) {
                        value.add(getClassFor(loader, pkg, substring));
                    }
                }

                put(getClassFor(loader, pkg, key), value.toArray(new Class<?>[] { }));
            }
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private Class<?> getClassFor(ClassLoader loader, Package pkg, String name) throws Exception {
        Class<?> cls = null;

        try {
            cls = Class.forName(pkg.getName() + "." + name, true, loader);
        } catch (Exception exception) {
            cls = Class.forName(name, true, loader);
        }

        return cls;
    }

    /**
     * Method to get a {@link BeanDeserializerModifier} for this
     * {@link PolymorphicTypeMap}.
     *
     * @return  The {@link BeanDeserializerModifier}.
     */
    public BeanDeserializerModifier getBeanDeserializerModifier() {
        return new DeserializerModifier();
    }

    /**
     * Callback from {@link JsonDeserializer} implementation to allow
     * subclass implementations to initialize the resulting bean with the
     * parse-tree ({@link JsonNode}).
     *
     * @param   object          The {@link Object} to initialize.
     * @param   codec           The {@link ObjectCodec}.
     * @param   node            The {@link JsonNode}.
     *
     * @throws  IOException     If there is a problem initializing the
     *                          {@link JSONBean}.
     */
    protected void initialize(Object object, ObjectCodec codec, JsonNode node) throws IOException {
    }

    @Override
    public Class<?>[] put(Class<?> key, Class<?>[] value) {
        for (Class<?> subtype : value) {
            if (! key.isAssignableFrom(subtype)) {
                throw new IllegalArgumentException(subtype.getName() + " is not a subclass of " + key.getName());
            }
        }

        return super.put(key, value);
    }

    @ToString
    private class DeserializerModifier extends BeanDeserializerModifier {
        public DeserializerModifier() { super(); }

        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription description, JsonDeserializer<?> deserializer) {
            Class<?> key = description.getBeanClass();

            if (containsKey(key)) {
                deserializer =
                    (deserializer instanceof BeanDeserializer)
                        ? new BeanDeserializerImpl((BeanDeserializer) deserializer, key, get(key))
                        : new AbstractDeserializerImpl(description, key, get(key));
            }

            return deserializer;
        }

        private class BeanInfoList extends ArrayList<BeanInfo> {
            private static final long serialVersionUID = 1008165295877024242L;

            private final Class<?> supertype;

            public BeanInfoList(Class<?> supertype, Class<?>... subtypes) {
                super(subtypes.length);

                this.supertype = requireNonNull(supertype, "supertype");

                try {
                    for (Class<?> subtype : subtypes) {
                        if (supertype.isAssignableFrom(subtype)) {
                            add(getBeanInfo(subtype, supertype));
                        } else {
                            throw new IllegalArgumentException(subtype.getName()
                                                               + " is not a subclass of " + supertype.getName());
                        }
                    }
                } catch (Exception exception) {
                    throw new ExceptionInInitializerError(exception);
                }
            }

            public Class<?> supertype() { return supertype; }

            public Class<?> subtypeFor(JsonNode node) {
                Class<?> subtype = null;

                for (BeanInfo info : this) {
                    if (hasAll(node, info.getPropertyDescriptors())) {
                        subtype = info.getBeanDescriptor().getBeanClass().asSubclass(supertype);
                        break;
                    }
                }

                return subtype;
            }

            private boolean hasAll(JsonNode node, PropertyDescriptor... properties) {
                boolean hasAll = true;

                for (PropertyDescriptor property : properties) {
                    hasAll &= node.has(property.getName());

                    if (! hasAll) {
                        break;
                    }
                }

                return hasAll;
            }
        }

        @ToString
        private class JsonParserImpl extends TreeTraversingParser {
            protected JsonNode node = null;

            public JsonParserImpl(JsonParser parser) throws IOException {
                this((JsonNode) parser.readValueAsTree(), parser.getCodec());
            }

            private JsonParserImpl(JsonNode node, ObjectCodec codec) {
                super(node, codec);

                this.node = requireNonNull(node, "node");
            }
        }

        @ToString
        private class BeanDeserializerImpl extends BeanDeserializer {
            private static final long serialVersionUID = 7600217898656123257L;

            private final BeanInfoList list;

            public BeanDeserializerImpl(BeanDeserializer deserializer, Class<?> supertype, Class<?>... subtypes) {
                super(deserializer);

                list = new BeanInfoList(supertype, subtypes);
            }

            @Override
            public Class<?> handledType() { return list.supertype(); }

            @Override
            public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                Object object = null;
                ObjectCodec codec = parser.getCodec();
                JsonNode node = null;

                if (parser instanceof JsonParserImpl) {
                    node = ((JsonParserImpl) parser).node;

                    if (node != null) {
                        ((JsonParserImpl) parser).node = null;
                    }
                }

                if (node != null) {
                    Class<?> subtype = list.subtypeFor(node);

                    if (subtype != null) {
                        object = codec.readValue(parser, subtype);
                    } else {
                        object = super.deserialize(parser, context);
                    }

                    initialize(list.supertype().cast(object), codec, node);
                } else {
                    object = codec.readValue(new JsonParserImpl(parser), list.supertype());
                }

                return object;
            }
        }

        @ToString
        private class AbstractDeserializerImpl extends AbstractDeserializer {
            private static final long serialVersionUID = -7887072343275693448L;

            private final BeanInfoList list;

            public AbstractDeserializerImpl(BeanDescription description, Class<?> supertype, Class<?>... subtypes) {
                super(description);

                list = new BeanInfoList(supertype, subtypes);
            }

            @Override
            public Class<?> handledType() { return list.supertype(); }

            @Override
            public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                Object object = null;
                ObjectCodec codec = parser.getCodec();
                JsonNode node = null;

                if (parser instanceof JsonParserImpl) {
                    node = ((JsonParserImpl) parser).node;

                    if (node != null) {
                        ((JsonParserImpl) parser).node = null;
                    }
                }

                if (node != null) {
                    Class<?> subtype = list.subtypeFor(node);

                    if (subtype != null) {
                        object = codec.readValue(parser, subtype);
                    } else {
                        object = super.deserialize(parser, context);
                    }

                    initialize(list.supertype().cast(object), codec, node);
                } else {
                    object = codec.readValue(new JsonParserImpl(parser), list.supertype());
                }

                return object;
            }
        }
    }
}
