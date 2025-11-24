package org.restlet.samples.openapidocumentedapplication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    @Post
    @Operation(summary = "Add a new summit", description = "Add a new summit to the list of summits")
    public void addSummit(@RequestBody Summit summit) {
        getResponse().setStatus(Status.SUCCESS_CREATED);
    }
}
