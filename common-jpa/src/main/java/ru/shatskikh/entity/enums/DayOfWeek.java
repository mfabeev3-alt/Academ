package ru.shatskikh.entity.enums;

public enum DayOfWeek {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница"),
    SATURDAY("Суббота"),
    SUNDAY("Воскресенье");

    private String description;

    DayOfWeek(String description) {
        this.description = description;
    }

    public static DayOfWeek of(java.time.DayOfWeek javaDay) {
        return DayOfWeek.valueOf(javaDay.name());
    }
    public static DayOfWeek fromValue(String day){

        for(DayOfWeek dayOfWeek: values()) {

            if(dayOfWeek.getDescription().equals(day))
                return  dayOfWeek;
        }

        throw new IllegalArgumentException("Неверный день недели: " + day);
    }

    public String getDescription() {
        return description;
    }
}
