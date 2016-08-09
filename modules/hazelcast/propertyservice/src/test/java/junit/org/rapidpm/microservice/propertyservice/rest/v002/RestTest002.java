package junit.org.rapidpm.microservice.propertyservice.rest.v002;


import junit.org.rapidpm.microservice.BasicRestTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.scopes.provided.JVMSingletonInjectionScope;
import org.rapidpm.microservice.propertyservice.impl.PropertyServiceImpl;
import org.rapidpm.microservice.propertyservice.persistence.file.PropertiesFileLoader;
import org.rapidpm.microservice.propertyservice.rest.PropertyServiceRest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class RestTest002 extends BasicRestTest {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    DI.activateDI(new PropertiesFileLoader());
    DI.registerClassForScope(PropertyServiceImpl.class, JVMSingletonInjectionScope.class.getSimpleName());
    System.setProperty("propertyservice.mapname", RestTest002.class.getSimpleName());
    System.setProperty("propertyservice.propertyfolder", this.getClass().getResource("").getPath());
  }

  @Override
  @After
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

    Assert.assertTrue(response.contains("\"restscope01.001\""));
    Assert.assertTrue(response.contains("\"restscope02.001\""));
    Assert.assertTrue(response.contains("\"restscope02.002\""));
    Assert.assertTrue(response.contains("\"restscope02.003\""));
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



