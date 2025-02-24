package es.upm.miw.foro.persistance.model;

public enum Role {

    ADMIN, MANAGER, OPERATOR, CUSTOMER, AUTHENTICATED;

    public static final String PREFIX = "ROLE_";

    public static Role of(String withPrefix) {
        return Role.valueOf(withPrefix.replace(Role.PREFIX, ""));
    }
}
