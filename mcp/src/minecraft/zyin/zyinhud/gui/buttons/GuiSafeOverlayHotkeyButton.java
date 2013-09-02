package zyin.zyinhud.gui.buttons;

import zyin.zyinhud.mods.SafeOverlay;

public class GuiSafeOverlayHotkeyButton extends GuiHotkeyButton
{
	public GuiSafeOverlayHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		SafeOverlay.Hotkey = hotkey;
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return SafeOverlay.HotkeyDescription;
	}
}
