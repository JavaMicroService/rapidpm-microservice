package junit.org.rapidpm.microservice.propertyservice.impl.v002;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.propertyservice.api.PropertyService;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

public class PropertyServiceTest002 /*extends BaseDITest*/ {


  private static final String PROPERTY_KEY = "example.part01.001";
  private static final String PROPERTY_VALUE = "test001";

  @Inject PropertyService service;

  @Before
  public void setUp() {

    System.setProperty("propertyservice.mapname", this.getClass().getSimpleName());
    System.setProperty("propertyservice.propertyfolder", PropertyServiceTest002.class.getResource("").getPath());

    DI.clearReflectionModel();
    DI.activatePackages(this.getClass());
    DI.activatePackages("org.rapidpm");
    DI.activateDI(this);

    service.initFromCmd();
    service.loadProperties("example");
  }

  @After
  public void tearDown() throws Exception {
    service.forget();
  }

  @Test
  public void test001() throws Exception {
    final String singleProperty = service.getSingleProperty(PROPERTY_KEY);
    Assert.assertNotNull(singleProperty);
    Assert.assertFalse(singleProperty.isEmpty());
    Assert.assertEquals(PROPERTY_VALUE, singleProperty);
  }

  @Test
  public void test002() throws Exception {
    final String singleProperty = service.getSingleProperty("example.invalid");
    Assert.assertTrue(singleProperty.isEmpty());
  }

  @Test
  public void test003() throws Exception {
    final Set<String> index = service.getIndexOfLoadedProperties();

    Assert.assertNotNull(index);
    Assert.assertTrue(index.size() > 0);
    Assert.assertTrue(index.contains("example.part01.001"));
    Assert.assertTrue(index.contains("example.part01.002"));
  }

  @Test
  public void test004() throws Exception {
    final Set<String> indexToDomain = service.getIndexOfScope("single");

    Assert.assertNotNull(indexToDomain);
    Assert.assertEquals(1, indexToDomain.size());
    Assert.assertTrue(indexToDomain.contains("single.theonlykey"));
  }

  @Test
  public void test005() throws Exception {
    final Map<String, String> propertiesOfDomain = service.getPropertiesOfScope("single");

    Assert.assertNotNull(propertiesOfDomain);
    Assert.assertEquals(1, propertiesOfDomain.size());
    Assert.assertTrue(propertiesOfDomain.keySet().contains("single.theonlykey"));
    Assert.assertEquals("theonlyvalue", propertiesOfDomain.get("single.theonlykey"));
  }
}
