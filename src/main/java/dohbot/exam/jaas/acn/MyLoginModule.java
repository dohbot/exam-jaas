package dohbot.exam.jaas.acn;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.Map;

@Slf4j
public class MyLoginModule implements LoginModule {
   // initial state
   private Subject subject;
   private CallbackHandler cbh;

   private Map<String, ?> share;
   private Map<String, ?> opts;

   // the authentication status
   private boolean loggedin = false;
   private boolean committed = false;

   // user and pw
   private String user;
   private String pw;

   // testUser's SamplePrincipal
   private MyPrincipal userPrincipal;

   @Override
   public void initialize(Subject sub, CallbackHandler cbh, Map<String, ?> share, Map<String, ?> opts) {
      this.subject = sub;
      this.cbh = cbh;
      this.share = share;
      this.opts = opts;
   }

   @Override
   public boolean login() throws LoginException {
      if (null == cbh) throw new LoginException("No CallbackHandler");

      NameCallback cbUser = new NameCallback("user: ");
      PasswordCallback cbPw = new PasswordCallback("password: ", false);
      Callback[] cbs = new Callback[]{cbUser, cbPw};

      try {
         this.cbh.handle(cbs);
         this.user = cbUser.getName();
         char[] readPw = cbPw.getPassword();
         this.pw = (null != readPw) ? String.valueOf(readPw) : "";
         cbPw.clearPassword();
      } catch (Exception e) {
         throw new LoginException(e.toString());
      }
      log.info("[{}] entered : {}/{}", this.getClass().getName(), this.user, this.pw);

      if (!"user".equals(this.user) || !"password".equals(this.pw)) {
         log.info("[{}] authentication failed", this.getClass().getName());

         this.user = null;
         this.pw = null;

         this.loggedin = false;

         throw new FailedLoginException("User/Password Incorrect");
      }
      log.info("[{}] authentication logged in", this.getClass().getName());
      this.loggedin = true;
      return true;
   }

   @Override
   public boolean commit() throws LoginException {
      if (!this.loggedin) return false;

      this.userPrincipal = new MyPrincipal(user);
      this.subject.getPrincipals().add(userPrincipal);

      log.info("[{}] added Principal to Subject", this.getClass().getName());

      this.user = null;
      this.pw = null;

      this.committed = true;

      return true;
   }

   @Override
   public boolean abort() throws LoginException {
      return this.loggedin && logout();
   }

   @Override
   public boolean logout() throws LoginException {
      if (this.committed) this.subject.getPrincipals().remove(this.userPrincipal);
      log.info("[{}] removed added Principal from Subject", this.getClass().getName());
      this.committed = false;
      this.loggedin = false;

      this.userPrincipal = null;

      this.user = null;
      this.pw = null;
      log.info("[{}] authentication logged out", this.getClass().getName());
      return true;
   }
}
