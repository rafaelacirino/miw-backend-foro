package es.upm.miw.foro.persistance.model;

public enum Role {

    ADMIN, MEMBER;

    public static final String PREFIX = "ROLE_";

    public static Role of(String withPrefix) {
        return Role.valueOf(withPrefix.replace(Role.PREFIX, ""));
    }
}
