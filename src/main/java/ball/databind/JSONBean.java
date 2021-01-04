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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Serializable;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract base class for bean implementations that wrap a
 * {@link JsonNode}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class JSONBean implements Serializable {
    private static final long serialVersionUID = -7720273815805776898L;

    /** @serial */ protected ObjectMapper mapper = ObjectMapperConfiguration.MAPPER;
    /** @serial */ protected JsonNode node = null;

    /**
     * Convenience method to call {@link JsonNode#at(String)}.
     *
     * @param   expression      The {@link String} respresentation of the
     *                          {@link com.fasterxml.jackson.core.JsonPointer}.
     *
     * @return  {@link JsonNode} that matches given
     *          {@link com.fasterxml.jackson.core.JsonPointer}: if no match
     *          exists, will return a {@link JsonNode} for which
     *          {@link com.fasterxml.jackson.core.TreeNode#isMissingNode()}
     *          returns {@code true}.
     */
    protected JsonNode nodeAt(String expression) {
        return (expression != null) ? node.at(expression) : null;
    }

    /**
     * Convenience method to get text for a node.
     * See {@link #nodeAt(String)}.
     *
     * @param   expression      See {@link #nodeAt(String)}.
     *
     * @return  {@link JsonNode#asText()} of {@link #nodeAt(String)}.
     */
    protected String textAt(String expression) {
        JsonNode node = nodeAt(expression);

        return (node != null && (! node.isMissingNode())) ? node.asText() : null;
    }

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
