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

package org.rapidpm.microservice.optionals.service;


import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.rapidpm.ddi.DI;
import org.rapidpm.dependencies.core.system.ExitHandler;
import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.rest.optionals.admin.BasicAdministration;

import javax.ws.rs.Path;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.rapidpm.microservice.Main.CONTEXT_PATH_REST;
import static org.rapidpm.microservice.Main.deploy;

public class ServiceWrapper {

  public static final String SHUTDOWN = "SHUTDOWN";
  public static final int DELAY = 1000;
  public static final java.nio.file.Path MICROSERVICE_REST_FILE = Paths.get("microservice.rest");

  public static ExitHandler exitHandler = DI.activateDI(ExitHandler.class);

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
    checkPortFileExists();
    deploy(Optional.ofNullable(args));
    writeRestPortToFile();
  }

  private static void checkPortFileExists() {

    if (Files.exists(MICROSERVICE_REST_FILE)) {
      System.err.println("File " + MICROSERVICE_REST_FILE + "already exists.");
      System.err.println("Service seems to be running. Make sure the service terminated and remove the file.");
      System.err.println("Then restart the service");
      exitHandler.exit(1);
    }
  }

  private static void writeRestPortToFile() {
    String restPort = System.getProperty(Main.REST_PORT_PROPERTY);
    try {
      Files.write(MICROSERVICE_REST_FILE, Arrays.asList(restPort));
    } catch (IOException e) {
      System.err.println("Can not write to File " + MICROSERVICE_REST_FILE + "check permissions and restart the service");
      exitHandler.exit(1);
    }

  }

  private static void shutdownMicroservice() {

    String restBaseUrl = buildBaseUrl();

    Optional<Annotation> pathAnnotation = getAnnotation();

    if (pathAnnotation.isPresent()) {
      sendShutdownToService(restBaseUrl, pathAnnotation);
    } else {
      System.err.print("Could not locate Path of rest service. Could it be you forgot to add the admin optional?");
      exitHandler.exit(1);
    }

    try {
      Files.delete(MICROSERVICE_REST_FILE);
    } catch (IOException e) {
      System.err.print("Could not delete file " + MICROSERVICE_REST_FILE + "Cleanup file before restart");
      exitHandler.exit(1);
    }

  }

  private static void sendShutdownToService(String restBaseUrl, Optional<Annotation> pathAnnotation) {
    String adminRestPath = getAdminRestPath(pathAnnotation);

    System.out.println("Sending SHUTDOWN message to microservice");

    String returnedValue = null;
    try {
      returnedValue = callRest(restBaseUrl, adminRestPath);
    } catch (IOException e) {
      System.err.println("Failed to call rest endpoint");
      exitHandler.exit(1);
    }
    System.out.println("Received answer, Server shutting down");

    if (!returnedValue.toLowerCase().contains("ok")) {
      System.err.println("Service returned <" + returnedValue + ">");
      System.err.println("Something went wrong, exiting");
      exitHandler.exit(1);
    }
  }

  private static String buildBaseUrl() {
    try {
      String restPortFromFile = getRestPortFromFile();
      return String.format("http://127.0.0.1:%d/%s", Integer.valueOf(restPortFromFile), CONTEXT_PATH_REST);

    } catch (FileNotFoundException e) {
      System.err.println("The file " + MICROSERVICE_REST_FILE + " was not found. \n It seems like your service wasn't started");
      exitHandler.exit(1);
    } catch (IOException e) {
      System.err.println("The file " + MICROSERVICE_REST_FILE + " wasn't read/writeable. \n" + e.getMessage());
      exitHandler.exit(1);
    } catch (NumberFormatException e) {
      System.err.println("The file " + MICROSERVICE_REST_FILE + " contained no readable port. \n" + e.getMessage());
      System.err.println("Remove the file and start the service again");
      exitHandler.exit(1);
    }

    return null; //never reached
  }

  private static String getRestPortFromFile() throws IOException {
    List<String> lines = Files.readAllLines(MICROSERVICE_REST_FILE, Charset.defaultCharset());
    if (lines.size() != 1) {
      throw new NumberFormatException("File does not contain a number. It is empty");
    }
    return lines.get(0);
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


  private static String callRest(String restBaseUrl, String adminRestPath) throws IOException {
    CloseableHttpClient httpClient = HttpClients.createMinimal();
    HttpGet httpGet = new HttpGet(restBaseUrl + adminRestPath + "/" + DELAY);
    final CloseableHttpResponse response = httpClient.execute(httpGet);
    return IOUtils.toString(response.getEntity().getContent());
  }
}