package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.DataSources.dataSource;

@ParametersAreNonnullByDefault
public class AuthUserDaoSpringJdbc implements AuthUserDao {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.authJdbcUrl();

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
              "VALUES (?,?,?,?,?,?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    user.setId(generatedKey);
    return user;
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    final List<AuthUserEntity> result = jdbcTemplate.query(
        """
            SELECT a.id   as authority_id,
                   a.authority,
                   u.id,
                   u.username,
                   u.password,
                   u.enabled,
                   u.account_non_expired,
                   u.account_non_locked,
                   u.credentials_non_expired
            FROM "user" u
            JOIN authority a ON u.id = a.user_id
            WHERE u.id = ?
            """,
        AuthUserEntityExtractor.instance,
        id
    );
    return result == null || result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    final List<AuthUserEntity> result = jdbcTemplate.query(
        """
            SELECT a.id   as authority_id,
                   a.authority,
                   u.id,
                   u.username,
                   u.password,
                   u.enabled,
                   u.account_non_expired,
                   u.account_non_locked,
                   u.credentials_non_expired
            FROM "user" u
            JOIN authority a ON u.id = a.user_id
            WHERE u.username = ?
            """,
        AuthUserEntityExtractor.instance,
        username
    );
    return result == null || result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
  }

  @Nonnull
  @Override
  public List<AuthUserEntity> findAll() {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    final List<AuthUserEntity> result = jdbcTemplate.query(
        """
            SELECT a.id   as authority_id,
                   a.authority,
                   u.id,
                   u.username,
                   u.password,
                   u.enabled,
                   u.account_non_expired,
                   u.account_non_locked,
                   u.credentials_non_expired
            FROM "user" u
            JOIN authority a ON u.id = a.user_id
            """,
        AuthUserEntityExtractor.instance
    );
    return result != null ? result : List.of();
  }
}
