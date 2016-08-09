package junit.org.rapidpm.microservice.propertyservice.impl.v004;

import junit.org.rapidpm.microservice.propertyservice.BaseDITest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.rapidpm.microservice.propertyservice.impl.PropertyServiceImpl;

import java.lang.reflect.Method;

public class PropertyServiceTest004 extends BaseDITest {


  @Test @Ignore
  public void test001() throws Exception {
    Method method = PropertyServiceImpl.class.getDeclaredMethod("getMapName");
    method.setAccessible(true);
    final String result = (String) method.invoke(propertyService);
    Assert.assertEquals("properties", result); // default ??
  }

  @Test
  public void test002() throws Exception {
    System.setProperty("propertyservice.mapname", PropertyServiceTest004.class.getSimpleName());

    Method method = PropertyServiceImpl.class.getDeclaredMethod("getMapName");
    method.setAccessible(true);
    final String result = (String) method.invoke(propertyService);
    Assert.assertEquals(PropertyServiceTest004.class.getSimpleName(), result);


  }
}
