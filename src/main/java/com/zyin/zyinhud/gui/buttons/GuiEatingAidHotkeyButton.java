package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.EatingAidKeyHandler;

public class GuiEatingAidHotkeyButton extends GuiHotkeyButton
{
	public GuiEatingAidHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		EatingAidKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return EatingAidKeyHandler.HotkeyDescription;
	}
}
