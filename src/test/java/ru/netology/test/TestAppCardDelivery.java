package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class TestAppCardDelivery {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    @Step("Открываем страницу и удаляем плейсхолдер даты")
    void setUp() {
        open("http://localhost:9999/");
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.DELETE);
    }

    @Epic(value = "Форма заказа доставки карты")
    @Feature(value = "Причина-следствие заказа карты и регистрация даты")
    @Story(value = "Успешный план встречи для получения карты")
    @Description(value = "Тестируется заполнение всех полей, чекбоксов, кнопки и" +
            "получения подтверждения успешной даты встречи для выдачи карты.")
    @Test
    @DisplayName("Should successful plan meeting")
    void shouldSuccessfulPlanMeeting() {
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").val(DataGenerator.generateCity());
        $("[data-test-id='date'] input").sendKeys(firstMeetingDate);
        $("[data-test-id='name'] input").val(DataGenerator.generateName());
        $("[data-test-id='phone'] input").val(DataGenerator.generatePhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='success-notification']").shouldHave(text("Встреча успешно запланирована на "
                + firstMeetingDate));
    }

    @Epic(value = "Форма заказа доставки карты")
    @Feature(value = "Причина-следствие заказа карты и регистрация даты")
    @Story(value = "Успешный план встречи для получения карты. " +
            "Перепланирование встречи на другую дату")
    @Description(value = "Тестируется заполнение всех полей, чекбоксов, кнопки,  " +
            "получения подтверждения успешной даты встречи для выдачи карты " +
            "и перерегистрация на другую дату")
    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id='city'] input").val(DataGenerator.generateCity());
        $("[data-test-id='date'] input").sendKeys(firstMeetingDate);
        $("[data-test-id='name'] input").val(DataGenerator.generateName());
        $("[data-test-id='phone'] input").val(DataGenerator.generatePhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='success-notification']").shouldHave(text("Встреча успешно запланирована на "
                + firstMeetingDate));
        $(".button").click();
        $("[data-test-id='replan-notification']").shouldHave(text("У вас уже запланирована встреча на другую дату." +
                " Перепланировать?"));
        $(byText("Перепланировать")).click();
        $("[data-test-id='success-notification']").shouldHave(text("Встреча успешно запланирована на "
                + secondMeetingDate));
    }
}