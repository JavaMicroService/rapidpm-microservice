package junit.org.rapidpm.microservice.propertyservice.startup.commandline.v001;

import junit.org.rapidpm.microservice.BasicRestTest;
import org.apache.commons.cli.Option;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.microservice.optionals.cli.CmdLineStartupAction;
import org.rapidpm.microservice.propertyservice.startup.commandline.DedicatedMapNameCmdLineOption;

import java.util.List;

public class DedicatedMapNameCmdLineOptionTest001 extends BasicRestTest {


  @Test
  public void test001() throws Exception {
    final CmdLineStartupAction lineOptions = new DedicatedMapNameCmdLineOption();
    final List<Option> options = lineOptions.getOptions();
    Assert.assertNotNull(options);
    Assert.assertFalse(options.isEmpty());
    Assert.assertEquals("mn", options.get(0).getOpt());
  }


}
