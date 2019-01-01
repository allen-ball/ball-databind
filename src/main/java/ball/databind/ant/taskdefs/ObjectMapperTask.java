/*
 * $Id$
 *
 * Copyright 2016 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.databind.ant.taskdefs;

import ball.databind.ObjectMapperFeature;
import ball.util.ant.taskdefs.AbstractClasspathTask;
import ball.util.ant.taskdefs.AntTask;
import ball.util.ant.taskdefs.ConfigurableAntTask;
import ball.util.ant.taskdefs.NotNull;
import ball.util.ant.types.StringAttributeType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;

import static ball.databind.ObjectMapperFeature.MAP;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant} base
 * {@link org.apache.tools.ant.Task} for {@link ObjectMapper} tasks.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ObjectMapperTask extends AbstractClasspathTask
                                       implements ConfigurableAntTask {
    private boolean registerModules = false;
    private final ArrayList<Setting> settings = new ArrayList<>();
    protected final ObjectMapper mapper;

    /**
     * No-argument constructor.
     */
    protected ObjectMapperTask() { this(new ObjectMapper()); }

    /**
     * Protected constructor to allow subclasses to specify the
     * {@link ObjectMapper}.
     *
     * @param   mapper          The {@link ObjectMapper}.
     */
    protected ObjectMapperTask(ObjectMapper mapper) {
        super();

        this.mapper = requireNonNull(mapper, "mapper");
    }

    public boolean getRegisterModules() { return registerModules; }
    public void setRegisterModules(boolean isSet) { registerModules = isSet; }

    public void addConfiguredConfigure(Setting setting) {
        settings.add(setting);
    }

    @Override
    public void init() throws BuildException {
        super.init();

        try {
            if (getRegisterModules()) {
                mapper.registerModules(ObjectMapper.findModules(getClassLoader()));
            }

            for (Map.Entry<String,Object> entry :
                     getProject().getProperties().entrySet()) {
                if (MAP.containsKey(entry.getKey())) {
                    ObjectMapperFeature.configure(mapper,
                                                  MAP.get(entry.getKey()),
                                                  PropertyHelper.toBoolean(entry.getValue()));
                }
            }

            for (Setting setting : settings) {
                ObjectMapperFeature.configure(mapper,
                                              setting.getEnum(),
                                              setting.booleanValue());
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new BuildException(throwable);
        }
    }

    /**
     * {@link ObjectMapper} configuration setting.
     *
     * {@bean.info}
     */
    public static class Setting extends StringAttributeType {

        /**
         * Sole constructor.
         */
        public Setting() { super(); }

        /**
         * Method to get the feature {@link Enum}.
         *
         * @return      The feature {@link Enum}.
         */
        public Enum<?> getEnum() { return MAP.get(getName()); }

        /**
         * Method to get the feature boolean value.
         *
         * @return      The feature boolean value.
         */
        public boolean booleanValue() {
            return (isEmpty(getValue())
                        ? true
                        : PropertyHelper.toBoolean(getValue()));
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to invoke
     * {@link ObjectMapper#readValue(File,Class)}.
     *
     * {@bean.info}
     */
    @AntTask("om-read-value")
    public static class ReadValue extends ObjectMapperTask {
        private File file = null;
        private String type = null;
        private String collection = null;

        /**
         * No-argument constructor.
         */
        public ReadValue() { this(new ObjectMapper()); }

        /**
         * Protected constructor to allow subclasses to specify the
         * {@link ObjectMapper}.
         *
         * @param       mapper  The {@link ObjectMapper}.
         */
        protected ReadValue(ObjectMapper mapper) { super(mapper); }

        @NotNull
        public File getFile() { return file; }
        public void setFile(File file) { this.file = file; }

        @NotNull
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getCollection() { return collection; }
        public void setCollection(String collection) {
            this.collection = collection;
        }

        /**
         * Method to construct a {@link JavaType} from {@link #getType()}
         * and {@link #getCollection()}.
         *
         * @return      The {@link JavaType}.
         *
         * @throws      Exception       If the {@link JavaType} cannot be
         *                              constructed.
         */
        protected JavaType getJavaType() throws Exception {
            TypeFactory factory = mapper.getTypeFactory();
            JavaType type = null;

            if (! isEmpty(getCollection())) {
                type =
                    factory
                    .constructCollectionType(getClassForName(getCollection())
                                             .asSubclass(Collection.class),
                                             getClassForName(getType()));
            } else {
                type =
                    factory
                    .constructSimpleType(getClassForName(getType()),
                                         new JavaType[] { });
            }

            return type;
        }

        @Override
        public void execute() throws BuildException {
            super.execute();

            try {
                Object value = mapper.readValue(getFile(), getJavaType());

                log(String.valueOf(value));
            } catch (BuildException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throw new BuildException(throwable);
            }
        }
    }
}
