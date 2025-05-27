package es.upm.miw.foro.util;

public class ApiPath {

    private ApiPath() {
    }

    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String SWAGGER_API_DOCS = "/v3/api-docs/**";
    public static final String ROOT = "/";

    public static final String USERS = "/users";
    public static final String USER_REGISTER = USERS + "/register";
    public static final String USER_LOGIN = USERS + "/login";

    public static final String QUESTIONS = "/questions";
    public static final String QUESTION_ID = QUESTIONS + "/{id}";
    public static final String QUESTION_SEARCH = QUESTIONS + "/search";
    public static final String QUESTION_MY = QUESTIONS + "/my";
    public static final String QUESTION_VIEWS = QUESTION_ID + "/views";

    public static final String ANSWERS = "/answers";
    public static final String ANSWERS_BY_QUESTION_ID = ANSWERS + "/{id}";

    public static final String TAGS = "/tags";

    public static final String NOTIFICATIONS = "/notifications";
    public static final String TOPIC_NOTIFICATIONS = "/topic/notifications/";

    public static final String ACTUATOR = "/actuator/**";

    public static final String ACCOUNT_FORGOT_PASSWORD = "/account/forgot-password";
    public static final String ACCOUNT_RESET_PASSWORD = "/account/reset-password";


}
