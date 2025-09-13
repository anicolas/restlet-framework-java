package org.restlet.ext.openapi;

import io.smallrye.openapi.api.util.MergeUtil;
import io.smallrye.openapi.runtime.scanner.AnnotationScannerExtension;
import io.smallrye.openapi.runtime.scanner.ResourceParameters;
import io.smallrye.openapi.runtime.scanner.dataobject.TypeResolver;
import io.smallrye.openapi.runtime.scanner.spi.AbstractAnnotationScanner;
import io.smallrye.openapi.runtime.scanner.spi.AnnotationScannerContext;
import io.smallrye.openapi.runtime.util.ModelUtil;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.jboss.jandex.*;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RestletAnnotationScanner extends AbstractAnnotationScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestletAnnotationScanner.class);

    @Override
    public String getName() {
        return "Restlet";
    }

    @Override
    public OpenAPI scan(AnnotationScannerContext context, OpenAPI openAPI) {
        this.context = context;

        if (context.getClassLoader() instanceof RestletClassLoaderWrapper restletClassLoaderWrapper) {
            return processRouter(restletClassLoaderWrapper.getRouter(), openAPI);
        } else {
            return openAPI;
        }
    }

    private OpenAPI processRouter(Router router, OpenAPI openApi) {
        List<Route> allRoutes = new ArrayList<>(router.getRoutes());
        if (router.getDefaultRoute() != null) {
            allRoutes.add(router.getDefaultRoute());
        }

        for (Route route : allRoutes) {
            processRoute(route, openApi);
        }

        return openApi;
    }

    private void processRoute(Route route, OpenAPI openApi) {
        if (route instanceof TemplateRoute templateRoute) {
            String path = templateRoute.getTemplate().getPattern();

            if (route.getNext() instanceof Finder finder) {
                ServerResource serverResource = finder.find(null, null);

                if (serverResource != null) {
                    LOGGER.info("Got route '{}', resource '{}'", templateRoute.getTemplate().getPattern(), serverResource.getClass());
                    OpenAPI resourceOpenAPI = processServerResource(serverResource, path, openApi);
                    MergeUtil.merge(openApi, resourceOpenAPI);
                }
            }
        } else {
            LOGGER.info("Route type ignored: {}", route.getClass());
        }
    }

    private OpenAPI processServerResource(ServerResource serverResource, String path, OpenAPI baseOpenAPI) {

        ClassInfo serverResourceClassInfo = getServerResourceClassInfo(serverResource);
        TypeResolver resolver = TypeResolver.forClass(context, serverResourceClassInfo, null);
        context.getResolverStack().push(resolver);

        for (MethodInfo methodInfo : getResourceMethods(context, serverResourceClassInfo)) {
            if (!methodInfo.annotations().isEmpty()) {
                for (PathItem.HttpMethod httpMethod : RestletSupport.getHttpMethods(methodInfo)) {
                    processServerResourceMethod(serverResourceClassInfo, methodInfo, httpMethod, path, baseOpenAPI);
                }
            }
        }

        return baseOpenAPI;
    }

    private void processServerResourceMethod(final ClassInfo resourceClass,
                                             final MethodInfo method,
                                             final PathItem.HttpMethod methodType,
                                             String path, OpenAPI openApi) {

        // Process any @Operation annotation
        Optional<Operation> maybeOperation = processOperation(context, resourceClass, method);
        if (!maybeOperation.isPresent()) {
            return; // If the operation is marked as hidden, just bail here because we don't want it as part of the model.
        }
        final Operation operation = maybeOperation.get();

        // Process @Parameter annotations.
        PathItem pathItem = OASFactory.createPathItem();
        ResourceParameters params = getResourceParameters(resourceClass, method);

        operation.setParameters(params.getOperationParameters());

        // Process @APIResponse annotations
        processResponse(context, resourceClass, method, operation, null);

        // Get or create a PathItem to hold the operation
        PathItem existingPath = ModelUtil.paths(openApi).getPathItem(path);

        if (existingPath == null) {
            ModelUtil.paths(openApi).addPathItem(path, pathItem);
        } else {
            // Changes applied to 'existingPath', no need to re-assign or add to OAI.
            MergeUtil.mergeObjects(existingPath, pathItem);
        }
    }

    private ResourceParameters getResourceParameters(final ClassInfo resourceClass,
                                                     final MethodInfo method) {
        var resourceParameters = new ResourceParameters();
        resourceParameters.setOperationParameters(Collections.emptyList());
        return resourceParameters;
    }

    private ClassInfo getServerResourceClassInfo(ServerResource serverResource) {
        try {
            Index index = Index.of(serverResource.getClass());
            return index.getClassByName(DotName.createSimple(serverResource.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAsyncResponse(MethodInfo methodInfo) {
        return false;
    }

    @Override
    public boolean isPostMethod(MethodInfo methodInfo) {
        return false;
    }

    @Override
    public boolean isDeleteMethod(MethodInfo methodInfo) {
        return false;
    }

    @Override
    public boolean containsScannerAnnotations(List<AnnotationInstance> list, List<AnnotationScannerExtension> list1) {
        return false;
    }
}
