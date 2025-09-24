package org.restlet.samples.openapidocumentedapplication;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.samples.openapidocumentedapplication.model.Summit;

import java.util.List;

public class SummitsResource extends ServerResource {

    private final SummitsRepository summitsRepository = new SummitsRepository();

    @Get
    public List<Summit> listSummits() {
        return summitsRepository.findAll();
    }

    @APIResponse(responseCode = "201")
    @Post
    public void addSummit(@RequestBody Summit summit) {
        getResponse().setStatus(Status.SUCCESS_CREATED);
    }
}
