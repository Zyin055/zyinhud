package com.zyin.zyinhud.gui.buttons;

import com.zyin.zyinhud.keyhandlers.ItemSelectorKeyHandler;
import org.lwjgl.input.Keyboard;

public class GuiItemSelectorHotkeyButton extends GuiHotkeyButton
{
	public GuiItemSelectorHotkeyButton(int id, int x, int y, int width, int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		ItemSelectorKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return ItemSelectorKeyHandler.HotkeyDescription;
	}
}
