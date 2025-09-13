package org.restlet.ext.openapi;

import org.eclipse.microprofile.openapi.models.PathItem;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;

import java.util.LinkedHashSet;
import java.util.Set;

class RestletSupport {
    static Set<PathItem.HttpMethod> getHttpMethods(MethodInfo methodInfo) {
        Set<PathItem.HttpMethod> methods = new LinkedHashSet<>();

        // Try @XXXMapping annotations
        for (DotName validMethodAnnotations : RestletConstants.HTTP_METHODS) {
            if (methodInfo.hasAnnotation(validMethodAnnotations)) {
                String httpMethod = validMethodAnnotations.withoutPackagePrefix().toUpperCase();
                methods.add(PathItem.HttpMethod.valueOf(httpMethod));
            }
        }

        return methods;
    }
}
