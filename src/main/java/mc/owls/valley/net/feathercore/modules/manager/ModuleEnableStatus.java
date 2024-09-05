package mc.owls.valley.net.feathercore.modules.manager;

/**
 * @brief Each module should return one of following values after enabling phase
 *        was finished.
 * @implNote INIT -> module not enabled yet
 * @implNote SUCCESS -> module enable successfully
 * @implNote FAIL -> module enable failed
 * @implNote OK_NOT_ENABLED -> the module should not be enabled
 */
public enum ModuleEnableStatus {
    INIT, SUCCESS, FAIL, OK_NOT_ENABLED
}
