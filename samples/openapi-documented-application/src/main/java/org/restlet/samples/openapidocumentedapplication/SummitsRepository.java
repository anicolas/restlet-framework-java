package org.restlet.samples.openapidocumentedapplication;

import org.restlet.samples.openapidocumentedapplication.model.Summit;

import java.util.List;

public class SummitsRepository {
    private static final List<Summit> SUMMITS = List.of(
            new Summit(1, "Mont Blanc", 4806),
            new Summit(2, "Mount Everest", 8849)
    );

    public List<Summit> findAll() {
        return SUMMITS;
    }

    public Summit getById(int id) {
        return SUMMITS.stream().filter(summit -> summit.id() == id).findFirst().orElse(null);
    }
}
