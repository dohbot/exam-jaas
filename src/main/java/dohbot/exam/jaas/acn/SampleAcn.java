package dohbot.exam.jaas.acn;

import lombok.extern.slf4j.Slf4j;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class SampleAcn {
   public static void main(String... args) throws URISyntaxException {
      URI uri = ClassLoader.getSystemResource("jaas.config").toURI();

      System.setProperty("java.security.auth.login.config", uri.toString());
      try {

         LoginContext lc = new LoginContext("Sample", new MyCallbackHandler());
         lc.login();
         lc.logout();

      } catch (LoginException e) {
         log.error("login failed.", e);
      }

   }
}
