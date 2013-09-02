package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.EnderPearlAid;

public class GuiEnderPearlAidHotkeyButton extends GuiHotkeyButton
{
	public GuiEnderPearlAidHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		EnderPearlAid.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return EnderPearlAid.HotkeyDescription;
	}
}
