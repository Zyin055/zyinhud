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
		mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("zyinhud:" + name), 1.0F));
	}
	
	/**
	 * Plays the sound that a GuiButton makes
	 */
	public static void PlayButtonPress()
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
	}
}
