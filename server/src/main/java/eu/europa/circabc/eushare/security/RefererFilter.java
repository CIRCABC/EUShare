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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.circabc.eushare.configuration.EushareConfiguration;

import java.io.IOException;

public class RefererFilter implements Filter {

    private String allowedOrigin;
    
    public RefererFilter(String allowedOrigin){
        this.allowedOrigin = allowedOrigin;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
       
        String referer = httpRequest.getHeader("Referer");

        if (referer == null || !referer.startsWith(allowedOrigin)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Referer");
            return;
        }

        chain.doFilter(request, response);
    }

}
