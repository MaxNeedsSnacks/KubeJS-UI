package dev.latvian.kubejs.ui.forge;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

import java.util.function.Consumer;

import static dev.latvian.kubejs.ui.VanillaActions.*;

public interface ForgeActions
{
	Consumer<Screen> FORGE_MOD_LIST = screen -> mc().setScreen(new ModListScreen(screen));
}
