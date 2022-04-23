package io.github.amayaframework.core.wrapping;


import com.github.romanqed.util.Action;
import io.github.amayaframework.core.contexts.HttpRequest;
import io.github.amayaframework.core.contexts.HttpResponse;

import java.lang.reflect.Method;

/**
 * An interface describing the method packer used in the framework.
 */
public interface Packer {
    Action<HttpRequest, HttpResponse> pack(Object instance, Method method) throws Throwable;

    Action<HttpRequest, HttpResponse> checkedPack(Object instance, Method method);
}
