package dev.latvian.kubejs.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.client.KubeJSClient;
import dev.latvian.kubejs.ui.widget.UI;
import dev.latvian.kubejs.ui.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public final class ScreenKubeJSUI extends Screen
{
	public final String screenId;
	public final Screen original;
	public final Consumer<UI> consumer;
	public final int forcedScale;
	public final UI ui;
	public final Map<ResourceLocation, Optional<EffectInstance>> shaders;

	public ScreenKubeJSUI(String i, Screen o, Consumer<UI> c, int fs)
	{
		super(o.getTitle());
		screenId = i;
		original = o;
		consumer = c;
		forcedScale = fs;
		ui = new UI(this);
		shaders = new HashMap<>();
	}

	@Override
	public void init(Minecraft mc, int w, int h)
	{
		original.init(mc, w, h);
		super.init(mc, w, h);
	}

	@Override
	public void init()
	{
		ui.children.clear();
		ui.allWidgets.clear();
		consumer.accept(ui);
		ui.collectWidgets(ui.allWidgets);

		for (Widget w : ui.allWidgets)
		{
			w.actualX = w.getX();
			w.actualY = w.getY();
		}
	}

	public Font getUiFont()
	{
		return font;
	}

	@Override
	public boolean shouldCloseOnEsc()
	{
		return original.shouldCloseOnEsc();
	}

	@Override
	public boolean isPauseScreen()
	{
		return original.isPauseScreen();
	}

	public void clearCaches()
	{
		for (Optional<EffectInstance> instance : shaders.values())
		{
			try
			{
				instance.ifPresent(EffectInstance::close);
			}
			catch (Exception ex)
			{
			}
		}

		shaders.clear();
	}

	@Override
	public void removed()
	{
		clearCaches();
		super.removed();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		original.renderBackground(matrixStack);
		ui.mouse.x = mouseX;
		ui.mouse.y = mouseY;
		ui.time = System.currentTimeMillis() - UI.startTime;

		for (Widget w : ui.allWidgets)
		{
			boolean b = w.isMouseOver;
			w.isMouseOver = mouseX >= w.actualX && mouseY >= w.actualY && mouseX < w.actualX + w.getWidth() && mouseY < w.actualY + w.getHeight();

			if (b != w.isMouseOver)
			{
				if (w.isMouseOver)
				{
					if (w.mouseEnter != null)
					{
						w.mouseEnter.run();
					}
				}
				else if (w.mouseExit != null)
				{
					w.mouseExit.run();
				}
			}
		}

		ui.renderBackground(matrixStack, partialTicks);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		ui.renderForeground(matrixStack, partialTicks);

		List<Component> list = new ArrayList<>();
		ui.appendHoverText(list);

		if (!list.isEmpty())
		{
			renderComponentTooltip(matrixStack, list, mouseX, mouseY);
			// GuiUtils.drawHoveringText(matrixStack, list, mouseX, mouseY, width, height, 180, font);
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button)
	{
		if (ui.mousePressed())
		{
			return true;
		}

		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseReleased(double x, double y, int button)
	{
		ui.mouseReleased();
		return super.mouseReleased(x, y, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if (keyCode == GLFW.GLFW_KEY_F5)
		{
			ui.tick = 0;

			if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0)
			{
				KubeJSClient.reloadClientScripts();
				minecraft.setScreen(this);
			}
			else
			{
				UI.startTime = System.currentTimeMillis();
				ui.time = 0L;
			}

			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public Optional<EffectInstance> loadShader(ResourceLocation id)
	{
		Optional<EffectInstance> instance = shaders.get(id);

		if (instance != null)
		{
			return instance;
		}

		try
		{
			instance = Optional.of(new EffectInstance(Minecraft.getInstance().getResourceManager(), id.toString()));
		}
		catch (Exception ex)
		{
			instance = Optional.empty();
			KubeJS.LOGGER.error("Failed to load shader " + id + ":");
			ex.printStackTrace();
		}

		shaders.put(id, instance);
		return instance;
	}

	@Override
	public void tick()
	{
		super.tick();
		ui.tick++;
	}

	public Minecraft getMinecraft()
	{
		return minecraft;
	}
}