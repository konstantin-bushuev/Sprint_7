import io.qameta.allure.junit4.DisplayName;
import static java.util.concurrent.TimeUnit.DAYS;
import static org.hamcrest.CoreMatchers.notNullValue;
import model.Order;
import net.datafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import steps.OrderSteps;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class GetOrdersListTest extends BaseTest {

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
    @DisplayName("Проверка получения списка заказов")
    public void getOrderListFull() {
        orderSteps.createOrder(order);

        orderSteps.getOrdersList()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
