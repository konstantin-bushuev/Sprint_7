import static org.hamcrest.CoreMatchers.equalTo;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import model.Courier;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import steps.CourierSteps;
import java.util.Locale;

@Epic("Функционал курьера")
@Feature("Создать курьера")
public class CreateCourierTest extends BaseTest{

    private CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    Faker faker = new Faker(new Locale("ru"));

    @Before
    public void setUp() {
        courier = new Courier();
        courier
                .setLogin(faker.regexify("[a-z]{6}"))
                .setPassword(faker.regexify("[a-z0-9]{6}"))
                .setFirstName(faker.name().firstName());
    }

    @Test
    @DisplayName("Проверка создения курьера: переданы логин, пароль, имя")
    public void createCourierAllDataProvided() {
        courierSteps
                .createCourier(courier)
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверка создения курьера: одинаковые данные переданы дважды")
    public void createCourierSameDataProvidedTwice() {
        courierSteps
                .createCourier(courier);

        courierSteps
                .createCourier(courier)
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Проверка создения курьера: не передан логин ")
    public void createCourierNoLoginProvided() {
        courier.setLogin(null);
        courierSteps
                .createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверка создения курьера: не передан пароль ")
    public void createCourierNoPasswordProvided() {
        courier.setPassword(null);
        courierSteps
                .createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        Integer id = null;
        try {
            id = courierSteps.loginCourier(courier)
                    .extract().body().path("id");
        } catch (Exception e) {
        }

        if (id != null) {
            courier.setId(id);
            courierSteps.deleteCourier(courier);
        }
    }
}






