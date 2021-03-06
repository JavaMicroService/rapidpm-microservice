/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.org.rapidpm.microservice.propertyservice.rest.v002;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.provided.JVMSingletonInjectionScope;
import org.rapidpm.microservice.propertyservice.impl.PropertyServiceImpl;
import org.rapidpm.microservice.propertyservice.persistence.file.PropertiesFileLoader;
import org.rapidpm.microservice.propertyservice.rest.PropertyServiceRest;
import junit.org.rapidpm.microservice.BasicRestTest;

public class RestTest002 extends BasicRestTest {

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    DI.activateDI(new PropertiesFileLoader());
    DI.registerClassForScope(PropertyServiceImpl.class , JVMSingletonInjectionScope.class.getSimpleName());
    System.setProperty("propertyservice.mapname" , RestTest002.class.getSimpleName());
    System.setProperty("propertyservice.propertyfolder" , this.getClass().getResource("").getPath());
  }

  @Override
  @AfterEach
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void test001() throws Exception {
    callLoadingOfProperties();
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getIndexOfLoadedProperties";
    Client client = ClientBuilder.newClient();
    final String response = client
        .target(uri)
        .request()
        .get(String.class);

    Assertions.assertTrue(response.contains("\"restscope01.001\""));
    Assertions.assertTrue(response.contains("\"restscope02.001\""));
    Assertions.assertTrue(response.contains("\"restscope02.002\""));
    Assertions.assertTrue(response.contains("\"restscope02.003\""));
  }

  private void callLoadingOfProperties() {
    Client client = ClientBuilder.newClient();
    final String uri01 = generateBasicReqURL(PropertyServiceRest.class) + "/loadProperties?scope=restscope01";
    final String uri02 = generateBasicReqURL(PropertyServiceRest.class) + "/loadProperties?scope=restscope02";
    client
        .target(uri01)
        .request()
        .get(String.class);
    client
        .target(uri02)
        .request()
        .get(String.class);
    client.close();
  }


}



