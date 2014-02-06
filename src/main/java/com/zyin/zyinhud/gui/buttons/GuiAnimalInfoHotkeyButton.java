package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.AnimalInfoKeyHandler;

public class GuiAnimalInfoHotkeyButton extends GuiHotkeyButton
{
	public GuiAnimalInfoHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		AnimalInfoKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return AnimalInfoKeyHandler.HotkeyDescription;
	}
}