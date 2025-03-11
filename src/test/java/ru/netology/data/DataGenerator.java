package ru.netology.data;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {
    private DataGenerator() {
    }

    private static final Faker faker = new Faker(new Locale("ru"));

    @Step("Генерируем актуальную дату с запасом в 4 дня в формате д.м.г")
    public static String generateDate(int shift) {
        return LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Step("Фейкеом генерируем рандомные города")
    public static String generateCity() {
        return faker.address().cityName();
    }

    @Step("Фейкеом генерируем рандомные имена")
    public static String generateName() {
        return faker.name().fullName();
    }

    @Step("Фейкеом генерируем рандомные номера телефонов из 11 цифр")
    public static String generatePhone() {
        return faker.number().digits(11);
    }

    @Step("Генерируем всю рандомную информацию о пользователе: город, имя, телефон")
    public static UserInfo generateUser(String locale) {
        return new UserInfo(generateCity(), generateName(), generatePhone());
    }
}