package org.restlet.samples.openapidocumentedapplication;

import org.restlet.samples.openapidocumentedapplication.model.Summit;

import java.util.List;

public class SummitsRepository {
    private static final List<Summit> SUMMITS = List.of(
            new Summit("Mont Blanc", 4806),
            new Summit("Mount Everest", 8849)
    );

    public List<Summit> findAll() {
        return SUMMITS;
    }
}
