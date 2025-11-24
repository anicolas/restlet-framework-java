package org.restlet.ext.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

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
                        OpenAPI openAPI = scan(rootRouter);
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

    public OpenAPI scan(Router router) {
        return new OpenAPI();
    }

    private void attachOpenApiSpecificationRestlet(Router router, OpenAPI openAPIDefinition) {
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
            Context context, OpenAPI openAPIDefinition) {
        OpenApiSpecificationRestlet result = new OpenApiSpecificationRestlet(this, openAPIDefinition);
        return result;
    }
}
