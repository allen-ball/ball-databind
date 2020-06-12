package ball.databind;
/*-
 * ##########################################################################
 * Data Binding Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2016 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Date;
import lombok.ToString;

/**
 * UNIX Time Stamp (seconds since epoch)
 * {@link com.fasterxml.jackson.databind.JsonDeserializer}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ToString
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
}

