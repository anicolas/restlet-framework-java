package org.restlet.ext.openapi;

import org.restlet.Application;
import org.restlet.routing.Router;

class RestletClassLoaderWrapper extends ClassLoader {
    private final Application application;
    private final Router router;

    RestletClassLoaderWrapper(ClassLoader parent, Application application, Router router) {
        super(parent);
        this.application = application;
        this.router = router;
    }

    public Application getApplication() {
        return application;
    }

    Router getRouter() {
        return router;
    }
}
