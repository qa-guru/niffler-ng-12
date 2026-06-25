package guru.qa.niffler.model;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public record TestData(String password,
                       List<CategoryJson> categories,
                       List<SpendJson> spendings,
                       List<UserJson> incomeInvitations,
                       List<UserJson> outcomeInvitations,
                       List<UserJson> friends) {
  public TestData(String password) {
    this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public TestData(String password, List<UserJson> incomeInvitations, List<UserJson> outcomeInvitations, List<UserJson> friends) {
    this(password, new ArrayList<>(), new ArrayList<>(), incomeInvitations, outcomeInvitations, friends);
  }

  public TestData(String password, List<CategoryJson> categories, List<SpendJson> spendings, List<UserJson> incomeInvitations, List<UserJson> outcomeInvitations, List<UserJson> friends) {
    this.password = password;
    this.categories = categories;
    this.spendings = spendings;
    this.incomeInvitations = incomeInvitations;
    this.outcomeInvitations = outcomeInvitations;
    this.friends = friends;
  }

  @Nonnull
  public String[] friendsUsernames() {
    return extractUsernames(friends);
  }

  @Nonnull
  public String[] incomeInvitationUsernames() {
    return extractUsernames(incomeInvitations);
  }

  @Nonnull
  public String[] outcomeInvitationUsernames() {
    return extractUsernames(outcomeInvitations);
  }

  @Nonnull
  public String[] extractUsernames(List<UserJson> users) {
    return users.stream().map(UserJson::username).toArray(String[]::new);
  }

  @Nonnull
  public String[] categoryDescriptions() {
    return categories.stream().map(CategoryJson::name).toArray(String[]::new);
  }
}
