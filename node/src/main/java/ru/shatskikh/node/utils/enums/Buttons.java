package ru.shatskikh.node.utils.enums;

public enum Buttons {
    APPROVE_USER("approve_user"),
    DECLINE_USER("decline_user");

    final String value;

    Buttons(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
