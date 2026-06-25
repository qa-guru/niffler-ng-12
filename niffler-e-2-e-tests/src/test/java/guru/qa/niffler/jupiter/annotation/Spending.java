package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.model.CurrencyValues;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Spending {
  @Nonnull
  String description();

  double amount();

  @Nonnull
  CurrencyValues currency() default CurrencyValues.RUB;

  @Nonnull
  String category();
}
