package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;

public abstract class ZyinHUDModBase
{
	protected static final Minecraft mc = Minecraft.getMinecraft();
	protected static final RenderItem itemRenderer = new RenderItem();
}
