package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.CoordinatesKeyHandler;

public class GuiCoordinatesHotkeyButton extends GuiHotkeyButton
{
	public GuiCoordinatesHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		CoordinatesKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return CoordinatesKeyHandler.HotkeyDescription;
	}
}
