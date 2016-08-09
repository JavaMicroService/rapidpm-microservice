/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.rapidpm.microservice;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.*;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.reflections.ReflectionUtils;
import org.rapidpm.microservice.optionals.ActiveUrlsDetector;
import org.rapidpm.microservice.optionals.header.ActiveUrlPrinter;
import org.rapidpm.microservice.optionals.header.HeaderScreenPrinter;
import org.rapidpm.microservice.rest.JaxRsActivator;
import org.rapidpm.microservice.rest.ddi.DdiInjectorFactory;
import org.rapidpm.microservice.servlet.ServletInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static io.undertow.servlet.Servlets.*;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static final String MYAPP = "/microservice"; //TODO extract to Optional - Servlet
  public static final String CONTEXT_PATH_REST = "/rest"; //TODO extract to Optional - REST

  public static final int DEFAULT_REST_PORT = 7081; //TODO extract to Optional - REST
  public static final int DEFAULT_SERVLET_PORT = 7080; //TODO extract to Optional - Servlet
  public static final String REST_PORT_PROPERTY = "org.rapidpm.microservice.rest.port"; //TODO extract to Optional - REST
  public static final String REST_HOST_PROPERTY = "org.rapidpm.microservice.rest.host"; //TODO extract to Optional - REST
  public static final String SERVLET_PORT_PROPERTY = "org.rapidpm.microservice.servlet.port"; //TODO extract to Optional - Servlet
  public static final String SERVLET_HOST_PROPERTY = "org.rapidpm.microservice.servlet.host"; //TODO extract to Optional - Servlet
  public static final String DEFAULT_HOST = "0.0.0.0";
  private static final String RESTEASY_PORT_PROPERTY = "org.jboss.resteasy.port"; //TODO extract to Optional - REST
  private static final String RESTEASY_HOST_PROPERTY = "org.jboss.resteasy.host"; //TODO extract to Optional - REST
  private static final Timer TIMER = new Timer(true);
  private static UndertowJaxrsServer jaxrsServer; //TODO extract to Optional - REST
  private static Undertow undertowServer; //TODO extract to Optional - Servlet
  private static Optional<String[]> cliArguments;
  private static LocalDateTime deployStart;

  private Main() {
  }

  public static void main(String[] args) {
    cliArguments = Optional.ofNullable(args);
    deploy(cliArguments);
  }

  public static void deploy(Optional<String[]> args) {
    cliArguments = args;
    deployStart = LocalDateTime.now();
    executeStartupActions(args);
    DI.bootstrap(); // per config steuern

    final Builder builder = Undertow.builder() //TODO optional
        .setDirectBuffers(true)
        .setServerOption(UndertowOptions.ENABLE_HTTP2, true);

    // deploy servlets
    DeploymentInfo deploymentInfo = createServletDeploymentInfos();
    final boolean anyServlets = !deploymentInfo.getServlets().isEmpty();
    if (anyServlets) {
      try {
        deployServlets(builder, deploymentInfo);
      } catch (ServletException e) {
        e.printStackTrace();
        LOGGER.error("deploy Servlets ", e);
      }
    }

    final JaxRsActivator jaxRsActivator = new JaxRsActivator();
    if (jaxRsActivator.somethingToDeploy()) {
      deployRestRessources(builder, jaxRsActivator);
    } else {
      undertowServer = builder.build();
      undertowServer.start();
    }


    new HeaderScreenPrinter().printOnScreen();
    new ActiveUrlPrinter().printActiveURLs(new ActiveUrlsDetector().detectUrls());


    System.out.println("");
    System.out.println("");
    final LocalDateTime stopTime = LocalDateTime.now();
    System.out.println(" ############  Startup finished  = " + stopTime + " ############  ");
    System.out.println(" ############  Startup time [ms] = " + Duration.between(deployStart, stopTime).toMillis() + " ############  ");

  }

  private static void executeStartupActions(final Optional<String[]> args) {
    final Set<Class<? extends MainStartupAction>> classes = DI.getSubTypesOf(MainStartupAction.class);
    createInstances(classes).stream()
        .map(DI::activateDI)
        .forEach((mainStartupAction) -> mainStartupAction.execute(args));
  }

  private static DeploymentInfo createServletDeploymentInfos() {

    final Set<Class<?>> typesAnnotatedWith = DI.getTypesAnnotatedWith(WebServlet.class, true);

    final List<ServletInfo> servletInfos = typesAnnotatedWith
        .stream()
        .filter(s -> new ReflectionUtils().checkInterface(s, HttpServlet.class))
        .map(c -> {
          Class<HttpServlet> servletClass = (Class<HttpServlet>) c;
          final ServletInfo servletInfo = servlet(c.getSimpleName(), servletClass, new ServletInstanceFactory<>(servletClass));
          if (c.isAnnotationPresent(WebInitParam.class)) {
            final WebInitParam[] annotationsByType = c.getAnnotationsByType(WebInitParam.class);
            for (WebInitParam webInitParam : annotationsByType) {
              final String value = webInitParam.value();
              final String name = webInitParam.name();
              servletInfo.addInitParam(name, value);
            }
          }
          final WebServlet annotation = c.getAnnotation(WebServlet.class);
          final String[] urlPatterns = annotation.urlPatterns();
          for (String urlPattern : urlPatterns) {
            servletInfo.addMapping(urlPattern);
          }
          servletInfo.setAsyncSupported(annotation.asyncSupported());
          return servletInfo;
        })
        .filter(servletInfo -> !servletInfo.getMappings().isEmpty())
        .collect(Collectors.toList());

    final Set<Class<?>> weblisteners = DI.getTypesAnnotatedWith(WebListener.class);
    final List<ListenerInfo> listenerInfos = weblisteners.stream()
        .map(c -> new ListenerInfo((Class<? extends EventListener>) c))
        .collect(Collectors.toList());

    return deployment()
        .setClassLoader(Main.class.getClassLoader())
        .setContextPath(MYAPP)
        .setDeploymentName("ROOT" + ".war")
        .setDefaultEncoding("UTF-8")
        .addListeners(listenerInfos)
//        .setResourceManager(new ClassPathResourceManager(Undertow.class.getClassLoader(),""))
//            .setResourceManager(new FileResourceManager(new File("src/main/webapp"), 1024))
        .addServlets(servletInfos);
  }

  private static void deployServlets(final Builder builder, final DeploymentInfo deploymentInfo) throws ServletException {
    final ServletContainer servletContainer = defaultContainer();
    DeploymentManager manager = servletContainer.addDeployment(deploymentInfo);
    manager.deploy();
    HttpHandler servletHandler = manager.start();
    PathHandler pathServlet = Handlers
        .path(Handlers.redirect(MYAPP))
        .addPrefixPath(MYAPP, servletHandler);
    final String realServletPort = System.getProperty(SERVLET_PORT_PROPERTY, DEFAULT_SERVLET_PORT + "");
    final String realServletHost = System.getProperty(SERVLET_HOST_PROPERTY, DEFAULT_HOST);

    builder.addHttpListener(Integer.parseInt(realServletPort), realServletHost, pathServlet);
  }

  private static void deployRestRessources(final Builder builder, final JaxRsActivator jaxRsActivator) {
    final String realRestPort = System.getProperty(REST_PORT_PROPERTY, DEFAULT_REST_PORT + "");
    final String realRestHost = System.getProperty(REST_HOST_PROPERTY, DEFAULT_HOST);

    System.setProperty(RESTEASY_PORT_PROPERTY, realRestPort);
    System.setProperty(RESTEASY_HOST_PROPERTY, realRestHost);

    builder.addHttpListener(Integer.parseInt(realRestPort), realRestHost);
    jaxrsServer = new UndertowJaxrsServer().start(builder);
    final ResteasyDeployment deployment = new ResteasyDeployment();
    deployment.setApplication(jaxRsActivator);
//      deployment.setAsyncJobServiceEnabled(false);
    deployment.setInjectorFactoryClass(DdiInjectorFactory.class.getCanonicalName());
    jaxrsServer.deploy(jaxrsServer.undertowDeployment(deployment)
        .setDeploymentName("Rest")
        .setContextPath(CONTEXT_PATH_REST)
        .setClassLoader(Main.class.getClassLoader()));
  }

  private static <T> List<T> createInstances(final Set<Class<? extends T>> classes) {
    return classes
        .stream()
        .map(c -> {
          try {
            return Optional.of(c.newInstance());
          } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
          }
          return Optional.<T>empty();
        })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.<T>toList());
  }

  public static void stop(long delayMS) {
    LOGGER.warn("shutdown delay [ms] = " + delayMS);
    if (delayMS < 1_000) delayMS = delayMS + 1_000;

    TIMER.schedule(new TimerTask() {
      @Override
      public void run() {
        LOGGER.warn("delayed shutdown  now = " + LocalDateTime.now());
        stop();
      }
    }, delayMS);
  }

  public static void stop() {
    executeShutdownActions(cliArguments);
    if (jaxrsServer != null) {
      if (new JaxRsActivator().somethingToDeploy())
        try {
          jaxrsServer.stop();
        } catch (Exception e) {
          LOGGER.error("jaxrsServer.stop()", e);
        }
    } else if (undertowServer != null) {
      try {
        undertowServer.stop();
      } catch (Exception e) {
        LOGGER.error("undertowServer.stop()", e);
      }
    }
  }

  private static void executeShutdownActions(Optional<String[]> args) {
    final Set<Class<? extends MainShutdownAction>> classes = DI.getSubTypesOf(MainShutdownAction.class);
    createInstances(classes)
        .stream()
        .map(DI::activateDI)
        .forEach((mainShutdownAction) -> mainShutdownAction.execute(args));
  }

  public static void deploy() {
    deploy(Optional.empty());
  }

  @FunctionalInterface
  public interface MainStartupAction {
    void execute(Optional<String[]> args);
  }

  @FunctionalInterface
  public interface MainShutdownAction {
    void execute(Optional<String[]> args);
  }


}
