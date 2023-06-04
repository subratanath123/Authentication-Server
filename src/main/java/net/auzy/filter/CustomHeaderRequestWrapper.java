package net.auzy.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CustomHeaderRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public CustomHeaderRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    // Add a custom header to the request
    public void addHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        // Check if the custom header is present
        if (customHeaders.containsKey(name)) {
            return customHeaders.get(name);
        }
        // If not, delegate to the original request
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // Combine the custom header names with the original header names
        Enumeration<String> originalHeaderNames = super.getHeaderNames();
        return new CombinedEnumeration<>(originalHeaderNames, customHeaders.keySet());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        // Check if the custom header is present
        if (customHeaders.containsKey(name)) {
            return new SingleValueEnumeration(customHeaders.get(name));
        }
        // If not, delegate to the original request
        return super.getHeaders(name);
    }
}
