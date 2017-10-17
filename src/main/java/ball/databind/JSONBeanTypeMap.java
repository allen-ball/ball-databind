/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 * {@link JSONBean} {@link PolymorphicTypeMap}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class JSONBeanTypeMap extends PolymorphicTypeMap {
    private static final long serialVersionUID = 8775770218915991160L;

    /**
     * Sole constructor.
     */
    protected JSONBeanTypeMap() { super(); }

    @Override
    protected void initialize(Object object,
                              ObjectCodec codec,
                              JsonNode node) throws IOException {
        super.initialize(object, codec, node);
        ((JSONBean) object).node = node;
    }
}
