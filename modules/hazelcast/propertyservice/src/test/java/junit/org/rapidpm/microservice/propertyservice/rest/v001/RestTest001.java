package junit.org.rapidpm.microservice.propertyservice.rest.v001;


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

public class RestTest001 extends BasicRestTest {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    DI.activateDI(new PropertiesFileLoader());
    DI.registerClassForScope(PropertyServiceImpl.class, JVMSingletonInjectionScope.class.getSimpleName());
    System.setProperty("propertyservice.mapname", RestTest001.class.getSimpleName());
    System.setProperty("propertyservice.propertyfolder", this.getClass().getResource("").getPath());
  }

  @Override
  @After
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void test001() throws Exception {
    final String response = callLoadingOfProperties();
    Assert.assertEquals("success", response);
  }

  private String callLoadingOfProperties() {
    Client client = ClientBuilder.newClient();
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/loadProperties?scope=rest01";
    final String response = client
        .target(uri)
        .request()
        .get(String.class);
    client.close();
    return response;
  }

  @Test
  public void test002() throws Exception {
    callLoadingOfProperties();
    Client client = ClientBuilder.newClient();
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getSingleProperty?property=rest01.prop01";
    final String response = client
        .target(uri)
        .request()
        .get(String.class);
    client.close();
    Assert.assertEquals("test001", response);
  }

  @Test
  public void test003() throws Exception {
    callLoadingOfProperties();
    Client client = ClientBuilder.newClient();
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getIndexOfLoadedProperties";
    final String response = client
        .target(uri)
        .request()
        .get(String.class);
    client.close();
    Assert.assertNotNull(response);
    Assert.assertFalse(response.isEmpty());
  }

  @Test
  public void test004() throws Exception {
    callLoadingOfProperties();
    Client client = ClientBuilder.newClient();
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getIndexOfScope?scope=rest01";
    final String response = client
        .target(uri)
        .request()
        .get(String.class);
    client.close();
    Assert.assertNotNull(response);
    Assert.assertEquals("[\"rest01.prop01\"]", response);
  }

  @Test
  public void test005() throws Exception {
    callLoadingOfProperties();
    Client client = ClientBuilder.newClient();
    final String uri = generateBasicReqURL(PropertyServiceRest.class) + "/getPropertiesOfScope?scope=rest01";
    final String response = client
        .target(uri)
        .request()
        .get(String.class);
    client.close();
    Assert.assertNotNull(response);
    Assert.assertEquals("{\"rest01.prop01\":\"test001\"}", response);
  }

}



