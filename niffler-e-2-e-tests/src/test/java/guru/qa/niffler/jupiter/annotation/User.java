package guru.qa.niffler.jupiter.annotation;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface User {
  @Nonnull
  String username() default "";

  @Nonnull
  Category[] categories() default {};

  @Nonnull
  Spending[] spendings() default {};

  int friends() default 0;

  int incomeInvitations() default 0;

  int outcomeInvitations() default 0;
}
