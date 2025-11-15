import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import static java.util.concurrent.TimeUnit.DAYS;
import static org.hamcrest.CoreMatchers.notNullValue;
import model.Order;
import net.datafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import steps.OrderSteps;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@RunWith(Parameterized.class)
@Epic("Функционал заказа")
@Feature("Создать заказ")
public class CreateOrderTest extends BaseTest {

    private OrderSteps orderSteps = new OrderSteps();
    private Order order;
    Faker faker = new Faker(new Locale("ru"));

    private final String[] colors;

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

    public CreateOrderTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Тестовые данные: цвет {0}")
    public static Object[][] getParameters() {
        return new Object[][]{
                {new String[] {"BLACK"}},
                {new String[] {"GREY"}},
                {new String[] {"BLACK", "GREY"}},
                {new String[] {}},
        };
    }

    @Test
    @DisplayName("Проверка создания заказа: с возможными вариантами выбора цвета")
    public void createOrderDependOnColorsProvided() {
        order.setColor(colors);
        orderSteps
                .createOrder(order)
                .statusCode(201)
                .body("track", notNullValue());
    }
}
