package junit.org.rapidpm.microservice.propertyservice.startup.commandline.v003;

import org.junit.Test;
import org.rapidpm.microservice.Main;

import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class LoadPropertiesStartupActionTest003 {

  //@Rule
  //public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void test001() throws Exception {
    //final File file = folder.newFile("test.properties");
    //writePropertiesToFile(file);
    //final Optional<String[]> args = Optional.of(new String[]{"-i" + file.getAbsoluteFile().getCanonicalPath()});
    final Optional<String[]> args = Optional.of(new String[]{"-i" + this.getClass().getResource("test.properties").getPath()});

    Main.deploy(args);
    final Properties properties = System.getProperties();
    assertEquals("/opt/test", properties.getProperty("propertyservice.propertyfolder"));
    assertEquals("true", properties.getProperty("propertyservice.distributed"));
    assertEquals("test", properties.getProperty("propertyservice.mapname"));
    Main.stop();
  }

/*  private void writePropertiesToFile(final File file) throws IOException {
    final FileOutputStream fos = new FileOutputStream(file);
    final Properties properties = new Properties();
    properties.setProperty("propertyservice.propertyfolder", "/opt/test");
    properties.setProperty("propertyservice.distributed", "true");
    properties.setProperty("propertyservice.mapname", "test");
    properties.store(fos, "temporary properties");
    fos.flush();
    fos.close();
  }*/

}
