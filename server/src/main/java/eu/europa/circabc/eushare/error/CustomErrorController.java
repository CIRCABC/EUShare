/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.error;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

@Controller
public class CustomErrorController implements ErrorController {

  private Logger log = LoggerFactory.getLogger(CustomErrorController.class);

  @ApiIgnore
  @RequestMapping("/error")
  @ResponseBody
  public String handleError(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute(
      "javax.servlet.error.status_code"
    );
    Exception exception = (Exception) request.getAttribute(
      "javax.servlet.error.exception"
    );
    if (exception != null) {
      log.error("Error happened", exception);
    }
    return String.format(
      "<html><body><h2>Error Page</h2><div>Status code: <b>%s</b></div>" +
      "<div>Exception Message: <b>%s</b></div><body></html>",
      statusCode,
      exception == null ? "N/A" : exception.getMessage()
    );
  }
}
