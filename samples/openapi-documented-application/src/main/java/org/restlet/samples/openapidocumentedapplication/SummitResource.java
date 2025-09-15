package org.restlet.samples.openapidocumentedapplication;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.samples.openapidocumentedapplication.model.Summit;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SummitResource extends ServerResource {
    private final SummitsRepository summitsRepository = new SummitsRepository();

    @Get
    public Summit getSummit() {
        return summitsRepository.getById(getSummitIdPathVariable());
    }

    public int getSummitIdPathVariable() {
        String value = getAttribute("id");
        String idAsString = URLDecoder.decode(value, StandardCharsets.UTF_8);
        return Integer.parseInt(idAsString);
    }
}
