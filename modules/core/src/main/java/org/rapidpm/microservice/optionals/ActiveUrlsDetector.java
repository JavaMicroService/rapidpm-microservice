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

package org.rapidpm.microservice.optionals;


import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.optionals.header.ActiveUrlsHolder;
import org.rapidpm.microservice.rest.JaxRsActivator;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class ActiveUrlsDetector {


  public ActiveUrlsHolder detectUrls() {
    final ActiveUrlsHolder activeUrlsHolder = new ActiveUrlsHolder();

    //print all URLs
    final Set<Class<?>> typesAnnotatedWith = DI.getTypesAnnotatedWith(WebServlet.class);

    long servletCount = typesAnnotatedWith.stream()
        .map(aClass1 -> aClass1.getAnnotation(WebServlet.class))
        .filter(ws -> ws.urlPatterns().length > 0)
        .count();

    activeUrlsHolder.setServletCount(servletCount);

    final JaxRsActivator jaxRsActivator = new JaxRsActivator();
//
    final Set<Object> singletonClasses = jaxRsActivator.getSingletons();

    final String realServletPort = System.getProperty(Main.SERVLET_PORT_PROPERTY, Main.DEFAULT_SERVLET_PORT + "");
    final String realServletHost = System.getProperty(Main.SERVLET_HOST_PROPERTY, Main.DEFAULT_HOST);

    typesAnnotatedWith
        .stream()
        .map(c -> c.getAnnotation(WebServlet.class))
        .map(WebServlet::urlPatterns)
        .map(Arrays::asList)
        .flatMap(Collection::stream)
        .forEach(url -> activeUrlsHolder.addServletUrl("http://" + realServletHost + ":" + realServletPort + Main.MYAPP + url));

    final Executor executorREST = activeUrlsHolder::addRestUrl;
    jaxRsActivator
        .getClasses()
        .forEach(executorREST::checkClass);

    final Executor executorSingleton = activeUrlsHolder::addSingletonUrl;
    singletonClasses.forEach(o -> executorSingleton.checkClass(o.getClass()));

    return activeUrlsHolder;
  }


  @FunctionalInterface
  private interface Executor {
    String REAL_REST_PORT = System.getProperty(Main.REST_PORT_PROPERTY, Main.DEFAULT_REST_PORT + "");
    String REAL_REST_HOST = System.getProperty(Main.REST_HOST_PROPERTY, Main.DEFAULT_HOST);

    default void checkClass(final Class<?> aClass) {
      System.out.println("aClass = " + aClass);
      final Path annotation = aClass.getAnnotation(Path.class);
      final String urlPattern = annotation.value();
      String url = "http://" + REAL_REST_HOST + ":" + REAL_REST_PORT + Main.CONTEXT_PATH_REST + urlPattern;

      boolean foundNoPath = true;
      final Method[] declaredMethods = aClass.getDeclaredMethods();
      for (final Method declaredMethod : declaredMethods) {
        final int modifiers = declaredMethod.getModifiers();
        if (Modifier.isPublic(modifiers)) {
          if (declaredMethod.isAnnotationPresent(Path.class)) {
            foundNoPath = false;
            final Path path = declaredMethod.getAnnotation(Path.class);
            String methodPathValue = url + "/" + path.value();
            final Parameter[] declaredMethodParameters = declaredMethod.getParameters();
            for (final Parameter declaredMethodParameter : declaredMethodParameters) {
              if (declaredMethodParameter.isAnnotationPresent(QueryParam.class)) {
                methodPathValue = methodPathValue + " - " + declaredMethodParameter.getAnnotation(QueryParam.class).value();
              }
            }
            addURL(methodPathValue);
          }
        }
      }
      if (foundNoPath) {
        addURL(url);
      }
    }

    void addURL(final String url);
  }


}
