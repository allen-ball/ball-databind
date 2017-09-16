/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * {@link JSONBean} {@link JsonSerializer} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class JSONBeanSerializer extends JsonSerializer<JSONBean> {

    /**
     * Sole constructor.
     */
    public JSONBeanSerializer() { super(); }

    @Override
    public void serialize(JSONBean value, JsonGenerator generator,
                          SerializerProvider serializers) throws IOException {
        generator.writeTree(value.asJsonNode());
    }

    @Override
    public String toString() { return super.toString(); }
}
