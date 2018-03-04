/*
 * $Id$
 *
 * Copyright 2017, 2018 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Serializable;

/**
 * Abstract base class for bean implementations that wrap a
 * {@link JsonNode}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class JSONBean implements Serializable {
    private static final long serialVersionUID = 7361248610078338176L;

    /**
     * Unconfigured {@link ObjectMapper} available for implementing subclass
     * methods.
     */
    protected static final ObjectMapper OM =
        new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(MapperFeature.USE_ANNOTATIONS, false)
        .configure(SerializationFeature.INDENT_OUTPUT, true);

    protected JsonNode node = null;

    /**
     * Sole constructor.
     */
    protected JSONBean() { }

    @Override
    public String toString() {
        String string = null;

        if (node != null) {
            try {
                string = OM.writeValueAsString(node);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }

        return (string != null) ? string : super.toString();
    }
}
