/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.circabc.eushare.services.UserService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

public class RefererFilter implements Filter {

    private final String serviceDomain;
    private final String servicePath;

    public RefererFilter(String serviceURL) throws Exception {
        URL url = new URL(serviceURL);
        this.serviceDomain = url.getHost();
        this.servicePath = url.getPath();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String referer = httpRequest.getHeader("Referer");

        if (referer == null || containsIpAddress(referer)) {
            referer = httpRequest.getHeader("X-Forwarded-Host");
        }

        if (referer == null || (referer != null && !isValidReferer(referer))) {
            throw new ServletException("Invalid request" + referer);
        }

        chain.doFilter(request, response);
    }

    private boolean isValidReferer(String referer) {
        try {
            URL refererURL = new URL(referer);
            return serviceDomain.equals(refererURL.getHost())
                    && refererURL.getPath().startsWith(servicePath);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean containsIpAddress(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            InetAddress inetAddress = InetAddress.getByName(host);
            return inetAddress.getHostAddress().equals(host); 
        } catch (Exception e) {
            return false;
        }
    }

}
