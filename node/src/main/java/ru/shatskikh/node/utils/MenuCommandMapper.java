package ru.shatskikh.node.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MenuCommandMapper {

    private final Map<String, String> menuToCommand;


    public MenuCommandMapper() {
        this.menuToCommand = new HashMap<>();
        menuToCommand.put("Какая сейчас пара?", "/current_lesson");
        menuToCommand.put("Расписание на неделю", "/weekly_schedule");
    }

    public String map(String text) {
        return menuToCommand.getOrDefault(text,text);
    }

}
