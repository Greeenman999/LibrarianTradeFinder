package de.greenman999.librariantradefinder.compat;
//? if fabric {

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.greenman999.librariantradefinder.gui.ConfigScreen;
import gg.essential.universal.UScreen;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> (UScreen) new ConfigScreen(parent);
    }
}

//? }
