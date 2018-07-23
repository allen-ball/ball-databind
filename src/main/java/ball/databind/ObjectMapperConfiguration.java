/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default {@link JSONBean} {@link ObjectMapper}
 * {@link ObjectMapperConfiguration}.
 *
 * {@include ObjectMapperConfiguration.properties}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ObjectMapperConfiguration
             extends AbstractObjectMapperConfiguration {
    private static final long serialVersionUID = 5510812052030491222L;

    /**
     * {@include #INSTANCE}
     */
    public static final ObjectMapperConfiguration INSTANCE =
        new ObjectMapperConfiguration();

    /**
     * An {@link ObjectMapper} configured with {@link #INSTANCE}.
     */
    public static final ObjectMapper MAPPER = INSTANCE.newObjectMapper();

    /**
     * Sole constructor.
     */
    public ObjectMapperConfiguration() { super(); }
}
