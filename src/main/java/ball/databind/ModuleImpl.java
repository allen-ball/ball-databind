/*
 * $Id$
 *
 * Copyright 2017 Allen D. Ball.  All rights reserved.
 */
package ball.databind;

import ball.annotation.ServiceProviderFor;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * {@link Module} service provider for {@link ball.databind}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Module.class })
public class ModuleImpl extends SimpleModule {
    private static final long serialVersionUID = 6899958736994041726L;

    /**
     * Sole constructor.
     */
    public ModuleImpl() {
        super(ModuleImpl.class.getPackage().getName());

        addSerializer(JSONBean.class, new JSONBean.Serializer());
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        super.setupModule(context);
    }

    @Override
    public String toString() { return super.toString(); }
}