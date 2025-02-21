package ru.netology.delivery;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    @BeforeEach
    void setup() {
        Configuration.headless = true;
        open("http://localhost:9999");
    }

    private void fillForm(DataGenerator.UserInfo user, String date) {
        // Проверка видимости и заполнение полей с увеличенными таймаутами
        $("[data-test-id=city] input")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .setValue(user.getCity());
        
        $("[data-test-id=date] input")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        
        $("[data-test-id=name] input")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .setValue(user.getName());
        
        $("[data-test-id=phone] input")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .setValue(user.getPhone());
        
        $("[data-test-id=agreement]").click();
        $("button.button").click();
    }

    @Test
    @DisplayName("Успешное планирование встречи с валидными данными")
    void shouldSuccessfulPlanMeetingWithValidData() {
        var user = DataGenerator.Registration.generateUser("ru");
        String meetingDate = DataGenerator.generateDate(5);

        fillForm(user, meetingDate);
        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + meetingDate), 
                Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Успешное планирование встречи с именем, содержащим дефис")
    void shouldSuccessfulPlanMeetingWithHyphenatedName() {
        var user = DataGenerator.Registration.generateUser("ru");
        String meetingDate = DataGenerator.generateDate(6);

        fillForm(user, meetingDate);
        $("[data-test-id=name] input").setValue("Иванов-Петров Иван");
        $("button.button").click();

        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + meetingDate), 
                Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Успешное планирование встречи с городом, содержащим букву 'ё'")
    void shouldSuccessfulPlanMeetingWithCityContainingYo() {
        var user = DataGenerator.Registration.generateUser("ru");
        user = new DataGenerator.UserInfo("Орёл", user.getName(), user.getPhone());
        String meetingDate = DataGenerator.generateDate(7);

        fillForm(user, meetingDate);
        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + meetingDate), 
                Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Успешное планирование встречи с минимальным количеством дней вперед")
    void shouldSuccessfulPlanMeetingWithMinimumDaysAhead() {
        var user = DataGenerator.Registration.generateUser("ru");
        String meetingDate = DataGenerator.generateDate(3);

        fillForm(user, meetingDate);
        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + meetingDate), 
                Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Ошибка при вводе невалидного города")
    void shouldShowErrorIfCityIsInvalid() {
        var user = DataGenerator.Registration.generateUser("ru");
        String meetingDate = DataGenerator.generateDate(4);

        fillForm(user, meetingDate);
        $("[data-test-id=city] input").setValue("Invalid City");
        $("button.button").click();

        // Диагностика
        System.out.println("Страница: " + WebDriverRunner.url());
        $("[data-test-id=city] .input__sub")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .shouldHave(Condition.text("Доставка в выбранный город недоступна"));
    }

    @Test
    @DisplayName("Ошибка при незаполненном чекбоксе согласия")
    void shouldShowErrorIfAgreementIsNotChecked() {
        var user = DataGenerator.Registration.generateUser("ru");
        String meetingDate = DataGenerator.generateDate(4);

        // Заполнение формы без чекбокса
        fillFormWithoutAgreement(user, meetingDate);
    
        // Диагностика
        System.out.println("Страница ДО отправки: " + WebDriverRunner.source());
        $("button.button").click();
        System.out.println("Страница ПОСЛЕ отправки: " + WebDriverRunner.source());

        // Проверка ошибки с новым селектором
        $("[data-test-id=agreement].input_invalid")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .$(".checkbox__text")
                .shouldHave(Condition.exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
        }

        private void fillFormWithoutAgreement(UserInfo user, String date) {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        }

    @Test
    @DisplayName("Ошибка при вводе невалидной даты (менее 3 дней вперед)")
    void shouldShowErrorIfDateIsInvalid() {
        var user = DataGenerator.Registration.generateUser("ru");
        String invalidDate = DataGenerator.generateDate(2);

        fillForm(user, invalidDate);

        $("[data-test-id=date] .input__sub")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"));
    }


    @Test
    @DisplayName("Успешное перепланирование встречи")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var user = DataGenerator.Registration.generateUser("ru");
        String firstDate = DataGenerator.generateDate(4);
        String secondDate = DataGenerator.generateDate(7);

        // Первая запись
        fillForm(user, firstDate);
        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstDate), 
                Duration.ofSeconds(10));

        // Перепланирование
        $("[data-test-id=date] input")
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(secondDate);
        $("button.button").click();
        
        $("[data-test-id=replan-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату"));
        $("[data-test-id=replan-notification] button").click();

        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondDate), 
                Duration.ofSeconds(10));
    }
}