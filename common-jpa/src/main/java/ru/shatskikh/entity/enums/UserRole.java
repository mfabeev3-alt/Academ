package ru.shatskikh.entity.enums;

public enum UserRole {
    ROLE_ADMIN(4),
    ROLE_MODERATOR(3),
    ROLE_LEADER(2),
    ROLE_STUDENT(1),
    ROLE_GUEST(0);

    private final int level;

    UserRole(int level) {
        this.level = level;
    }

   public boolean hasAccess(UserRole requiredRole) {
        return this.level >= requiredRole.level;
    }

    public int getLevel() {
        return level;
    }
}
