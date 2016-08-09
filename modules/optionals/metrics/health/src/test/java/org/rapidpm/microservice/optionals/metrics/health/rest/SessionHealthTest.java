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

package org.rapidpm.microservice.optionals.metrics.health.rest;

import com.google.gson.Gson;
import org.junit.*;
import org.rapidpm.ddi.DI;
import org.rapidpm.dependencies.core.net.PortUtils;
import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.optionals.metrics.health.rest.api.SessionHealthInfo;
import org.rapidpm.microservice.optionals.metrics.health.rest.api.SessionHealthInfoJsonConverter;
import org.rapidpm.microservice.test.RestUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class SessionHealthTest {

  private static String url;
  private final String USER_AGENT = "Mozilla/5.0";

  @BeforeClass
  public static void setUpClass() {
    final PortUtils portUtils = new PortUtils();
    System.setProperty(Main.REST_PORT_PROPERTY, portUtils.nextFreePortForTest() + "");
    System.setProperty(Main.SERVLET_PORT_PROPERTY, portUtils.nextFreePortForTest() + "");
    url = "http://127.0.0.1:" + System.getProperty(Main.SERVLET_PORT_PROPERTY) + Main.MYAPP + "/test"; //from Annotation Servlet
    System.out.println("url = " + url);
  }

  @Before
  public void startUp() {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("junit.org.rapidpm");
    Main.deploy();
  }

  @After
  public void tearDown() {
    Main.stop();
    DI.clearReflectionModel();
  }

  @Test
  public void heathTest001() {
    final String generateBasicReqURL = generateBasicReqURL(SessionHealth.class);
    Client client = ClientBuilder.newClient();
    System.out.println("generateBasicReqURL = " + generateBasicReqURL);
    final Builder authcode = client
            .target(generateBasicReqURL)
            .request();
    final Response response = authcode.get();

    Assert.assertEquals(200, response.getStatus());
    String val = response.getStatusInfo().toString();
    System.out.println("response status info = " + val);
    client.close();

  }

  public String generateBasicReqURL(Class restClass) {
    final String restAppPath = Main.CONTEXT_PATH_REST;
    return new RestUtils().generateBasicReqURL(restClass, restAppPath);
  }

  @Test
  public void heathTest002() {
    final String generateBasicReqURL = generateBasicReqURL(SessionHealth.class);
    Client client = ClientBuilder.newClient();
    System.out.println("generateBasicReqURL = " + generateBasicReqURL);
    final Builder authcode = client
            .target(generateBasicReqURL)
            .request();
    String response = authcode.get(String.class);
    Gson gson = new Gson();
    SessionHealthInfo[] sessionHealthInfo = gson.fromJson(response, SessionHealthInfo[].class);

    Assert.assertEquals(0L, sessionHealthInfo[0].activeSessionCount);

    client.close();

  }

  @Test
  @Ignore
  public void heathTest003() throws IOException {
    generateSession();
    generateSession();
    generateSession();

    String url = generateBasicReqURL(SessionHealth.class);
    URL obj = new URL(url);
    System.out.println("\nSending 'GET' request to URL : " + url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    // optional default is GET
    con.setRequestMethod("GET");
    //add request header
    con.setRequestProperty("User-Agent", USER_AGENT);

    int responseCode = con.getResponseCode();
    System.out.println("Response Code : " + responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    final String jsonResponse = response.toString();
    Assert.assertFalse(jsonResponse.isEmpty());

    final List<SessionHealthInfo> healthInfos = new SessionHealthInfoJsonConverter().fromJsonList(jsonResponse);
    Assert.assertEquals(3L, healthInfos.get(0).activeSessionCount);
  }

  public void generateSession() throws IOException {

    URL obj = new URL(url);
    System.out.println("\nSending 'GET' request to URL : " + url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    // optional default is GET
    con.setRequestMethod("GET");
    //add request header
    con.setRequestProperty("User-Agent", USER_AGENT);

    int responseCode = con.getResponseCode();
    System.out.println("Response Code : " + responseCode);

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
  }
}
