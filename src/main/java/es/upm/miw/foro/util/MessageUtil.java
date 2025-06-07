package es.upm.miw.foro.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtil {

    public static final String PASSWORD_MESSAGE = "Password must be at least 8 characters, contain at least one " +
                                                  "uppercase letter, one lowercase letter, one digit, and one " +
                                                  "special character.";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String QUESTION_NOT_FOUND_WITH_ID = "Question not found with id: ";
    public static final String QUESTION_NOT_FOUND = "Question not found";
    public static final String ANSWER_NOT_FOUND = "Answer not found with id: ";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
    public static final String USER_ID = "User with id ";

    public static final String FIRSTNAME_CLAIM = "firstName";
    public static final String LASTNAME_CLAIM = "lastName";
    public static final String EMAIL_CLAIM = "email";
    public static final String ROLE_CLAIM = "role";
    public static final String PASSWORD_RESET_CLAIM = "pwd_reset";
    public static final String NOT_FOUND = " not found";
    public static final String MESSAGE = "message";
    public static final String AUTHORIZATION = "Authorization";
}