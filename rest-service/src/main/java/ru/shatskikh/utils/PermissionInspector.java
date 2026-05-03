package ru.shatskikh.utils;

import org.springframework.stereotype.Component;
import ru.shatskikh.entity.Schedule.Professor;
import ru.shatskikh.entity.Schedule.Subject;


@Component
public class PermissionInspector {

    public boolean isPermitted (Long gotGroupId, Long objectGroupId) {

        return gotGroupId == objectGroupId;

    }

}
