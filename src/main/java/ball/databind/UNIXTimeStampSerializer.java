/*
 * $Id$
 *
 * Copyright 2018 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Date;

/**
 * UNIX Time Stamp (seconds since epoch)
 * {@link com.fasterxml.jackson.databind.JsonSerializer}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class UNIXTimeStampSerializer extends StdSerializer<Long> {
    private static final long serialVersionUID = -4952546998424181027L;

    /**
     * Sole constructor.
     */
    public UNIXTimeStampSerializer() { super(Long.class); }

    @Override
    public void serialize(Long value,
                          JsonGenerator generator,
                          SerializerProvider provider) throws IOException,
                                                              JsonProcessingException {
        Date date = new Date(value.longValue() * 1000);

        generator.writeObject(date);
    }

    @Override
    public String toString() { return super.toString(); }
}
