package mc.owls.valley.net.feathercore.api.module.interfaces;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;

public interface ITranslationManager {
    public IConfigFile getTranslation(final String language);
}
