package ru.shatskikh.entity.enums;

public enum DayOfWeek {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private String description;

    DayOfWeek(String description) {
        this.description = description;
    }

    DayOfWeek fromValue(String day){

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
