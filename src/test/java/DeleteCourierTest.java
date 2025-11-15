import static org.hamcrest.CoreMatchers.equalTo;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import model.Courier;
import net.datafaker.Faker;
import org.junit.Test;
import org.junit.Before;
import steps.CourierSteps;

import java.util.concurrent.ThreadLocalRandom;

@Epic("Функционал курьера")
@Feature("Удалить курьера")
public class DeleteCourierTest extends BaseTest {

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
    @DisplayName("Проверка удаления курьера: передан существующий id")
    public void deleteCourierExistentIdProvided() {
        Integer id = courierSteps.loginCourier(courier)
                .extract().body().path("id");
        courier.setId(id);

        courierSteps.deleteCourier(courier)
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверка удаления курьера: передан несуществующий id")
    public void deleteCourierNonExistentIdProvided() {
        Integer id = ThreadLocalRandom.current().nextInt(800000, 900000);
        courier.setId(id);

        courierSteps.deleteCourier(courier)
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id нет"));
    }

    @Test
    @DisplayName("Проверка удаления курьера: не передан id")
    public void deleteCourierNoIdProvided() {
        courierSteps.createCourier(courier);
        courierSteps.deleteCourier(courier)
                .statusCode(400)
                .body("ok", equalTo("Недостаточно данных для удаления курьера"));
    }
}
