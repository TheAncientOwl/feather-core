package mc.owls.valley.net.feathercore.modules.manager.exceptions;

public class ModuleSetupException extends Exception {
    public ModuleSetupException() {
        super();
    }

    public ModuleSetupException(String message) {
        super(message);
    }

    public ModuleSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleSetupException(Throwable cause) {
        super(cause);
    }
}
