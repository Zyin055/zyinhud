package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.EatingAid;

public class GuiEatingAidHotkeyButton extends GuiHotkeyButton
{
	public GuiEatingAidHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		EatingAid.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return EatingAid.HotkeyDescription;
	}
}
