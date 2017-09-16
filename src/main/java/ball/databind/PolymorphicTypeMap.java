/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import ball.io.IOUtil;
import ball.util.ClassOrder;
import ball.util.PropertiesImpl;
import java.io.InputStream;
import java.util.TreeSet;
import java.util.TreeMap;

import static ball.util.StringUtil.isNil;

/**
 * Class suitable for mapping polymorphic subtypes.  Subclasses can specify
 * the mapping in a {@link java.util.Properties} resource which will be
 * automatically loaded on instantiation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PolymorphicTypeMap extends TreeMap<Class<?>,Class<?>[]> {
    private static final long serialVersionUID = -3585659541275170267L;

    /**
     * Sole constructor.
     */
    public PolymorphicTypeMap() {
        super(ClassOrder.NAME);

        try {
            PropertiesImpl properties = new PropertiesImpl();
            String name = getClass().getSimpleName() + ".properties";
            InputStream in = getClass().getResourceAsStream(name);

            if (in != null) {
                try {
                    properties.load(in);
                } finally {
                    IOUtil.close(in);
                }
            }

            ClassLoader loader = getClass().getClassLoader();

            for (String key : properties.stringPropertyNames()) {
                TreeSet<Class<?>> value = new TreeSet<>(ClassOrder.NAME);

                for (String substring :
                         properties.getProperty(key)
                         .split("[,\\p{Space}]+")) {
                    substring = substring.trim();

                    if (! isNil(substring)) {
                        value.add(Class.forName(substring, true, loader));
                    }
                }

                put(Class.forName(key, true, loader),
                    value.toArray(new Class<?>[] { }));
            }
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public Class<?>[] put(Class<?> key, Class<?>[] value) {
        for (Class<?> subtype : value) {
            if (! key.isAssignableFrom(subtype)) {
                throw new IllegalArgumentException(subtype.getName()
                                                   + " is not a subclass of "
                                                   + key.getName());
            }
        }

        return super.put(key, value);
    }
}
