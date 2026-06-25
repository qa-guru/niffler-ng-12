package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.userdataJdbcUrl();

  private final UserdataUserDao udUserDao = new UserdataUserDaoJdbc();

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
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
        "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)"
    )) {
      ps.setObject(1, requesterId);
      ps.setObject(2, addresseeId);
      ps.setString(3, status.name());
      ps.setDate(4, new java.sql.Date(new Date().getTime()));
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
