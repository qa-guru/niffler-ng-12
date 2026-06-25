package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TestMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

  private static final ThreadLocal<ExtensionContext> store = new ThreadLocal<>();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    store.set(context);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    store.remove();
  }

  @Nonnull
  public static ExtensionContext context() {
    return store.get();
  }
}
