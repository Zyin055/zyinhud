package com.zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ZyinHUDSound
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	
	/**
	 * Plays a zyinhud sound with the given resource name.
	 * @param name
	 */
	public static void PlaySound(String name)
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("zyinhud:" + name), 1.0F));
	}
	
	/**
	 * Plays the sound that a GuiButton makes.
	 */
	public static void PlayButtonPress()
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
	}
	
	/**
	 * Plays the "plop" sound that a chicken makes when laying an egg.
	 */
	public static void PlayPlopSound()
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("mob.chicken.plop"), 1.0F));
	}
}
