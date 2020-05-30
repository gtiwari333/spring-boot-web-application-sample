package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T> {

    public SelenideElement body() {
        return $("body");
    }

    public abstract T open();

}
