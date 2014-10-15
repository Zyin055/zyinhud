package com.zyin.zyinhud.mods;

import net.minecraft.init.Items;

import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * EnderPearl Aid allows the player to easily use an ender pearl on their hotbar by calling its UseEnderPearl() method.
 */
public class EnderPearlAid extends ZyinHUDModBase
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	return Enabled = !Enabled;
    }
    
    /**
     * Makes the player throw an ender pearl if they have one.
     */
    public static void UseEnderPearl()
    {
        if(mc.playerController.isInCreativeMode())
        {
        	ZyinHUDUtil.DisplayNotification(Localization.get("enderpearlaid.increative"));
        	return;
        }
        
        if (EatingAid.instance.isEating())
        {
            EatingAid.instance.StopEating();    //it's not good if we have an ender pearl selected and hold right click down...
        }
        
        boolean usedEnderPearlSuccessfully = InventoryUtil.UseItem(Items.ender_pearl);
        
        if (!usedEnderPearlSuccessfully)
        {
        	ZyinHUDUtil.DisplayNotification(Localization.get("enderpearlaid.noenderpearls"));
        }
    }
}
