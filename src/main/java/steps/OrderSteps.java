package steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Order;
import static io.restassured.RestAssured.given;
public class OrderSteps {

    public static final String ORDER = "/api/v1/orders";
    public static final String TRACK = "/api/v1/orders/track";
    public static final String ACCEPT = "/api/v1/orders/accept/";

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post(ORDER)
                .then();
    }

    @Step("Получение заказа по номеру")
    public ValidatableResponse getOrderByNumber(Order order) {
        return given()
                .body(order)
                .queryParam("t", order.getTrack())
                .when()
                .get(TRACK)
                .then();
    }

    public ValidatableResponse acceptOrder(Order order, Courier courier) {
        return given()
                .body(order)
                .queryParam("courierId", courier.getId())
                .when()
                .put(ACCEPT + (order.getId() != null ? order.getId() : ""))
                .then();
    }

    public ValidatableResponse getOrdersList() {
        return given()
                .get(ORDER)
                .then();
    }
}
