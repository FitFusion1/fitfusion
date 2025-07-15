package com.fitfusion.web.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.net.URI;

public class SavedRequestWrapper extends HttpServletRequestWrapper {

    private final String requestUri;

    public SavedRequestWrapper(HttpServletRequest request, String requestUri) {
        super(request);
        this.requestUri = requestUri;
    }

    @Override
    public String getRequestURI() {
        return URI.create(requestUri).getPath();
    }

    @Override
    public String getQueryString() {
        return URI.create(requestUri).getQuery();
    }
}
