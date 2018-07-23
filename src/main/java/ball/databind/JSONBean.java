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
    private static final long serialVersionUID = 4388214881251701753L;

    protected ObjectMapper mapper = ObjectMapperConfiguration.MAPPER;
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
                string = mapper.writeValueAsString(node);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }

        return (string != null) ? string : super.toString();
    }
}
