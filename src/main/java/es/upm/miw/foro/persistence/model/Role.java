package es.upm.miw.foro.persistence.model;

public enum Role {

    ADMIN, MEMBER, UNKNOWN;

    public static final String PREFIX = "ROLE_";

    public static Role of(String withPrefix) {
        return Role.valueOf(withPrefix.replace(Role.PREFIX, ""));
    }
}
