import static java.util.concurrent.TimeUnit.DAYS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import model.Order;
import net.datafaker.Faker;
import org.junit.Test;
import org.junit.Before;
import steps.OrderSteps;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Epic("Функционал заказа")
@Feature("Получить заказ по номеру")
public class GetOrderByNumberTest extends BaseTest{

    private OrderSteps orderSteps = new OrderSteps();
    private Order order;
    Faker faker = new Faker(new Locale("ru"));

    @Before
    public void setUp() {
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
    @DisplayName("Проверка получения заказа по номеру: передан существующий номер")
    public void getOrderByNumberTrackProvided() {
        Integer track = orderSteps.createOrder(order).extract().body().path("track");
        order.setTrack(track);

        orderSteps.getOrderByNumber(order)
                .statusCode(200)
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Проверка получения заказа по номеру: передан несуществующий номер")
    public void getOrderByNumberUnexistentTrackProvided() {
        Integer track = ThreadLocalRandom.current().nextInt(8000000, 9000000);

        order.setTrack(track);

        orderSteps.getOrderByNumber(order)
                .statusCode(404)
                .body("message", equalTo("Заказ не найден"));
    }

    @Test
    @DisplayName("Проверка получения заказа по номеру: номер не передан")
    public void getOrderByNumberNoTrackProvided() {
        orderSteps.createOrder(order);

        orderSteps.getOrderByNumber(order)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }
}
