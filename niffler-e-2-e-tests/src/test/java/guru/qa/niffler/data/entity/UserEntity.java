package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CurrencyValues;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEntity implements Serializable {
  private UUID id;
  private String username;
  private CurrencyValues currency;
  private String firstname;
  private String surname;
  private String fullName;
  private byte[] photo;
  private byte[] photoSmall;
}
