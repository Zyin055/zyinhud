package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

/**
 * Weapon Swap allows the player to quickly equip their sword and bow.
 */
public class WeaponSwapper extends ZyinHUDModBase
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
    
    public static boolean ScanHotbarForWeaponsFromLeftToRight;

    /**
     * Makes the player select their sword. If a sword is already selected, it selects the bow instead.
     */
    public static void SwapWeapons()
    {
        ItemStack currentItemStack = mc.thePlayer.getHeldItem();
        Item currentItem;

        if (currentItemStack == null)
        {
            currentItem = null;
        }
        else
        {
            currentItem = currentItemStack.getItem();
        }

        int bowSlot = GetItemSlotFromHotbar(ItemBow.class);
        int swordSlot = GetItemSlotFromHotbar(ItemSword.class);

        if (swordSlot < 0 && bowSlot < 0)
        {
            //we dont have a sword or a bow
            ZyinHUDUtil.DisplayNotification(Localization.get("weaponswapper.noweaponsinhotbar"));
        }
        else if (swordSlot >= 0 && bowSlot < 0)
        {
            //we have a sword, but no bow
            SelectHotbarSlot(swordSlot);
        }
        else if (swordSlot < 0 && bowSlot >= 0)
        {
            //we have a bow, but no sword
            SelectHotbarSlot(bowSlot);
        }
        else if (currentItem instanceof ItemSword)
        {
            //currently selected sword, so equip bow
            SelectHotbarSlot(bowSlot);
        }
        else if (currentItem instanceof ItemBow)
        {
            //currently selected bow, so equip sword
            SelectHotbarSlot(swordSlot);
        }
        else
        {
            //we have weapons but they are not selected, so select the sword
            SelectHotbarSlot(swordSlot);
        }
    }

    /**
     * Makes the player select a slot on their hotbar
     * @param slot 0 through 8
     */
    protected static void SelectHotbarSlot(int slot)
    {
        if (slot < 0 || slot > 8)
        {
            return;
        }

        mc.thePlayer.inventory.currentItem = slot;
    }

    /**
     * Gets the index of an item that exists in the player's hotbar.
     * @param itemType the type of item to find (i.e. ItemSword.class, ItemBow.class)
     * @return 0 through 8, inclusive. -1 if not found.
     */
    protected static int GetItemSlotFromHotbar(Class itemType)
    {
        ItemStack[] items = mc.thePlayer.inventory.mainInventory;

        if (ScanHotbarForWeaponsFromLeftToRight)
        {
            for (int i = 0; i < 9; i++)
            {
                ItemStack itemStack = items[i];

                if (itemStack != null)
                {
                    Item item = itemStack.getItem();

                    if (item.getClass().getName().equals(itemType.getName()))
                    {
                        return i;
                    }
                }
            }
        }
        else
        {
            for (int i = 8; i >= 0; i--)
            {
                ItemStack itemStack = items[i];

                if (itemStack != null)
                {
                    Item item = itemStack.getItem();

                    if (item.getClass().getName().equals(itemType.getName()))
                    {
                        return i;
                    }
                }
            }
        }

        return -1;
    }
    
    /**
     * Toggles between scanning the hotbar starting from left or right
     * @return The state new scanning method
     */
    public static boolean ToggleScanHotbarFromLeftToRight()
    {
    	return ScanHotbarForWeaponsFromLeftToRight = !ScanHotbarForWeaponsFromLeftToRight;
    }
}
