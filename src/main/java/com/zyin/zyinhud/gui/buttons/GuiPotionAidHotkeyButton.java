package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.PotionAidKeyHandler;

public class GuiPotionAidHotkeyButton extends GuiHotkeyButton
{
	public GuiPotionAidHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		PotionAidKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return PotionAidKeyHandler.HotkeyDescription;
	}
}
