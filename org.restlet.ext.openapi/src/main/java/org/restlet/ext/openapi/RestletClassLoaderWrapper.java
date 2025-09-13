package org.restlet.ext.openapi;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

class RestletClassLoaderWrapper extends ClassLoader {
    private final Router router;

    RestletClassLoaderWrapper(ClassLoader parent, Router router) {
        super(parent);
        this.router = router;
    }

    Router getRouter() {
        return router;
    }
}
