package com.zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ZyinHUDSound
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	/**
	 * Players a sound with the given name
	 * @param name
	 */
	public static void PlaySound(String name)
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("zyinhud:" + name)));
	}
	
	/**
	 * Playes the sound that a GuiButton makes
	 */
	public static void PlayButtonPress()
	{
		//func_147673_a plays at 100% volume
		//mc.getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("gui.button.press")));
		
		//func_147674_a plays at 25% volume
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1f));
	}
}
