package com.zyin.zyinhud;

import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zyin.zyinhud.gui.GuiZyinHUDOptions;

/**
 * Used to capture GUI Events for vanilla screens in order to add custom functionality to them.
 */
public class ZyinHUDGuiEvents
{
	public static final ZyinHUDGuiEvents instance = new ZyinHUDGuiEvents();
    
    //Used to inject new buttons into vanilla GUIs
	@SubscribeEvent
	public void InitGuiEventPost(InitGuiEvent.Post event)
	{
		GuiZyinHUDOptions.InitGuiEventPost(event);
	}
	
	//Used to capture when a custom button is clicked in a vanilla GUI
	@SubscribeEvent
	public void ActionPerformedEventPost(ActionPerformedEvent.Post event)
	{
		GuiZyinHUDOptions.ActionPerformedEventPost(event);
	}
}
