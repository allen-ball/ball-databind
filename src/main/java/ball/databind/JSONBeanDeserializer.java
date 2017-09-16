/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import static java.beans.Introspector.getBeanInfo;

/**
 * {@link JSONBean} {@link JsonDeserializer} implementation.  Supports
 * polymorphism by differentiating subclass implementations' bean
 * properties.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class JSONBeanDeserializer<T extends JSONBean>
             extends JsonDeserializer<T> {

    /**
     * {@link ObjectMapper}
     */
    protected static final ObjectMapper OM =
        new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.INDENT_OUTPUT, true);

    private final Class<? extends T> supertype;
    private final ArrayList<BeanInfo> list = new ArrayList<>();

    /**
     * Sole constructor.
     *
     * @param   supertype       The {@link Class} served by this
     *                          {@link JsonDeserializer}.
     * @param   subtypes        The polymorphic subtype {@link Class}es
     *                          (optional).  Must be subclasses of
     *                          {@code supertype}.
     */
    public JSONBeanDeserializer(Class<? extends T> supertype,
                                Class<?>... subtypes) {
        super();

        if (supertype != null) {
            this.supertype = supertype;
        } else {
            throw new NullPointerException("supertype");
        }

        try {
            for (Class<?> subtype : subtypes) {
                if (supertype.isAssignableFrom(subtype)) {
                    list.add(getBeanInfo(subtype, JSONBean.class));
                } else {
                    throw new IllegalArgumentException(subtype.getName()
                                                       + " is not a subclass of "
                                                       + supertype.getName());
                }
            }
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public Class<? extends T> handledType() { return supertype; }

    @Override
    public T deserialize(JsonParser parser,
                         DeserializationContext context) throws IOException {
        ObjectMapper om = (ObjectMapper) parser.getCodec();
        JsonNode node = om.readTree(parser);
        Class<? extends JSONBean> type = null;

        for (BeanInfo info : list) {
            if (hasAll(node, info.getPropertyDescriptors())) {
                type =
                    info.getBeanDescriptor()
                    .getBeanClass()
                    .asSubclass(supertype);
                break;
            }
        }

        if (type == null) {
            type = supertype;
        }

        JSONBean bean = OM.treeToValue(node, type);

        bean.node = node;
        initialize(bean, bean.node, OM.writeValueAsString(node));

        return supertype.cast(bean);
    }

    /**
     * Callback to allow subclass implementations to initialize the
     * resulting {@link JSONBean} with the parse-tree ({@link JsonNode}).
     *
     * @param   bean            The {@link JSONBean} to initialize.
     * @param   node            The {@link JsonNode}.
     * @param   string          The JSON {@link String}.
     *
     * @throws  IOException     If there is a problem initializing the
     *                          {@link JSONBean}.
     */
    protected void initialize(JSONBean bean,
                              JsonNode node,
                              String string) throws IOException {
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

    @Override
    public String toString() { return super.toString(); }
}
