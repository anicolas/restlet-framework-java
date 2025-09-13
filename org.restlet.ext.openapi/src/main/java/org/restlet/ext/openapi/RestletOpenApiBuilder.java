package org.restlet.ext.openapi;

import io.smallrye.openapi.api.SmallRyeOpenAPI;
import io.smallrye.openapi.runtime.scanner.OpenApiAnnotationScanner;

public class RestletOpenApiBuilder extends SmallRyeOpenAPI.Builder {
    public RestletOpenApiBuilder() {
        super();
        enableAnnotationScan(false);
    }

    @Override
    protected <V, A extends V, O extends V, AB, OB> void buildAnnotationModel(BuildContext<V, A, O, AB, OB> ctx) {
        super.buildAnnotationModel(ctx);
    }

    @Override
    public SmallRyeOpenAPI build() {
        return super.build();
    }
}
