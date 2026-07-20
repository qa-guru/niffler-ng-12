package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc implements AuthUserDao {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.authJdbcUrl();

  @Nonnull
  @Override
  @SuppressWarnings("resource")
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
        "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      user.setId(generatedKey);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  @SuppressWarnings("resource")
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
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
            """)) {
      ps.setObject(1, id);
      ps.execute();
      final List<AuthUserEntity> users = mapToAuthUsers(ps.getResultSet());
      return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  @SuppressWarnings("resource")
  public Optional<AuthUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
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
            """)) {
      ps.setString(1, username);
      ps.execute();
      final List<AuthUserEntity> users = mapToAuthUsers(ps.getResultSet());
      return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  @SuppressWarnings("resource")
  public List<AuthUserEntity> findAll() {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
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
            """)) {
      ps.execute();
      return mapToAuthUsers(ps.getResultSet());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  private static List<AuthUserEntity> mapToAuthUsers(ResultSet rs) throws SQLException {
    final Map<UUID, AuthUserEntity> userCache = new LinkedHashMap<>();
    while (rs.next()) {
      final UUID userId = rs.getObject("id", UUID.class);
      AuthUserEntity user = userCache.get(userId);
      if (user == null) {
        user = new AuthUserEntity();
        user.setId(userId);
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        userCache.put(userId, user);
      }
      final AuthorityEntity ae = new AuthorityEntity();
      ae.setId(rs.getObject("authority_id", UUID.class));
      ae.setAuthority(Authority.valueOf(rs.getString("authority")));
      ae.setUser(user);
      user.addAuthorities(ae);
    }
    return new ArrayList<>(userCache.values());
  }
}
