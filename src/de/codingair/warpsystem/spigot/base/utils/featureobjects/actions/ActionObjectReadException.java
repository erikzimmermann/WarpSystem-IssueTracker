package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions;

import de.codingair.warpsystem.spigot.features.warps.nextlevel.exceptions.IconReadException;

public class ActionObjectReadException extends IconReadException {
    public ActionObjectReadException(String message) {
        super(message);
    }

    public ActionObjectReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
