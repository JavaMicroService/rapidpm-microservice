package junit.org.rapidpm.microservice.propertyservice.persistence.file.v001;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.microservice.propertyservice.persistence.PropertiesLoader;
import org.rapidpm.microservice.propertyservice.persistence.file.PropertiesFileLoader;

import java.util.Map;

public class PropertiesFileLoaderTest001 {

  private PropertiesLoader fileLoader;

  @Before
  public void setUp() throws Exception {
    fileLoader = new PropertiesFileLoader();
  }

  @Test
  public void test001() throws Exception {
    final Map<String, String> properties = fileLoader.load(this.getClass().getResource("test.properties").getPath());

    Assert.assertNotNull(properties);
    Assert.assertTrue(properties.size() > 0);
    Assert.assertTrue(properties.containsKey("domain01.property01"));
    Assert.assertEquals("d01p01", properties.get("domain01.property01"));
  }
}
