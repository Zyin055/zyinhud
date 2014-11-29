package com.zyin.zyinhud.mods;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import com.zyin.zyinhud.ZyinHUDRenderer;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ModCompatibility;

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
    
    private static List<Class> meleeWeaponClasses = null;
    private static List<Class> rangedWeaponClasses = null;

    /**
     * Makes the player select their sword. If a sword is already selected, it selects the bow instead.
     */
    public static void SwapWeapons()
    {
        ItemStack currentItemStack = mc.thePlayer.getHeldItem();
        Item currentItem = null;

        if (currentItemStack != null)
        {
            currentItem = currentItemStack.getItem();
        }

        InitializeListOfWeaponClasses();
        

        int meleeWeaponSlot = GetItemSlotFromHotbar(meleeWeaponClasses);
        int rangedWeaponSlot = GetItemSlotFromHotbar(rangedWeaponClasses);

        if (meleeWeaponSlot < 0 && rangedWeaponSlot < 0)
        {
            //we dont have a sword or a bow
        	ZyinHUDRenderer.DisplayNotification(Localization.get("weaponswapper.noweaponsinhotbar"));
        }
        else if (meleeWeaponSlot >= 0 && rangedWeaponSlot < 0)
        {
            //we have a sword, but no bow
            SelectHotbarSlot(meleeWeaponSlot);
        }
        else if (meleeWeaponSlot < 0 && rangedWeaponSlot >= 0)
        {
            //we have a bow, but no sword
            SelectHotbarSlot(rangedWeaponSlot);
        }
        else
        {
        	//we have both a bow and a sword
        	
            if (IsMeleeWeapon(currentItem))
            {
                //currently selected sword, so equip bow
                SelectHotbarSlot(rangedWeaponSlot);
            }
            else if (IsRangedWeapon(currentItem))
            {
                //currently selected bow, so equip sword
                SelectHotbarSlot(meleeWeaponSlot);
            }
            else
            {
                //we have weapons but they are not selected, so select the sword
                SelectHotbarSlot(meleeWeaponSlot);
            }
        }
    }
    
	private static void InitializeListOfWeaponClasses()
	{
		if(meleeWeaponClasses == null)
        {
        	meleeWeaponClasses = new ArrayList<Class>();
        	meleeWeaponClasses.add(ItemSword.class);
        	
            if(ModCompatibility.TConstruct.isLoaded)
            {
    			try
    			{
    	        	meleeWeaponClasses.add(Class.forName(ModCompatibility.TConstruct.tConstructWeaponClass));
    			}
    			catch (ClassNotFoundException e)
    			{
    				e.printStackTrace();
    			}
            }
        }
		
        if(rangedWeaponClasses == null)
        {
        	rangedWeaponClasses = new ArrayList<Class>();
        	rangedWeaponClasses.add(ItemBow.class);
        	
            if(ModCompatibility.TConstruct.isLoaded)
            {
    			try
    			{
    	        	rangedWeaponClasses.add(Class.forName(ModCompatibility.TConstruct.tConstructBowClass));
    			}
    			catch (ClassNotFoundException e)
    			{
    				e.printStackTrace();
    			}
            }
        }
	}
    
    /**
     * Determines if an item is a melee weapon.
     * @param item
     * @return
     */
    private static boolean IsMeleeWeapon(Item item)
    {
    	if(meleeWeaponClasses == null)
    		return false;
    	
        for(int j = 0; j < meleeWeaponClasses.size(); j++)
        {
            if (meleeWeaponClasses.get(j).isInstance(item))
            {
                return true;
            }
        }
		return false;
	}
    
    /**
     * Determines if an item is a melee weapon.
     * @param item
     * @return
     */
    private static boolean IsRangedWeapon(Item item)
    {
    	if(rangedWeaponClasses == null)
    		return false;
    	
        for(int j = 0; j < rangedWeaponClasses.size(); j++)
        {
            if (rangedWeaponClasses.get(j).isInstance(item))
            {
                return true;
            }
        }
		return false;
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
     * @param itemClasses the type of item to find (i.e. ItemSword.class, ItemBow.class)
     * @return 0 through 8, inclusive. -1 if not found.
     */
    protected static int GetItemSlotFromHotbar(List<Class> itemClasses)
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
                    
                    for(int j = 0; j < itemClasses.size(); j++)
                    {
                        //System.out.println(i+" "+item.getClass()+" --- " + itemClasses.get(j));
                        if (itemClasses.get(j).isInstance(item))
                        {
                            return i;
                        }
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

                    for(int j = 0; j < itemClasses.size(); j++)
                    {
                        if (itemClasses.get(j).isInstance(item))
                        {
                            return i;
                        }
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
