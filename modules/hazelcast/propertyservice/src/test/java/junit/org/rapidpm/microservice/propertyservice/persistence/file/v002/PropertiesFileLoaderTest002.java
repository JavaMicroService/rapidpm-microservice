package junit.org.rapidpm.microservice.propertyservice.persistence.file.v002;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidpm.microservice.propertyservice.persistence.PropertiesLoader;
import org.rapidpm.microservice.propertyservice.persistence.file.PropertiesFileLoader;

import java.util.Map;

public class PropertiesFileLoaderTest002 {

  private PropertiesLoader fileLoader;

  @Before
  public void setUp() throws Exception {
    fileLoader = new PropertiesFileLoader();
  }

  @Test
  public void test001() throws Exception {
    final Map<String, String> properties = fileLoader.load(this.getClass().getResource("").getPath(), "scope01");

    Assert.assertNotNull(properties);
    Assert.assertTrue(properties.size() > 0);
    Assert.assertTrue(properties.containsKey("scope01.001"));
    Assert.assertEquals("001", properties.get("scope01.001"));
  }

  @Test
  public void test002() throws Exception {
    final Map<String, String> scope01 = fileLoader.load(this.getClass().getResource("").getPath(), "scope01");
    final Map<String, String> scope02 = fileLoader.load(this.getClass().getResource("").getPath(), "scope02");

    Assert.assertNotNull(scope01);
    Assert.assertTrue(scope01.size() == 2);
    Assert.assertNotNull(scope02);
    Assert.assertTrue(scope02.size() == 1);
  }

}
