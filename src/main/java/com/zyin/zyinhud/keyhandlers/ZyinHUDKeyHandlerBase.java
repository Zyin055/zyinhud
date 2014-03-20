package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

public interface ZyinHUDKeyHandlerBase
{
	/**
	 * The Hotkey Description is used to uniquely identify the hotkey using Minecraft's hotkey system,
	 * and is also used to get the friendly display name from the language file when viewing it in the
	 * Options > Controls menu.
	 */
    public static final String HotkeyDescription = "key.zyinhud.unknown";
    
    static Minecraft mc = Minecraft.getMinecraft();
}