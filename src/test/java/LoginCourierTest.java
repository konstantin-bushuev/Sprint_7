import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import model.Courier;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import steps.CourierSteps;

@Epic("Функционал курьера")
@Feature("Логин курьера в систему")
public class LoginCourierTest extends BaseTest{

    private CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    Faker faker = new Faker();

    @Before
    public void setUp() {
        courier = new Courier();
        courier
                .setLogin(faker.regexify("[a-z]{6}"))
                .setPassword(faker.regexify("[a-z0-9]{6}"));

        courierSteps.createCourier(courier);
    }

    @Test
    @DisplayName("Проверка логина курьера в систему: переданы логин и пароль")
    public void loginCourierAllDataProvided() {
        courierSteps
                .loginCourier(courier)
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Проверка логина курьера в систему: не передан логин")
    public void loginCourierNoLoginProvided() {
        courier.setLogin(null);
        courierSteps
                .loginCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверка логина курьера в систему: не передан пароль")
    public void loginCourierNoPasswordProvided() {
        courier.setPassword(null);
        courierSteps
                .loginCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверка логина курьера в систему: передан неверный логин")
    public void loginCourierUnexistentLoginProvided() {
        courier.setLogin(faker.regexify("[a-z]{6}"));
        courierSteps
                .loginCourier(courier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверка логина курьера в систему: передан неверный пароль")
    public void loginCourierUnexistentPasswordProvided() {
        courier.setPassword(faker.regexify("[a-z0-9]{6}"));
        courierSteps
                .loginCourier(courier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
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


