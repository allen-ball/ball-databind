package ball.databind.entity;
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
import ball.databind.JSONBean;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Abstract base class for {@link JSONBean} entities.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@MappedSuperclass
@NoArgsConstructor @EqualsAndHashCode(callSuper = false)
public class JSONEntity extends JSONBean {
    private static final long serialVersionUID = -688254612378583290L;

    /** @serial */
    @Column @Lob
    @Getter
    protected String json = null;

    protected void setJson(String string) {
        synchronized (this) {
            boolean modified = (node == null || (! string.equals(json)));

            json = string;

            if (modified) {
                node = null;

                if (json != null) {
                    try {
                        node = mapper.readTree(json);
                    } catch (IOException exception) {
                        throw new IllegalArgumentException(exception);
                    }
                }
            }
        }
    }

    /**
     * Method to get this {@link JSONEntity} as a {@link JsonNode}.
     *
     * @return  The {@link JSONEntity} as a {@link JsonNode}.
     */
    public JsonNode asJsonNode() {
        JsonNode node = this.node;

        return (node != null) ? node.deepCopy() : null;
    }

    @Override
    public String toString() {
        String string = getJson();

        return (string != null) ? string : super.toString();
    }
}
