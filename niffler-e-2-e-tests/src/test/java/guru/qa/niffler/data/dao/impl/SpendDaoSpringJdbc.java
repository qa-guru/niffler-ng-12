package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityRowExtractor;
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
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDaoSpringJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.spendJdbcUrl();

  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
              "VALUES ( ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
  }

  private static final String JOIN_SQL = """
      SELECT c.id       as category_id,
             c.name     as category_name,
             c.archived as category_archived,
             s.id,
             s.username,
             s.spend_date,
             s.currency,
             s.amount,
             s.description
      FROM spend s
      JOIN category c ON s.category_id = c.id
      """;

  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    return requireNonNull(jdbcTemplate.query(JOIN_SQL, SpendEntityRowExtractor.instance));
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    final List<SpendEntity> result = jdbcTemplate.query(
        JOIN_SQL + "WHERE s.username = ?",
        SpendEntityRowExtractor.instance,
        username
    );
    return result != null ? result : List.of();
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    final List<SpendEntity> result = jdbcTemplate.query(
        JOIN_SQL + "WHERE s.id = ?",
        SpendEntityRowExtractor.instance,
        id
    );
    return result == null || result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
  }

  @Nonnull
  @Override
  public SpendEntity update(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    jdbcTemplate.update("""
              UPDATE "spend"
                SET spend_date  = ?,
                    currency    = ?,
                    amount      = ?,
                    description = ?
                WHERE id = ?
            """,
        new java.sql.Date(spend.getSpendDate().getTime()),
        spend.getCurrency().name(),
        spend.getAmount(),
        spend.getDescription(),
        spend.getId()
    );
    return spend;
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    jdbcTemplate.update("DELETE FROM spend WHERE id = ?", spend.getId());
  }
}
