package guru.qa.niffler.config;


import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  INSTANCE;

  @Override
  @Nonnull
  public String frontUrl() {
    return "http://localhost:3000/";
  }

  @Override
  @Nonnull
  public String authUrl() {
    return "http://localhost:9000/";
  }

  @Override
  @Nonnull
  public String gatewayUrl() {
    return "http://localhost:8090/";
  }

  @Override
  @Nonnull
  public String userdataUrl() {
    return "http://localhost:8089/";
  }

  @Override
  @Nonnull
  public String spendUrl() {
    return "http://localhost:8093/";
  }

  @Override
  @Nonnull
  public String spendJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-spend";
  }

  @Override
  @Nonnull
  public String currencyJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-currency";
  }

  @Override
  @Nonnull
  public String authJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-auth";
  }

  @Override
  @Nonnull
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-userdata";
  }

  @Override
  @Nonnull
  public String githubUrl() {
    return "https://api.github.com/";
  }
}
