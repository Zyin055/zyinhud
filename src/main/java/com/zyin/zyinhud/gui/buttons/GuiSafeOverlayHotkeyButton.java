package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.SafeOverlayKeyHandler;

public class GuiSafeOverlayHotkeyButton extends GuiHotkeyButton
{
	public GuiSafeOverlayHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		SafeOverlayKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return SafeOverlayKeyHandler.HotkeyDescription;
	}
}
