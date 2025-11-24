package org.restlet.ext.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.routing.Router;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenApiSpecificationRestlet extends Restlet {

    /**
     * The Application to describe.
     */
    private final Application application;

    /**
     * The OpenAPI definition of the API
     */
    private final OpenAPI openAPIDefinition;

    public OpenApiSpecificationRestlet(Application application, OpenAPI openAPIDefinition) {
        super(application.getContext());
        this.application = application;
        this.openAPIDefinition = openAPIDefinition;
    }

    @Override
    public void handle(Request request, Response response) {
        super.handle(request, response);

        if (Method.GET.equals(request.getMethod())) {
            response.setEntity(getOpenApiDefinitionAsRepresentation(openAPIDefinition));
        } else {
            response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }
    }

    private Representation getOpenApiDefinitionAsRepresentation(OpenAPI openAPI) {
        OpenAPIConfiguration oasConfig = new SwaggerConfiguration()
                .prettyPrint(true)
                .resourceClasses(Set.of("org.restlet.samples.openapidocumentedapplication.SummitResource", "org.restlet.samples.openapidocumentedapplication.SummitsResource"))
                .resourcePackages(Stream.of("org.restlet.samples").collect(Collectors.toSet()));

        try {
            var context = new GenericOpenApiContextBuilder()
                    .openApiConfiguration(oasConfig)
                    .buildContext(true);

            var openApiRead = context.read();

            var openApiAsYaml = context.getOutputYamlMapper().writer(new DefaultPrettyPrinter()).writeValueAsString(openApiRead);

            return new StringRepresentation(openApiAsYaml, MediaType.APPLICATION_YAML);

        } catch (OpenApiConfigurationException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    public void attach(Router router) {
        router.attach("/openapi.yaml", this);
    }
}
