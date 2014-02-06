package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.EnderPearlAidKeyHandler;

public class GuiEnderPearlAidHotkeyButton extends GuiHotkeyButton
{
	public GuiEnderPearlAidHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		EnderPearlAidKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return EnderPearlAidKeyHandler.HotkeyDescription;
	}
}
