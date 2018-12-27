/*
 * $Id$
 *
 * Copyright 2017, 2018 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import ball.util.EnumLookupMap;
import ball.util.PropertiesImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.SortedMap;

import static java.util.Collections.unmodifiableSortedMap;

/**
 * Abstract base class for {@link ObjectMapper} configurations.  The typical
 * implementation is a trivial subclass combined with a corresponding
 * {@link java.util.Properties} file.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractObjectMapperConfiguration
                      extends PropertiesImpl {
    private static final long serialVersionUID = 3380012687021883148L;

    /**
     * Features supported by this configuration.
     */
    public static enum Feature {
        /**
         * If set and {@code true}, calls
         * {@link ObjectMapper#registerModules(Iterable)} with the return
         * value of {@link ObjectMapper#findModules(ClassLoader)}.
         */
        REGISTER_MODULES;

        private static final SortedMap<String,Enum<?>> MAP =
            unmodifiableSortedMap(new EnumLookupMap(Feature.class));
    }

    /**
     * Sole constructor.
     */
    protected AbstractObjectMapperConfiguration() {
        super();

        String name = getClass().getSimpleName() + ".properties";

        try (InputStream in = getClass().getResourceAsStream(name)) {
            if (in != null) {
                load(in);
            }
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Method to configure an {@link ObjectMapper}.
     *
     * @param   mapper          The {@link ObjectMapper} to configure.
     *
     * @return  The {@link ObjectMapper}.
     */
    public ObjectMapper configure(ObjectMapper mapper) {
        for (String name : stringPropertyNames()) {
            if (ObjectMapperFeature.MAP.containsKey(name)) {
                ObjectMapperFeature
                    .configure(mapper,
                               ObjectMapperFeature.MAP.get(name),
                               Boolean.valueOf(getProperty(name)));
            }
        }

        for (String name : stringPropertyNames()) {
            if (Feature.MAP.containsKey(name)) {
                switch ((Feature) Feature.MAP.get(name)) {
                case REGISTER_MODULES:
                    ClassLoader loader = getClass().getClassLoader();

                    mapper.registerModules(ObjectMapper.findModules(loader));
                    break;

                default:
                    break;
                }
            }
        }

        return mapper;
    }

    /**
     * Method to return a new, configured {@link ObjectMapper}.
     *
     * @return  The {@link ObjectMapper}.
     */
    public ObjectMapper newObjectMapper() {
        return configure(new ObjectMapper());
    }
}
