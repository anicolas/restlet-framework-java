package org.restlet.samples.openapidocumentedapplication;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.samples.openapidocumentedapplication.model.Summit;

import java.util.List;

public class SummitsResource extends ServerResource {

    private final SummitsRepository summitsRepository = new SummitsRepository();

    @Get
    public List<Summit> listSummits() {
        return summitsRepository.findAll();
    }
}
