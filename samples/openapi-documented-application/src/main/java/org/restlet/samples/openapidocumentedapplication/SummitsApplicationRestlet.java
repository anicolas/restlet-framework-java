package org.restlet.samples.openapidocumentedapplication;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.openapi.RestletOpenApiApplication;
import org.restlet.routing.Router;
import org.restlet.samples.openapidocumentedapplication.model.Summit;

import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Summits API", version = "1.0"))
public class SummitsApplicationRestlet extends RestletOpenApiApplication {
    public SummitsApplicationRestlet() {
        super(
                List.of(Summit.class)
        );
    }

    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/summits", SummitsResource.class);
        router.attach("/summits/{summitId}", SummitResource.class);
        return router;
    }

    public static void main(String[] args) throws Exception {
        var application = new SummitsApplicationRestlet();
        var context = new Context();
        var server = new Server(context, Protocol.HTTP, 8080, application);
        server.start();
    }
}
