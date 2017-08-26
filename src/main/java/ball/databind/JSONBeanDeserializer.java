/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;

/**
 * {@link JSONBean} {@link JsonDeserializer} implementation.  Supports
 * polymorphism by differentiating subclass implementations' bean
 * properties.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class JSONBeanDeserializer extends JsonDeserializer<JSONBean> {
    private static final ObjectMapper OM = new ObjectMapper();

    private final Class<? extends JSONBean> supertype;
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
    @SafeVarargs
    public JSONBeanDeserializer(Class<? extends JSONBean> supertype,
                                Class<? extends JSONBean>... subtypes) {
        super();

        if (supertype != null) {
            this.supertype = supertype;
        } else {
            throw new NullPointerException("supertype");
        }

        try {
            for (Class<? extends JSONBean> subtype : subtypes) {
                if (supertype.isAssignableFrom(subtype)) {
                    list.add(Introspector.getBeanInfo(subtype,
                                                      JSONBean.class));
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
    public JSONBean deserialize(JsonParser parser,
                                DeserializationContext context) throws IOException {
        JsonNode node = parser.readValueAsTree();
        String string = JSONBean.OM.writeValueAsString(node);
        Class<? extends JSONBean> type = null;

        for (BeanInfo info : list) {
            if (hasAll(node, info.getPropertyDescriptors())) {
                type =
                    info.getBeanDescriptor()
                    .getBeanClass()
                    .asSubclass(JSONBean.class);
                break;
            }
        }

        if (type == null) {
            type = supertype;
        }

        JSONBean bean = JSONBeanDeserializer.OM.treeToValue(node, type);

        bean.node = node;

        return bean;
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
