package com.zyin.zyinhud.keyhandlers;

import net.minecraft.client.Minecraft;

import com.zyin.zyinhud.mods.PotionAid;

import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class PotionAidKeyHandler
{
    public static final String HotkeyDescription = "key.zyinhud.potionaid";
    
    private static Minecraft mc = Minecraft.getMinecraft();
    
	public static void Pressed(KeyInputEvent event) 
	{
		if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

		if (PotionAid.Enabled)
            PotionAid.instance.Drink();
	}
}