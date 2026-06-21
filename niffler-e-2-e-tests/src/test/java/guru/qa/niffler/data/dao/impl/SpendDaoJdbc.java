package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendEntity create(SpendEntity spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
              "VALUES ( ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, spend.getUsername());
        ps.setDate(2, spend.getSpendDate());
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());

        ps.executeUpdate();

        final UUID generatedKey;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedKey = rs.getObject("id", UUID.class);
          } else {
            throw new SQLException("Can`t find id in ResultSet");
          }
        }
        spend.setId(generatedKey);
        return spend;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT sp.id AS spend_id, " +
              "sp.username AS spend_username, " +
              "sp.currency AS spend_currency," +
              "sp.spend_date AS spend_date, " +
              "sp.amount AS spend_amount, " +
              "sp.description AS spend_description, " +
              "cat.id AS category_id, " +
              "cat.name AS category_name, " +
              "cat.username AS category_username, " +
              "cat.archived AS category_archived " +
              "FROM spend sp JOIN category cat ON sp.category_id = cat.id WHERE sp.id = ?"
      )) {
        ps.setObject(1, id);
        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            SpendEntity entity = getSpendEntityFromResultSet(rs);
            return Optional.of(entity);
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
  public List<SpendEntity> findAllByUsername(String username) {
    List<SpendEntity> spends = new ArrayList<>();
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT sp.id AS spend_id, " +
              "sp.username AS spend_username, " +
              "sp.currency AS spend_currency," +
              "sp.spend_date AS spend_date, " +
              "sp.amount AS spend_amount, " +
              "sp.description AS spend_description, " +
              "cat.id AS category_id, " +
              "cat.name AS category_name, " +
              "cat.username AS category_username, " +
              "cat.archived AS category_archived " +
              "FROM spend sp JOIN category cat ON sp.category_id = cat.id WHERE sp.username = ?"
      )) {
        ps.setString(1, username);
        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          while (rs.next()) {
            SpendEntity se = getSpendEntityFromResultSet(rs);
            spends.add(se);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spends;
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM spend WHERE id = ?"
      )) {
        ps.setObject(1, spend.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private SpendEntity getSpendEntityFromResultSet(ResultSet resultSet) throws SQLException {
    SpendEntity se = new SpendEntity();
    se.setId(resultSet.getObject("spend_id", UUID.class));
    se.setUsername(resultSet.getString("spend_username"));
    se.setCurrency(CurrencyValues.valueOf(resultSet.getString("spend_currency")));
    se.setSpendDate(Date.valueOf(resultSet.getObject("spend_date", LocalDate.class)));
    se.setAmount(resultSet.getDouble("spend_amount"));
    se.setDescription(resultSet.getString("spend_description"));

    CategoryEntity ce = new CategoryEntity();
    ce.setId(resultSet.getObject("category_id", UUID.class));
    ce.setName(resultSet.getString("category_name"));
    ce.setUsername(resultSet.getString("category_username"));
    ce.setArchived(resultSet.getBoolean("category_archived"));

    se.setCategory(ce);
    return se;
  }
}
