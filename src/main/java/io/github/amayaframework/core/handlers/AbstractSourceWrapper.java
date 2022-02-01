package io.github.amayaframework.core.handlers;

import com.github.romanqed.jutils.http.HttpCode;
import com.github.romanqed.jutils.pipeline.PipelineResult;
import io.github.amayaframework.core.contexts.ContentType;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.util.AmayaConfig;
import io.github.amayaframework.core.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractSourceWrapper<T> implements SourceWrapper<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void reject(T source, Exception e) throws IOException {
        HttpCode code = HttpCode.INTERNAL_SERVER_ERROR;
        logger.error(code.getMessage(), e);
        String message = code.getMessage() + "\n";
        if (e != null && AmayaConfig.INSTANCE.getDebug()) {
            message += ParseUtil.throwableToString(e) + "\n";
            Throwable caused = e.getCause();
            if (caused != null) {
                message += "Caused by: \n" + ParseUtil.throwableToString(caused);
            }
        }
        reject(source, code, message);
    }

    @Override
    public void reject(T source, HttpResponse response) throws IOException {
        String message;
        if (response.getContentType().isString()) {
            message = response.getBodyAsString();
        } else {
            message = response.getCode().getMessage();
        }
        reject(source, response.getCode(), message);
    }

    @Override
    public void process(T source, PipelineResult processResult) throws IOException {
        Exception exception = processResult.getException();
        if (exception != null) {
            reject(source, exception);
            return;
        }
        HttpResponse response;
        try {
            response = (HttpResponse) processResult.getResult();
        } catch (Exception e) {
            reject(source, e);
            return;
        }
        if (response == null) {
            reject(source, HttpCode.INTERNAL_SERVER_ERROR, "Response is null");
            return;
        }
        HttpCode code = response.getCode();
        if (code.getCode() >= 400 && code.getCode() <= 511) {
            reject(source, response);
            return;
        }
        ContentType type = response.getContentType();
        if (type != null && type.isString()) {
            sendStringResponse(source, response);
            return;
        }
        sendStreamResponse(source, response);
    }
}
