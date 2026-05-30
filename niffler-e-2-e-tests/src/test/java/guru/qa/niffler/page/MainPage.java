package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

  private final ElementsCollection tableRows = $("table tbody").$$("tr");
  private final SelenideElement personIconButton = $("button > div > svg[data-testid='PersonIcon']");
  private final SelenideElement menu = $("[role='menu']");
  private final SelenideElement linkFriends = $("[href='/people/friends']");




  public EditSpendingPage openSpendingByDescription(String description) {
    tableRows.find(text(description))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage checkThatTableContainsSpending(String description) {
    tableRows.find(text(description)).should(visible);
    return this;
  }

  public MainPage openMenu(){
    personIconButton.click();
    menu.shouldBe(visible);
    return new MainPage();
  }


  public FriendsPage openFriendsPage(){
    linkFriends.click();
    return new FriendsPage();
  }
}
