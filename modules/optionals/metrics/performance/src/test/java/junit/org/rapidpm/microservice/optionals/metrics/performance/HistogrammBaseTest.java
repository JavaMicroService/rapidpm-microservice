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

package junit.org.rapidpm.microservice.optionals.metrics.performance;

import com.google.gson.Gson;
import junit.org.rapidpm.microservice.BasicRestTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.rapidpm.microservice.optionals.metrics.performance.HistogrammSnapshot;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.stream.IntStream;

public class HistogrammBaseTest extends BasicRestTest {


  public static final String HISTOGRAMM_NAME = TestRessource.class.getName() + ".doWork";

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    runRequests();
  }

  public int runRequests() throws Exception {
    final int endExclusive = 1_00;

    final String generateBasicReqURL = generateBasicReqURL(TestRessource.class);
    final Client client = ClientBuilder.newClient();
    final WebTarget target = client.target(generateBasicReqURL);

    IntStream
        .range(0, endExclusive)
        .forEach(i -> target.request().get(String.class));
    client.close();
    return endExclusive;
  }


  @NotNull
  public String requestWithCheck(final String generateBasicReqURL) {
    final String result = request(generateBasicReqURL);
    Assert.assertNotNull(result);
    Assert.assertFalse(result.isEmpty());
    return result;
  }

  public String request(final String generateBasicReqURL) {
    Client client = ClientBuilder.newClient();
    String val = client
        .target(generateBasicReqURL)
        .request()
        .get(String.class);
    client.close();
    return val;
  }

  @NotNull
  public void requestWithCheckWithVoid(final String generateBasicReqURL) {
    Client client = ClientBuilder.newClient();
    client
        .target(generateBasicReqURL)
        .request();
    client.close();
  }

  @NotNull
  public HistogrammSnapshot fromJsonWithCheck(final String result) {
    final HistogrammSnapshot histogrammSnapshot = new Gson().fromJson(result, HistogrammSnapshot.class);
    Assert.assertNotNull(histogrammSnapshot);
    Assert.assertEquals(histogrammSnapshot.getName(), HISTOGRAMM_NAME);
    return histogrammSnapshot;
  }


}
