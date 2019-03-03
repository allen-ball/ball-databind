/*
 * $Id$
 *
 * Copyright 2018, 2019 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

/**
 * Default {@link JSONBean} {@link ObjectMapper}
 * {@link ObjectMapperConfiguration}.
 *
 * {@include #INSTANCE}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor
public class ObjectMapperConfiguration
             extends AbstractObjectMapperConfiguration {
    private static final long serialVersionUID = 5510812052030491222L;

    /**
     * {@link ObjectMapperConfiguration} {@link #INSTANCE}
     */
    public static final ObjectMapperConfiguration INSTANCE =
        new ObjectMapperConfiguration();

    /**
     * An {@link ObjectMapper} configured with {@link #INSTANCE}.
     */
    public static final ObjectMapper MAPPER = INSTANCE.newObjectMapper();
}
