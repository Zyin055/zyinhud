package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.WeaponSwapper;

public class GuiWeaponSwapperHotkeyButton extends GuiHotkeyButton
{
	public GuiWeaponSwapperHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		WeaponSwapper.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return WeaponSwapper.HotkeyDescription;
	}
}
