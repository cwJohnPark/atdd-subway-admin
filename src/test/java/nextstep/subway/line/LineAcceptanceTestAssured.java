package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_식별자;

import java.util.List;
import java.util.Map;

import org.apache.groovy.util.Maps;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LineAcceptanceTestAssured {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String DEFAULT_COLOR = "bg-red-600";
    public static final String UP_STATION_ID = "upStationId";
    public static final String DOWN_STATION_ID = "downStationId";
    public static final String REQUEST_PATH = "/lines";

    public static ExtractableResponse<Response> 지하철_노선_생성(String 노선_이름, String 상행역_이름, String 하행역_이름) {
        Long 상행역_아이디 = 지하철역_식별자(지하철역_생성(상행역_이름));
        Long 하행역_아이디 = 지하철역_식별자(지하철역_생성(하행역_이름));

        return RestAssured.given().log().all()
            .body(지하철_노선_파라미터(노선_이름, DEFAULT_COLOR, 상행역_아이디, 하행역_아이디))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(REQUEST_PATH)
            .then().log().all()
            .extract();
    }

    public static List<String> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .when().get(REQUEST_PATH)
            .then().log().all()
            .extract().jsonPath().getList(NAME, String.class);
    }

    public static String 지하철_노선_조회(ExtractableResponse<Response> 지하철_노선_생성_응답) {
        return RestAssured.given().log().all()
            .when().get(REQUEST_PATH + "/" + 지하철_노선_식별자(지하철_노선_생성_응답))
            .then().log().all()
            .extract().jsonPath().getString(NAME);
    }

    private static Map<String, String> 지하철_노선_파라미터(String 노선_이름, String 노선_색상, Long 상행역_아이디, Long 하행역_아이디) {
        return Maps.of(NAME, 노선_이름, COLOR, 노선_색상, UP_STATION_ID, 상행역_아이디 + "", DOWN_STATION_ID, 하행역_아이디 + "");
    }

    private static long 지하철_노선_식별자(ExtractableResponse<Response> 지하철_노선_생성_응답) {
        return 지하철_노선_생성_응답.jsonPath().getLong(ID);
    }
}
