package com.zyin.zyinhud.gui.buttons;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.keyhandlers.WeaponSwapperKeyHandler;

public class GuiWeaponSwapperHotkeyButton extends GuiHotkeyButton
{
	public GuiWeaponSwapperHotkeyButton(int id, int x, int y, int width,int height, String hotkey)
	{
		super(id, x, y, width, height, hotkey);
	}

	@Override
	protected void SetHotkey(String hotkey)
	{
		WeaponSwapperKeyHandler.Hotkey = Keyboard.getKeyIndex(hotkey);
	}

	@Override
	protected String GetHotkeyDescription()
	{
		return WeaponSwapperKeyHandler.HotkeyDescription;
	}
}
