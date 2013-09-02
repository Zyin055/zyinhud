package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.PotionAid;

public class GuiPotionAidHotkeyButton extends GuiHotkeyButton
{
	public GuiPotionAidHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		PotionAid.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return PotionAid.HotkeyDescription;
	}
}
