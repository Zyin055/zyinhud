package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.PlayerLocatorKeyHandler;

public class GuiPlayerLocatorHotkeyButton extends GuiHotkeyButton
{
	public GuiPlayerLocatorHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		PlayerLocatorKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return PlayerLocatorKeyHandler.HotkeyDescription;
	}
}
