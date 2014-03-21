package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;

public abstract class ZyinHUDModBase
{
	protected static final Minecraft mc = Minecraft.getMinecraft();
	protected static final RenderItem itemRenderer = new RenderItem();
	
	//We can't move the static variable Enabled to this base mod because then if one mod sets it to false
	//then ALL mods will be set to false
	
	//We can't move the enum Modes here because enum cannot values can only be added where it is declared
}
