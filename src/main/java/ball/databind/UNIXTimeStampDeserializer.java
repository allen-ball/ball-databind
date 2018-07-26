/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Date;

/**
 * UNIX Time Stamp (seconds since epoch)
 * {@link com.fasterxml.jackson.databind.JsonDeserializer}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class UNIXTimeStampDeserializer extends StdDeserializer<Long> {
    private static final long serialVersionUID = 4297437977249133382L;

    /**
     * Sole constructor.
     */
    public UNIXTimeStampDeserializer() { super(Long.class); }

    @Override
    public Long deserialize(JsonParser parser,
                            DeserializationContext context) throws IOException,
                                                                   JsonProcessingException {
        Date date = parser.getCodec().readValue(parser, Date.class);

        return (date != null) ? (date.getTime() / 1000) : null;
    }

    @Override
    public String toString() { return super.toString(); }
}

