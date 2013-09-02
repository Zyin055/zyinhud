package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.PlayerLocator;

public class GuiPlayerLocatorHotkeyButton extends GuiHotkeyButton
{
	public GuiPlayerLocatorHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		PlayerLocator.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return PlayerLocator.HotkeyDescription;
	}
}
