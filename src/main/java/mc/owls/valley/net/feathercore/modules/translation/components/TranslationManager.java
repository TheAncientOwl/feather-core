package mc.owls.valley.net.feathercore.modules.translation.components;

import mc.owls.valley.net.feathercore.api.configuration.IConfigFile;
import mc.owls.valley.net.feathercore.api.core.FeatherModule;
import mc.owls.valley.net.feathercore.api.core.IFeatherCoreProvider;
import mc.owls.valley.net.feathercore.api.exception.FeatherSetupException;
import mc.owls.valley.net.feathercore.api.module.interfaces.ITranslationManager;

public class TranslationManager extends FeatherModule implements ITranslationManager {

    public TranslationManager(String name) {
        super(name);
    }

    @Override
    protected void onModuleEnable(IFeatherCoreProvider core) throws FeatherSetupException {
    }

    @Override
    protected void onModuleDisable() {
    }

    @Override
    public IConfigFile getTranslation(final String language) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTranslation'");
    }

}
