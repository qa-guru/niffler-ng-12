package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

@ParametersAreNonnullByDefault
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.userdataJdbcUrl();

  private final UserdataUserDao udUserDao = new UserdataUserDaoSpringJdbc();

  @Override
  @Nonnull
  public UserEntity create(UserEntity user) {
    return udUserDao.create(user);
  }

  @Override
  @Nonnull
  public Optional<UserEntity> findById(UUID id) {
    return udUserDao.findById(id);
  }

  @Override
  @Nonnull
  public Optional<UserEntity> findByUsername(String username) {
    return udUserDao.findByUsername(username);
  }

  @Override
  public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
    insertFriendship(addressee.getId(), requester.getId(), FriendshipStatus.PENDING);
  }

  @Override
  public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
    insertFriendship(requester.getId(), addressee.getId(), FriendshipStatus.PENDING);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    insertFriendship(requester.getId(), addressee.getId(), FriendshipStatus.ACCEPTED);
    insertFriendship(addressee.getId(), requester.getId(), FriendshipStatus.ACCEPTED);
  }

  private void insertFriendship(UUID requesterId, UUID addresseeId, FriendshipStatus status) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(URL));
    jdbcTemplate.update(
        "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)",
        requesterId,
        addresseeId,
        status.name(),
        new Date()
    );
  }
}
