package org.restlet.ext.openapi;

import org.jboss.jandex.DotName;
import org.restlet.resource.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RestletConstants {
    static final DotName GET_MAPPING = DotName.createSimple(Get.class);
    static final DotName PUT_MAPPING = DotName.createSimple(Put.class);
    static final DotName POST_MAPPING = DotName.createSimple(Post.class);
    static final DotName DELETE_MAPPING = DotName.createSimple(Delete.class);
    static final DotName PATCH_MAPPING = DotName.createSimple(Patch.class);

    public static final Set<DotName> HTTP_METHODS = Set.of(
        GET_MAPPING,
        PUT_MAPPING,
        POST_MAPPING,
        DELETE_MAPPING,
        PATCH_MAPPING
    );
}
