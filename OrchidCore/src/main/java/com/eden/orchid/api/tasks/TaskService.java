package com.eden.orchid.api.tasks;

import com.eden.orchid.api.OrchidService;

public interface TaskService extends OrchidService {

    default boolean runTask(String taskName) {
        return getService(TaskService.class).runTask(taskName);
    }

    default boolean runCommand(String commandName, String parameters) {
        return getService(TaskService.class).runCommand(commandName, parameters);
    }

    default void build() {
        getService(TaskService.class).build();
    }

    default void watch() {
        getService(TaskService.class).watch();
    }

    default void serve() {
        getService(TaskService.class).serve();
    }
}
