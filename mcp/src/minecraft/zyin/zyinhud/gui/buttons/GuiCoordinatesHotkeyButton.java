package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.Coordinates;

public class GuiCoordinatesHotkeyButton extends GuiHotkeyButton
{
	public GuiCoordinatesHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		Coordinates.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return Coordinates.HotkeyDescription;
	}
}
