import static java.util.concurrent.TimeUnit.DAYS;
import static org.hamcrest.CoreMatchers.equalTo;
import io.qameta.allure.junit4.DisplayName;
import model.Courier;
import model.Order;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import steps.CourierSteps;
import steps.OrderSteps;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class AcceptOrderTest extends BaseTest {

    private CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private OrderSteps orderSteps = new OrderSteps();
    private Order order;
    Faker faker = new Faker(new Locale("ru"));

    @Before
    public void setUp() {

        courier = new Courier();
        courier
                .setLogin(faker.regexify("[a-z]{6}"))
                .setPassword(faker.regexify("[a-z0-9]{6}"))
                .setFirstName(faker.name().firstName());

        order = new Order();
        order
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setAddress(faker.address().streetAddress())
                .setMetroStation(String.valueOf(ThreadLocalRandom.current().nextInt(1, 10)))
                .setPhone(faker.regexify("\\+7[0-9]{10}"))
                .setRentTime(ThreadLocalRandom.current().nextInt(1, 15))
                .setDeliveryDate(faker.date().future(10, DAYS, "yyyy-MM-dd"))
                .setComment(faker.regexify("[А-Яа-я ]{20}"));
    }

    @Test
    @DisplayName("Проверка принятия заказа: переданы id заказа и id курьера")
    public void acceptOrderAllDataProvided() {
        courierSteps.createCourier(courier);
        Integer courierId = courierSteps.loginCourier(courier)
                .extract().body().path("id");
        courier.setId(courierId);

        Integer track = orderSteps.createOrder(order)
                .extract().body().path("track");
        order.setTrack(track);
        Integer orderId = orderSteps.getOrderByNumber(order)
                .extract().body().path("order.id");
        order.setId(orderId);

        orderSteps.acceptOrder(order, courier)
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверка принятия заказа: передан несуществующий id заказа")
    public void acceptOrderUnexistentOrderIdProvided() {
        courierSteps.createCourier(courier);
        Integer courierId = courierSteps.loginCourier(courier)
                .extract().body().path("id");
        courier.setId(courierId);

        Integer orderId = ThreadLocalRandom.current().nextInt(800000, 900000);
        order.setId(orderId);

        orderSteps.acceptOrder(order, courier)
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }

    @Test
    @DisplayName("Проверка принятия заказа: передан несуществующий id курьера")
    public void acceptOrderUnexistentCourierIdProvided() {
        Integer courierId = ThreadLocalRandom.current().nextInt(800000, 900000);
        courier.setId(courierId);

        Integer track = orderSteps.createOrder(order)
                .extract().body().path("track");
        order.setTrack(track);
        Integer orderId = orderSteps.getOrderByNumber(order)
                .extract().body().path("order.id");
        order.setId(orderId);

        orderSteps.acceptOrder(order, courier)
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Проверка принятия заказа: не передан id курьера")
    public void acceptOrderNoCourierIdProvided() {
        Integer track = orderSteps.createOrder(order)
                .extract().body().path("track");
        order.setTrack(track);
        Integer orderId = orderSteps.getOrderByNumber(order)
                .extract().body().path("order.id");
        order.setId(orderId);

        orderSteps.acceptOrder(order, courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));

    }

    @Test
    @DisplayName("Проверка принятия заказа: не передан id заказа")
    public void acceptOrderNoOrderIdProvided() {
        courierSteps.createCourier(courier);
        Integer courierId = courierSteps.loginCourier(courier)
                .extract().body().path("id");
        courier.setId(courierId);

        orderSteps.acceptOrder(order, courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
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
