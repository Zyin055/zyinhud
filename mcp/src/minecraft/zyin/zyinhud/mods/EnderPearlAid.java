package zyin.zyinhud.mods;

import net.minecraft.item.ItemEnderPearl;
import zyin.zyinhud.util.InventoryUtil;
import zyin.zyinhud.util.Localization;

/**
 * EnderPearl Aid allows the player to easily use an ender pearl on their hotbar by calling its UseEnderPearl() method.
 */
public class EnderPearlAid
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	Enabled = !Enabled;
    	return Enabled;
    }
    public static String Hotkey;
    public static final String HotkeyDescription = "Zyin's HUD: Ender Pearl Aid";
    
    /**
     * Makes the player throw an ender pearl if there is one on their hotbar.
     */
    public static void UseEnderPearl()
    {
        if (EatingAid.instance.isEating())
        {
            EatingAid.instance.StopEating();    //it's not good if we have an ender pearl selected and hold right click down...
        }
        
        boolean usedEnderPearlSuccessfully = InventoryUtil.UseItem(ItemEnderPearl.class);

        if (!usedEnderPearlSuccessfully)
        {
            InfoLine.DisplayNotification(Localization.get("enderpearlaid.noenderpearls"));
        }
    }
}
