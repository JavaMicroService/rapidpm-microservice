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

package junit.org.rapidpm.microservice.rest.optionals.admin;

import junit.org.rapidpm.microservice.BasicRestTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.microservice.rest.optionals.admin.BasicAdministration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

/** 
* BasicAdministration Tester. 
* 
* @author <Authors name> 
* @since <pre>Dez 7, 2015</pre> 
* @version 1.0 
*/ 
public class BasicAdministrationTest extends BasicRestTest {

  @Override
  @After
  public void tearDown() throws Exception {
    //no stop and wait


  }

  @Test
  public void test001() throws Exception {
    final String generateBasicReqURL = generateBasicReqURL(BasicAdministration.class);
    Client client = ClientBuilder.newClient();
    System.out.println("generateBasicReqURL = " + generateBasicReqURL);
    final Builder authcode = client
        .target(generateBasicReqURL)
        .path("1000")
        .request();
    final Response response = authcode.get();

    Assert.assertEquals(200, response.getStatus());
    String val = response.getStatusInfo().toString();
    System.out.println("response status info = " + val);
    client.close();
    Thread.sleep(3_000);
  }

} 
