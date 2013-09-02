package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.DistanceMeasurer;

public class GuiDistanceMeasurerHotkeyButton extends GuiHotkeyButton
{
	public GuiDistanceMeasurerHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		DistanceMeasurer.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return DistanceMeasurer.HotkeyDescription;
	}
}
