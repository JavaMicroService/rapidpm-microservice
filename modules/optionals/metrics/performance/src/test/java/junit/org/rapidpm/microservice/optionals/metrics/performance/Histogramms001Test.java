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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.optionals.metrics.performance.HistogrammSnapshot;
import org.rapidpm.microservice.optionals.metrics.performance.Histogramms;

import java.util.List;

public class Histogramms001Test extends HistogrammBaseTest {



  @Test
  public void test001() throws Exception {

    final String generateBasicReqURL = generateBasicReqURL(Histogramms.class);
    System.out.println("generateBasicReqURL = " + generateBasicReqURL);

    final String result = requestWithCheck(generateBasicReqURL + "/" + Histogramms.LIST_ALL_HISTOGRAMMS);
    final List<HistogrammSnapshot> histogrammSnapshots = new Gson().fromJson(result, Histogramms.HISTORY_SNAPSHOT_LIST_TYPE);
    Assert.assertTrue(histogrammSnapshots.isEmpty());

    DI.activateMetrics(TestRessource.class);

    runRequests();

    final String resultWithMetrics = requestWithCheck(generateBasicReqURL + "/" + Histogramms.LIST_ALL_HISTOGRAMMS);
    Assert.assertNotNull(resultWithMetrics);
    final List<HistogrammSnapshot> histogrammSnapshotsWithMetrics = new Gson().fromJson(resultWithMetrics, Histogramms.HISTORY_SNAPSHOT_LIST_TYPE);
    Assert.assertNotNull(histogrammSnapshotsWithMetrics);
    Assert.assertFalse(histogrammSnapshotsWithMetrics.isEmpty());

    for (HistogrammSnapshot histogrammSnapshot : histogrammSnapshots) {
      Assert.assertNotNull(histogrammSnapshot);
    }
  }


  @Test
  public void test002() throws Exception {
    final String generateBasicReqURL = generateBasicReqURL(Histogramms.class)
        + "/" + Histogramms.LIST_ONE_HISTOGRAMMS
        + "?" + Histogramms.QUERY_PARAM_HISTOGRAMM_NAME + "=" + HISTOGRAMM_NAME;

    fromJsonWithCheck(
        requestWithCheck(
            generateBasicReqURL));
  }


  @Test
  public void test003() throws Exception {
    DI.activateMetrics(TestRessource.class);
    runRequests();

    final String generateBasicReqURL = generateBasicReqURL(Histogramms.class)
        + "/" + Histogramms.LIST_ONE_HISTOGRAMMS
        + "?" + Histogramms.QUERY_PARAM_HISTOGRAMM_NAME + "=" + HISTOGRAMM_NAME;

    final HistogrammSnapshot preHistogrammSnapshot = fromJsonWithCheck(
        requestWithCheck(
            generateBasicReqURL));

    Assert.assertTrue(preHistogrammSnapshot.getHistogramCount() > 0);

    //DLETE
    final String generateBasicReqURL2Remove = generateBasicReqURL(Histogramms.class)
        + "/" + Histogramms.REMOVE_ONE_HISTOGRAMMS
        + "?" + Histogramms.QUERY_PARAM_HISTOGRAMM_NAME + "=" + HISTOGRAMM_NAME;

    final HistogrammSnapshot histogrammSnapshot =
        fromJsonWithCheck(
            requestWithCheck(
                generateBasicReqURL2Remove));

    Assert.assertNotNull(histogrammSnapshot);
    Assert.assertTrue(histogrammSnapshot.getHistogramCount() > 0);

    final String checkURL = generateBasicReqURL(Histogramms.class)
        + "/" + Histogramms.LIST_ONE_HISTOGRAMMS
        + "?" + Histogramms.QUERY_PARAM_HISTOGRAMM_NAME + "=" + HISTOGRAMM_NAME;

    final HistogrammSnapshot check =
        fromJsonWithCheck(
            requestWithCheck(
                checkURL));

    Assert.assertEquals(check.getHistogramCount(), 0);

  }

  @Test
  public void test004() throws Exception {
    final String basicReqURL = generateBasicReqURL(Histogramms.class);
    final String generateBasicReqURL = basicReqURL + "/" + Histogramms.LIST_ALL_HISTOGRAMM_NAMES;
    System.out.println("generateBasicReqURL = " + generateBasicReqURL);
    final String requestWithCheck = requestWithCheck(generateBasicReqURL);
    System.out.println("requestWithCheck = " + requestWithCheck);
  }


}
