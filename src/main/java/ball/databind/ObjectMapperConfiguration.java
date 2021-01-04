package ball.databind;
/*-
 * ##########################################################################
 * Data Binding Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2016 - 2021 Allen D. Ball
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
