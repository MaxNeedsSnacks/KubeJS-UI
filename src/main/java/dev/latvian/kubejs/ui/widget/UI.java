package dev.latvian.kubejs.ui.widget;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.ui.ScreenKubeJSUI;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class UI extends Panel
{
	public final ScreenKubeJSUI screen;
	public final Mouse mouse;
	public final List<Widget> allWidgets;

	public UI(ScreenKubeJSUI s)
	{
		screen = s;
		mouse = new Mouse();
		allWidgets = new ArrayList<>();
		setName(s.getTitle());
	}

	@Override
	public UI getUi()
	{
		return this;
	}

	@Override
	public int getX()
	{
		return 0;
	}

	@Override
	public int getY()
	{
		return 0;
	}

	@Override
	public int getWidth()
	{
		return screen.width;
	}

	@Override
	public int getHeight()
	{
		return screen.height;
	}

	// Utils //

	public int textWidth(@Nullable Object o)
	{
		if (o == null)
		{
			return 0;
		}
		else if (o instanceof ITextProperties)
		{
			return screen.getUiFont().width((ITextProperties) o);
		}
		else if (o instanceof IReorderingProcessor)
		{
			return screen.getUiFont().width((IReorderingProcessor) o);
		}
		else if (o instanceof Text)
		{
			return screen.getUiFont().width(((Text) o).component());
		}

		return screen.getUiFont().width(o.toString());
	}
}