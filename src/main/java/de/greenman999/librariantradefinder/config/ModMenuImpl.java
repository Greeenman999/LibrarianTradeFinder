package de.greenman999.librariantradefinder.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.greenman999.librariantradefinder.screens.ControlUi;


public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<ControlUi> getModConfigScreenFactory() {
        return ControlUi::new;
    }
}
