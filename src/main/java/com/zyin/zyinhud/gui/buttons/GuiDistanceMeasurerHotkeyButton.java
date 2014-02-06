package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.DistanceMeasurerKeyHandler;

public class GuiDistanceMeasurerHotkeyButton extends GuiHotkeyButton
{
	public GuiDistanceMeasurerHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		DistanceMeasurerKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return DistanceMeasurerKeyHandler.HotkeyDescription;
	}
}
