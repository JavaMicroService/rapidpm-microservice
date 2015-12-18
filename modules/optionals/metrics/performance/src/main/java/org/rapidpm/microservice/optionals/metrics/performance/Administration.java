package org.rapidpm.microservice.optionals.metrics.performance;

import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by svenruppert on 14.12.15.
 */
@Path("/metrics/performance/administration")
public class Administration {

  public static final String STOP_JMXREPORTER = "stopJMXReporter";
  public static final String START_JMXREPORTER = "startJMXReporter";
  public static final String STOP_CONSOLEREPORTER = "stopConsoleReporter";
  public static final String START_CONSOLEREPORTER = "startConsoleReporter";

  @GET()
  @Path(START_JMXREPORTER)
  @Produces("application/json")
  public String startJMXReporter() {
    RapidPMMetricsRegistry
        .getInstance().startJmxReporter();
    return result(START_JMXREPORTER);
  }

  @GET()
  @Path(STOP_JMXREPORTER)
  @Produces("application/json")
  public String stopJMXReporter() {
    RapidPMMetricsRegistry
        .getInstance().stopJmxReporter();
    return result(STOP_JMXREPORTER);
  }

  @GET()
  @Path(START_CONSOLEREPORTER)
  @Produces("application/json")
  public String startConsoleReporter() {
    RapidPMMetricsRegistry
        .getInstance()
        .startConsoleReporter();
    return result(START_CONSOLEREPORTER);
  }

  @GET()
  @Path(STOP_CONSOLEREPORTER)
  @Produces("application/json")
  public String stopConsoleReporter() {
    RapidPMMetricsRegistry
        .getInstance()
        .stopConsoleReporter();

    System.out.println("ConsoleReporter was stopped - " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    return result(STOP_CONSOLEREPORTER);
  }

  private String result(final String methodPath) {
    final String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ISO_DATE_TIME);
    return "{\"methodPath\":\"" + methodPath + "\",\"timestamp\":\"" + timestamp + "\"}";
  }

}