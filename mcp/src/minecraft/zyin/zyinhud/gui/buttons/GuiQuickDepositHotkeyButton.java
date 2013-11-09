package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.QuickDeposit;

public class GuiQuickDepositHotkeyButton extends GuiHotkeyButton
{
	public GuiQuickDepositHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		QuickDeposit.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return QuickDeposit.HotkeyDescription;
	}
}
