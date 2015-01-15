package de.zaunkoenigweg.m2e.pojomaker.core;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

    public static final String PLUGIN_ID = "de.zaunkoenigweg.m2e.pojomaker.core";

    private static Activator plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        logOk("M2E-Connector for Pojomaker Maven Plugin started.");
    }

    public static Activator getDefault() {
        return plugin;
    }

    public void logOk(String message) {
        this.getLog().log(new Status(Status.OK, PLUGIN_ID, message));
    }
}
