package org.restlet.ext.openapi;

import io.smallrye.openapi.api.SmallRyeOpenAPI;
import org.jboss.jandex.Index;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestletOpenApiApplication extends Application {
    /**
     * Indicates if this application can document herself.
     */
    private volatile boolean documented;

    private final List<Class<?>> classesToScan;

    protected RestletOpenApiApplication(List<Class<?>> classesToScan) {
        this.classesToScan = classesToScan;
    }

    @Override
    public Restlet getInboundRoot() {
        Restlet inboundRoot = super.getInboundRoot();

        if (!documented) {
            synchronized (this) {
                if (!documented) {
                    Router rootRouter = getNextRouter(inboundRoot);

                    if (!documented && rootRouter != null) {
                        SmallRyeOpenAPI openAPI = scan(rootRouter);
                        attachOpenApiSpecificationRestlet(rootRouter, openAPI);
                        documented = true;
                    }

                }
            }
        }

        return inboundRoot;
    }

    /**
     * Returns the next router available.
     *
     * @param current The current Restlet to inspect.
     * @return The first router available.
     */
    private static Router getNextRouter(Restlet current) {
        Router result = null;
        if (current instanceof Router) {
            result = (Router) current;
        } else if (current instanceof Filter) {
            result = getNextRouter(((Filter) current).getNext());
        }

        return result;
    }

    public SmallRyeOpenAPI scan(Router router) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            var classes = new ArrayList<>(classesToScan);
            classes.add(getClass());
            var index = Index.of(classes); // FIXME is that necessary?

            var classLoaderWrapper = new RestletClassLoaderWrapper(classLoader, this, router);

            SmallRyeOpenAPI result = SmallRyeOpenAPI.builder()
                    .withApplicationClassLoader(classLoaderWrapper)
                    .withIndex(index)
                    .build();

            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void attachOpenApiSpecificationRestlet(Router router, SmallRyeOpenAPI openAPIDefinition) {
        getOpenApiSpecificationRestlet(getContext(), openAPIDefinition).attach(router);
        documented = true;
    }

    /**
     * The dedicated {@link Restlet} able to generate the Swagger specification
     * formats.
     *
     * @return The {@link Restlet} able to generate the Swagger specification
     * formats.
     */
    public OpenApiSpecificationRestlet getOpenApiSpecificationRestlet(
            Context context, SmallRyeOpenAPI openAPIDefinition) {
        OpenApiSpecificationRestlet result = new OpenApiSpecificationRestlet(this, openAPIDefinition);
        return result;
    }
}
