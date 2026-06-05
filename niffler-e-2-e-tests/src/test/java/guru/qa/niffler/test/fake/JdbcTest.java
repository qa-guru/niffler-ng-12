package guru.qa.niffler.test.fake;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;


public class JdbcTest {

  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "cat-name-tx-3",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-tx-3",
            "duck"
        )
    );

    System.out.println(spend);
  }

  @Test
  @Disabled
  void xaTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUser(
        "valentin-4",
        "12345"
    );
    System.out.println(user);
  }

  @Test
  @Disabled
  void springJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserSpringJdbc(
        new UserJson(
            null,
            "valentin-5",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  static UsersDbClient usersDbClient = new UsersDbClient();

  @ValueSource(strings = {
      "valentin-1340"
  })
  @ParameterizedTest
  void hibernateTest(String uname) {
    UserJson user = usersDbClient.createUser(
        uname,
        "12345"
    );

    usersDbClient.addIncomeInvitation(user, 1);
    usersDbClient.addOutcomeInvitation(user, 1);
  }
}
