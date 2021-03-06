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
package junit.org.rapidpm.microservice.propertyservice.rest.v003;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import junit.org.rapidpm.microservice.BasicRestTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.provided.JVMSingletonInjectionScope;
import org.rapidpm.microservice.propertyservice.impl.PropertyServiceImpl;
import org.rapidpm.microservice.propertyservice.persistence.file.PropertiesFileLoader;
import org.rapidpm.microservice.propertyservice.rest.PropertyServiceRest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;


public class RestTest003 extends BasicRestTest {

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    DI.activateDI(new PropertiesFileLoader());
    DI.registerClassForScope(PropertyServiceImpl.class, JVMSingletonInjectionScope.class.getSimpleName());
    final File file = new File(this.getClass().getResource("test.xml").getFile());
    System.setProperty("propertyservice.configfolder", file.getParentFile().getAbsolutePath());
    System.setProperty("propertyservice.mapname", RestTest003.class.getSimpleName());
  }

  @Override
  @AfterEach
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void test001() throws Exception {
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getConfigurationFile?filename=wrong.xml";
    Client client = ClientBuilder.newClient();
    final Response response = client.target(uri).request().get();
    assertEquals(500, response.getStatus());
  }

  @Test
  public void test002() throws Exception {
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getConfigurationFile?filename=test.xml";
    Client client = ClientBuilder.newClient();
    final File response = client.target(uri).request().get(File.class);
    assertNotNull(response);
  }

  @Test
  public void test003() throws Exception {
    final File resourceFile = new File(this.getClass().getResource("test.xml").getFile());

    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getConfigurationFile?filename=test.xml";
    Client client = ClientBuilder.newClient();
    final File loaderFile = client.target(uri).request().get(File.class);

    long resourceSize = new FileInputStream(resourceFile).getChannel().size();
    long loaderSize = new FileInputStream(loaderFile).getChannel().size();

    assertEquals(resourceSize, loaderSize);
  }
}



