package dohbot.exam.jaas.acn;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;
import java.util.Objects;

@Data
@AllArgsConstructor
public class MyPrincipal implements Principal {
   private String name;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MyPrincipal that = (MyPrincipal) o;
      return Objects.equals(name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name);
   }
}
