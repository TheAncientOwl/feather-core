package mc.owls.valley.net.feathercore.api.exception;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException() {
        super();
    }

    public ModuleNotEnabledException(String moduleName) {
        super("Module '" + moduleName + "' is not enabled in configs");
    }

}
