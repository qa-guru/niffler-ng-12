package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.spend.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoJdbc implements UserdataUserDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity createUser(UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO \"user\" (username, firstname, surname, full_name, currency, photo, photo_small) " +
              "VALUES (?, ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, user.getUsername());
        ps.setObject(2, user.getCurrency());
        ps.setString(3, user.getFirstname());
        ps.setString(4, user.getSurname());
        ps.setString(5, user.getFullName());
        ps.setBytes(6, user.getPhoto());
        ps.setBytes(7, user.getPhotoSmall());
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
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT * FROM \"user\" WHERE id = ?"
      )) {
        ps.setObject(1, id);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            UserEntity userEntity = getUserEntityFromResultSet(rs);
            return Optional.of(userEntity);
          } else {
            return Optional.empty();
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT * FROM \"user\" WHERE username = ?"
      )) {
        ps.setObject(1, username);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            UserEntity userEntity = getUserEntityFromResultSet(rs);
            return Optional.of(userEntity);
          } else {
            return Optional.empty();
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM \"user\" WHERE id = ?"
      )) {
        ps.setObject(1, user.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private UserEntity getUserEntityFromResultSet(ResultSet resultSet) throws SQLException {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(resultSet.getObject("id", UUID.class));
    userEntity.setUsername(resultSet.getString("username"));
    userEntity.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
    userEntity.setFirstname(resultSet.getString("firstname"));
    userEntity.setSurname(resultSet.getString("surname"));
    userEntity.setFullName(resultSet.getString("full_name"));
    userEntity.setPhoto(resultSet.getBytes("photo"));
    userEntity.setPhotoSmall(resultSet.getBytes("photo_small"));
    return userEntity;
  }
}
