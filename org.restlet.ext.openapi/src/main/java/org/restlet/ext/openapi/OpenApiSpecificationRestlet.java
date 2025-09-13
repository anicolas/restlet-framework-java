package org.restlet.ext.openapi;

import io.smallrye.openapi.api.SmallRyeOpenAPI;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.routing.Router;

import java.nio.charset.StandardCharsets;

public class OpenApiSpecificationRestlet extends Restlet {

    /**
     * The Application to describe.
     */
    private final Application application;

    /**
     * The OpenAPI definition of the API
     */
    private final SmallRyeOpenAPI openAPIDefinition;

    public OpenApiSpecificationRestlet(Application application, SmallRyeOpenAPI openAPIDefinition) {
        super(application.getContext());
        this.application = application;
        this.openAPIDefinition = openAPIDefinition;
    }

    @Override
    public void handle(Request request, Response response) {
        super.handle(request, response);

        if (Method.GET.equals(request.getMethod())) {
            response.setEntity(getOpenApiDefinitionAsRepresentation());
        } else {
            response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }
    }

    private Representation getOpenApiDefinitionAsRepresentation() {
        var openApiAsYaml = openAPIDefinition.toYAML().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayRepresentation(openApiAsYaml, MediaType.APPLICATION_YAML);
    }

    public void attach(Router router) {
        router.attach("/openapi.yaml", this);
    }
}
