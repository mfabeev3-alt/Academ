package ru.shatskikh.node.utils.enums;

public enum Buttons {
    APPROVE_USER("registration_approve_user"),
    DECLINE_USER("registration_decline_user");

    final String value;

    Buttons(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
