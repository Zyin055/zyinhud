package com.zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.zyin.zyinhud.helper.HUDEntityTrackerHelper;
import com.zyin.zyinhud.helper.RenderEntityTrackerHelper;
import com.zyin.zyinhud.mods.AnimalInfo;
import com.zyin.zyinhud.mods.DistanceMeasurer;
import com.zyin.zyinhud.mods.DurabilityInfo;
import com.zyin.zyinhud.mods.InfoLine;
import com.zyin.zyinhud.mods.ItemSelector;
import com.zyin.zyinhud.mods.PotionTimers;
import com.zyin.zyinhud.mods.SafeOverlay;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is in charge of rendering things onto the HUD and into the game world.
 */
public class ZyinHUDRenderer
{
	public static ZyinHUDRenderer instance = new ZyinHUDRenderer();
	private static Minecraft mc = Minecraft.getMinecraft();
	
	/**
	 * Event fired at various points during the GUI rendering process.
	 * We render anything that need to be rendered onto the HUD in this method.
	 * @param event
	 */
    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event)
    {
    	//render everything onto the screen
    	if(event.type == RenderGameOverlayEvent.ElementType.TEXT)
    	{
    		InfoLine.RenderOntoHUD();
    		DistanceMeasurer.RenderOntoHUD();
            DurabilityInfo.RenderOntoHUD();
            PotionTimers.RenderOntoHUD();
            HUDEntityTrackerHelper.RenderEntityInfo(event.partialTicks);	//calls other mods that need to render things on the HUD near entities
            ItemSelector.RenderOntoHUD(event.partialTicks);
    	}
    	else if(event.type == RenderGameOverlayEvent.ElementType.DEBUG)
    	{
            AnimalInfo.RenderOntoDebugMenu();
    	}
    	
    	
    	//change how the inventories are rendered (this has to be done on every game tick)
    	if (mc.currentScreen instanceof InventoryEffectRenderer)
    	{
    		PotionTimers.DisableInventoryPotionEffects((InventoryEffectRenderer)mc.currentScreen);
    	}
    }
    

    /**
     * Event fired when the world gets rendered.
     * We render anything that need to be rendered into the game world in this method.
     * @param event
     */
    @SubscribeEvent
    public void RenderWorldLastEvent(RenderWorldLastEvent event)
    {
        //render unsafe positions (cache calculations are done from this render method)
        SafeOverlay.instance.RenderAllUnsafePositionsMultithreaded(event.partialTicks);
    	
        //calls other mods that need to render things in the game world nearby other entities
        RenderEntityTrackerHelper.RenderEntityInfo(event.partialTicks);
        
        //store world render transform matrices for later use when rendering HUD
        HUDEntityTrackerHelper.StoreMatrices();
    }
    
    
    
}
