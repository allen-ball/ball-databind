/*
 * $Id$
 *
 * Copyright 2016 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.databind.ant.taskdefs;

import ball.databind.ObjectMapperFeature;
import ball.util.ant.taskdefs.AnnotatedAntTask;
import ball.util.ant.taskdefs.AntTask;
import ball.util.ant.taskdefs.ClasspathDelegateAntTask;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;

import static ball.databind.ObjectMapperFeature.MAP;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant} base {@link Task} for
 * {@link ObjectMapper} tasks.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class ObjectMapperTask extends Task
                                       implements AnnotatedAntTask,
                                                  ClasspathDelegateAntTask,
                                                  ConfigurableAntTask {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @Getter @Setter
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

    public void addConfiguredConfigure(Setting setting) {
        settings.add(setting);
    }

    @Override
    public void init() throws BuildException {
        super.init();
        ClasspathDelegateAntTask.super.init();
        ConfigurableAntTask.super.init();

        try {
            if (isRegisterModules()) {
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

    @Override
    public void execute() throws BuildException {
        super.execute();
        AnnotatedAntTask.super.execute();
    }

    /**
     * {@link ObjectMapper} configuration setting.
     *
     * {@bean.info}
     */
    @NoArgsConstructor @ToString
    public static class Setting extends StringAttributeType {

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
     * {@ant.task}
     */
    @AntTask("om-read-value")
    @ToString
    public static class ReadValue extends ObjectMapperTask {
        @NotNull @Getter @Setter
        private File file = null;
        @NotNull @Getter @Setter
        private String type = null;
        @Getter @Setter
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

        /**
         * Method to construct a {@link JavaType} from {@code getType()} and
         * {@code getCollection()}.
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
