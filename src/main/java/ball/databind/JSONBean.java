/*
 * $Id$
 *
 * Copyright 2017 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
