/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.databind.ant.taskdefs;

import ball.util.MapUtil;
import ball.util.PropertiesImpl;
import ball.util.ant.taskdefs.AbstractClasspathTask;
import ball.util.ant.taskdefs.AntTask;
import ball.util.ant.taskdefs.NotNull;
import ball.util.ant.types.StringAttributeType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;

import static ball.util.StringUtil.NIL;
import static ball.util.StringUtil.isNil;

/**
 * Abstract {@link.uri databind://ant.apache.org/ Ant} base
 * {@link org.apache.tools.ant.Task} for {@link ObjectMapper} tasks.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class ObjectMapperTask extends AbstractClasspathTask {
    protected final ObjectMapper mapper = new ObjectMapper();

    /**
     * Sole constructor.
     */
    protected ObjectMapperTask() { super(); }

    public void addConfiguredConfigure(Setting setting) throws BuildException {
        Enum<?> feature = setting.getEnum();
        boolean value = setting.booleanValue();

        if (feature instanceof DeserializationFeature) {
            mapper.configure((DeserializationFeature) feature, value);
        } else if (feature instanceof JsonGenerator.Feature) {
            mapper.configure((JsonGenerator.Feature) feature, value);
        } else if (feature instanceof JsonParser.Feature) {
            mapper.configure((JsonParser.Feature) feature, value);
        } else if (feature instanceof MapperFeature) {
            mapper.configure((MapperFeature) feature, value);
        } else if (feature instanceof SerializationFeature) {
            mapper.configure((SerializationFeature) feature, value);
        } else {
            throw new BuildException("Unrecognized feature `"
                                     + setting.getName() + "'");
        }
    }

    @Override
    public void init() throws BuildException {
        super.init();

        PropertiesImpl properties = new PropertiesImpl();

        MapUtil.copy(getProject().getProperties(), properties);
        properties.configure(this);
    }

    /**
     * {@link ObjectMapper} configuration setting.
     *
     * {@bean.info}
     */
    public static class Setting extends StringAttributeType {
        private static final SortedMap<String,Enum<?>> MAP;

        static {
            TreeMap<String,Enum<?>> map =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (EnumSet<? extends Enum<?>> set :
                     Arrays.asList(EnumSet.allOf(DeserializationFeature.class),
                                   EnumSet.allOf(JsonGenerator.Feature.class),
                                   EnumSet.allOf(JsonParser.Feature.class),
                                   EnumSet.allOf(MapperFeature.class),
                                   EnumSet.allOf(SerializationFeature.class))) {
                for (Enum<?> feature : set) {
                    map.put(feature.name(), feature);
                }
            }

            MAP = Collections.unmodifiableSortedMap(map);
        }

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
            return (isNil(getValue())
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
         * Sole constructor.
         */
        public ReadValue() { super(); }

        @NotNull
        public File getFile() { return file; }
        public void setFile(File file) { this.file = file; }
        public void setFile(String path) { setFile(new File(path)); }

        @NotNull
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getCollection() { return collection; }
        public void setCollection(String collection) {
            this.collection = collection;
        }

        /**
         * Method to construct a {@link JavaType} from {@link getType()} and
         * {@link getCollection()}.
         *
         * @return      The {@link JavaType}.
         */
        protected JavaType getJavaType() {
            TypeFactory factory = mapper.getTypeFactory();
            JavaType type = null;

            if (! isNil(getCollection())) {
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
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Throwable throwable) {
                throw new BuildException(throwable);
            }
        }
    }
}
