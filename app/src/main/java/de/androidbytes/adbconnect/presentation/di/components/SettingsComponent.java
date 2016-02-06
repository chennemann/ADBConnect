package de.androidbytes.adbconnect.presentation.di.components;


/**
 * Created by Christoph on 04.10.2015.
 */
import dagger.Component;
import de.androidbytes.adbconnect.presentation.di.OperatorScope;
import de.androidbytes.adbconnect.presentation.di.modules.ActivityModule;
import de.androidbytes.adbconnect.presentation.view.fragment.SettingsFragment;


/**
 * A scope {@link OperatorScope} component.
 * Injects user specific Fragments.
 */
@OperatorScope
@Component(
            dependencies = ApplicationComponent.class,
            modules = {ActivityModule.class}
)
public interface SettingsComponent {
    void inject(SettingsFragment settingsFragment);
}
