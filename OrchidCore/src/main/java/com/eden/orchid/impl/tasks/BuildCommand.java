package com.eden.orchid.impl.tasks;

import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.tasks.OrchidCommand;
import com.google.inject.Provider;

import javax.inject.Inject;

public final class BuildCommand extends OrchidCommand {

    private final Provider<OrchidContext> contextProvider;

    @Inject
    public BuildCommand(Provider<OrchidContext> contextProvider) {
        super(100);
        this.contextProvider = contextProvider;
    }

    @Override
    public boolean matches(String commandName) {
        return commandName.equalsIgnoreCase("build");
    }

    @Override
    public String[] parameters() {
        return new String[0];
    }

    @Override
    public void run(String commandName) throws Exception {
        contextProvider.get().build();
    }
}

