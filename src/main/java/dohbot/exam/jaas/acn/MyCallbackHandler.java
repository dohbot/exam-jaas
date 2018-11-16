package dohbot.exam.jaas.acn;

import lombok.extern.slf4j.Slf4j;

import javax.security.auth.callback.*;
import java.io.*;
import java.util.Arrays;

@Slf4j
public class MyCallbackHandler implements CallbackHandler {
   @Override
   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      for (Callback cb : callbacks) {
         if (cb instanceof TextOutputCallback) {
            TextOutputCallback toc = (TextOutputCallback) cb;
            switch (toc.getMessageType()) {
               case TextOutputCallback.INFORMATION:
                  System.out.println(toc.getMessage());
                  break;
               case TextOutputCallback.ERROR:
                  System.out.println("ERROR: " + toc.getMessage());
                  break;
               case TextOutputCallback.WARNING:
                  System.out.println("WARNING: " + toc.getMessage());
                  break;
               default:
                  throw new IOException("Unsupported message type: " + toc.getMessageType());
            }

         } else if (cb instanceof NameCallback) {

            NameCallback nc = (NameCallback) cb;

            System.err.print(nc.getPrompt());
            System.err.flush();
            nc.setName((new BufferedReader(new InputStreamReader(System.in))).readLine());

         } else if (cb instanceof PasswordCallback) {

            // prompt the user for sensitive information
            PasswordCallback pc = (PasswordCallback) cb;
            System.err.print(pc.getPrompt());
            System.err.flush();
            pc.setPassword(readPassword(System.in));

         } else {
            throw new UnsupportedCallbackException(cb, "Unrecognized Callback");
         }
      }

   }

   private char[] readPassword(InputStream in) throws IOException {

      char[] lineBuffer;
      char[] buf;
      int i;

      buf = lineBuffer = new char[128];

      int room = buf.length;
      int offset = 0;
      int c;

      loop:
      while (true) {
         switch (c = in.read()) {
            case -1:
            case '\n':
               break loop;

            case '\r':
               int c2 = in.read();
               if ((c2 != '\n') && (c2 != -1)) {
                  if (!(in instanceof PushbackInputStream)) {
                     in = new PushbackInputStream(in);
                  }
                  ((PushbackInputStream) in).unread(c2);
               } else
                  break loop;

            default:
               if (--room < 0) {
                  buf = new char[offset + 128];
                  room = buf.length - offset - 1;
                  System.arraycopy(lineBuffer, 0, buf, 0, offset);
                  Arrays.fill(lineBuffer, ' ');
                  lineBuffer = buf;
               }
               buf[offset++] = (char) c;
               break;
         }
      }

      if (offset == 0) {
         return null;
      }

      char[] ret = new char[offset];
      System.arraycopy(buf, 0, ret, 0, offset);
      Arrays.fill(buf, ' ');

      return ret;
   }
}
