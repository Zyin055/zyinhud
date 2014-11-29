package com.zyin.zyinhud.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModCompatibility
{
	
	public static class TConstruct	//Tinker's Construct
	{
	    public static boolean isLoaded;

	    public static final String tConstructWeaponClass = "tconstruct.library.tools.Weapon";
	    public static final String tConstructBowClass = "tconstruct.items.tools.BowBase";
	    public static final String tConstructHarvestToolClass = "tconstruct.library.tools.HarvestTool";
	    public static final String tConstructDualHarvestToolClass = "tconstruct.library.tools.DualHarvestTool";
	    
	    public static boolean IsTConstructHarvestTool(Item item)
	    {
	    	if(isLoaded)
	    	{
	        	String className = item.getClass().getSuperclass().getName();
	        	return className.equals(tConstructHarvestToolClass) || className.equals(tConstructDualHarvestToolClass);
	    	}
	    	
	    	return false;
	    }
	    
	    public static boolean IsTConstructWeapon(Item item)
	    {
	    	if(isLoaded)
	    	{
	        	String className = item.getClass().getSuperclass().getName();
	        	return className.equals(tConstructWeaponClass);
	    	}
	    	
	    	return false;
	    }
	    
	    public static boolean IsTConstructBow(Item item)
	    {
	    	if(isLoaded)
	    	{
	        	String className = item.getClass().getSuperclass().getName();
	        	return className.equals(tConstructBowClass);
	    	}
	    	
	    	return false;
	    }
	    
	    public static boolean IsTConstructItem(Item item)
	    {
	    	return IsTConstructHarvestTool(item)
	    			|| IsTConstructWeapon(item)
	    			|| IsTConstructBow(item);
	    }
	    
	    public static boolean IsTConstructToolWithoutARightClickAction(Item item)
	    {
	    	if(isLoaded)
	    	{
	        	String className = item.getClass().getSuperclass().getName();
	        	return className.equals(tConstructHarvestToolClass);
	        		//|| className.equals(tConstructDualHarvestToolClass))	//the only DualHarvestTool is the Mattock which also tills dirt on right click
	    	}
	    	
	    	return false;
	    }
	    
	    /**
	     * 
	     * @param itemStack
	     * @return returns the damage value of the tool,
	     * 			returns the energy if it has any,
	     * 			or returns -1 if the tool is broken.
	     */
	    public static Integer GetDamage(ItemStack itemStack)
	    {
	        NBTTagCompound tags = itemStack.getTagCompound();
	        if (tags == null)
	        {
	        	return null;
	        }
	        else if (tags.hasKey("Energy"))
	        {
				return tags.getInteger("Energy");
	        }
	        else
	        {
		    	if(tags.getCompoundTag("InfiTool").getBoolean("Broken"))
		    		return -1;
		    	else
		    		return tags.getCompoundTag("InfiTool").getInteger("Damage");
	        }
	    }
	    
	    /**
	     * 
	     * @param itemStack
	     * @return returns the max durability of the tool.
	     * 			returns 400000 if it has energy.
	     */
	    public static int GetMaxDamage(ItemStack itemStack)
	    {
	        NBTTagCompound tags = itemStack.getTagCompound();
	        if (tags == null)
	        {
	        	return -1;
	        }
	        else if (tags.hasKey("Energy"))
	        {
				return 400000;	//is this right??
	        }
	        else
	        {
				return tags.getCompoundTag("InfiTool").getInteger("TotalDurability");
	        }
	    }
		
	}
	
	
}
