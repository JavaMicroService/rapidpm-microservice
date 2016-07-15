package org.rapidpm.microservice.optionals.service;

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


import com.google.common.io.Files;
import org.jetbrains.annotations.NotNull;
import org.rapidpm.microservice.rest.optionals.admin.BasicAdministration;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

import static org.rapidpm.microservice.Main.CONTEXT_PATH_REST;
import static org.rapidpm.microservice.Main.deploy;

public class ServiceWrapper {

  public static final String SHUTDOWN = "SHUTDOWN";
  public static final int DELAY = 100;
  public static final String MICROSERVICE_REST_FILE = "microservice.rest";
  public static final String MICROSERVICE_REST_PORT_SYSTEM_PROPERTY = "org.jboss.resteasy.port";

  private ServiceWrapper() {
  }

  public static void main(String[] args) {
    boolean shutdown = Arrays.stream(args).anyMatch(s -> s.equals(SHUTDOWN));
    if (!shutdown) {
      startMicroservice(args);
    } else {
      shutdownMicroservice();
    }
  }

  private static void startMicroservice(String[] args) {
    deploy(Optional.ofNullable(args));
    String restPort = System.getProperty(MICROSERVICE_REST_PORT_SYSTEM_PROPERTY);
    try (PrintWriter printWriter = new PrintWriter(MICROSERVICE_REST_FILE)) {
      printWriter.write(restPort);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static void shutdownMicroservice() {

    String restBaseUrl = buildBaseUrl();

    Optional<Annotation> pathAnnotation = getAnnotation();

    if (pathAnnotation.isPresent()) {
      sendShutdownToService(restBaseUrl, pathAnnotation);
    } else {
      System.err.print("Could not locate Path of rest service. Could it be you forgot to add the admin optional?");
      System.exit(1);
    }
  }

  private static void sendShutdownToService(String restBaseUrl, Optional<Annotation> pathAnnotation) {
    String adminRestPath = getAdminRestPath(pathAnnotation);

    System.out.println("Sending SHUTDOWN message to microservice");

    String returnedValue = callRest(restBaseUrl, adminRestPath);
    System.out.println("Received answer, Server shutting down");

    if (!returnedValue.toLowerCase().contains("ok")) {
      System.err.println("Service returned <" + returnedValue + ">");
      System.err.println("Something went wrong, exiting");
      System.exit(1);
    }
  }

  private static String buildBaseUrl() {
    try {
      String restPortFromFile = Files.readFirstLine(new File(MICROSERVICE_REST_FILE), Charset.defaultCharset());
      return String.format("http://localhost:%d/%s", Integer.valueOf(restPortFromFile), CONTEXT_PATH_REST);

    } catch (FileNotFoundException e) {
      System.err.println("The file " + MICROSERVICE_REST_FILE + " was not found. \n It seems like your service wasn't started");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("The file " + MICROSERVICE_REST_FILE + " wasn't read/writeable. \n" + e.getMessage());
      System.exit(1);
    }

    return null; //never reached
  }

  private static Optional<Annotation> getAnnotation() {
    return Arrays.asList(BasicAdministration.class.getAnnotations()).stream()
            .filter(a -> a.annotationType().equals(Path.class))
            .findFirst();
  }

  @NotNull
  private static String getAdminRestPath(Optional<Annotation> pathAnnotation) {
    Path annotation = (Path) pathAnnotation.get();
    return annotation.value();
  }


  private static String callRest(String restBaseUrl, String adminRestPath) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(restBaseUrl + adminRestPath + "/" + DELAY);
    Response response = target.request().get();
    return response.readEntity(String.class);
  }
}