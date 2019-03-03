/*
 * $Id$
 *
 * Copyright 2017 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

/**
 * {@link JSONBean} {@link PolymorphicTypeMap}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class JSONBeanTypeMap extends PolymorphicTypeMap {
    private static final long serialVersionUID = 8775770218915991160L;

    @Override
    protected void initialize(Object object,
                              ObjectCodec codec,
                              JsonNode node) throws IOException {
        super.initialize(object, codec, node);

        if (object instanceof JSONBean) {
            JSONBean bean = (JSONBean) object;

            bean.mapper = (ObjectMapper) codec;
            bean.node = node.deepCopy();
        }
    }
}
