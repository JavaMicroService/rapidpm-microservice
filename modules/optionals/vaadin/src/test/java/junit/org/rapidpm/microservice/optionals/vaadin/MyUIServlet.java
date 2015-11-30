package junit.org.rapidpm.microservice.optionals.vaadin;

import com.vaadin.annotations.VaadinServletConfiguration;
import org.rapidpm.microservice.optionals.vaadin.DDIVaadinServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by svenruppert on 11.08.15.
 */
@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true, displayName = "Exampl002")
@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
@WebInitParam(name = "Resources", value = "http://virit.in/dawn/11")  //.addInitParam("Resources", "http://virit.in/dawn/11"))
public class MyUIServlet extends DDIVaadinServlet {
  @Override
  protected void servletInitialized() throws ServletException {
    super.servletInitialized();
  }

}