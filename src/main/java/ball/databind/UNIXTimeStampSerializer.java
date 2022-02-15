package ball.databind;
/*-
 * ##########################################################################
 * Data Binding Utilities
 * %%
 * Copyright (C) 2016 - 2022 Allen D. Ball
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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Date;
import lombok.ToString;

/**
 * UNIX Time Stamp (seconds since epoch)
 * {@link com.fasterxml.jackson.databind.JsonSerializer}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ToString
public class UNIXTimeStampSerializer extends StdSerializer<Long> {
    private static final long serialVersionUID = -4952546998424181027L;

    /**
     * Sole constructor.
     */
    public UNIXTimeStampSerializer() { super(Long.class); }

    @Override
    public void serialize(Long value, JsonGenerator generator, SerializerProvider provider) throws IOException,
                                                                                                   JsonProcessingException {
        Date date = new Date(value.longValue() * 1000);

        generator.writeObject(date);
    }
}
