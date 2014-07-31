package com.zyin.zyinhud.mods;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.Localization;

/**
 * The Compass determines what direction the player is facing.
 */
public class Compass extends ZyinHUDModBase
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
    
    public static boolean renderCompassTextInMiddle = true;

    private static String south = Localization.get("compass.south");
    private static String southwest = Localization.get("compass.southwest");
    private static String west = Localization.get("compass.west");
    private static String northwest = Localization.get("compass.northwest");
    private static String north = Localization.get("compass.north");
    private static String northeast = Localization.get("compass.northeast");
    private static String east = Localization.get("compass.east");
    private static String southeast = Localization.get("compass.southeast");

    /**
     * Calculates the direction the player is facing
     * @param infoLineMessageUpToThisPoint
     * @param renderCompassTextInMiddle if true, stabalizes the compass by rendering the red cardinal direction
     * in the middle of of the containing [ ] brackets
     * @return "[Direction]" compass formatted string if the Compass is enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine(String infoLineMessageUpToThisPoint)
    {
        if (Compass.Enabled)
        {
            int yaw = (int)mc.thePlayer.rotationYaw;
            yaw += 22;	//+22 centers the compass (45degrees/2)
            yaw %= 360;

            if (yaw < 0)
                yaw += 360;

            int facing = yaw / 45; //  360degrees divided by 45 == 8 zones
            String compassDirection = "";

            if (facing == 0)
                compassDirection = south;
            else if (facing == 1)
                compassDirection = southwest;
            else if (facing == 2)
                compassDirection = west;
            else if (facing == 3)
                compassDirection = northwest;
            else if (facing == 4)
                compassDirection = north;
            else if (facing == 5)
                compassDirection = northeast;
            else if (facing == 6)
                compassDirection = east;
            else// if(facing == 7)
                compassDirection = southeast;
            
            if(renderCompassTextInMiddle)
            {
            	String brackets;
            	int x;
            	int x_padding;

        		//the font spacing is different if we are rendering in Unicode
            	if(mc.fontRenderer.getUnicodeFlag())
            	{
            		brackets = "[  ]";
                	x = mc.fontRenderer.getStringWidth(infoLineMessageUpToThisPoint);
                	x_padding = mc.fontRenderer.getStringWidth(brackets)/2 - 4;
                	if(facing % 2 == 0)	//s,w,n,e
                		x_padding += 2;
            		
            	}
            	else
            	{
            		brackets = "[   ]";
                	x = mc.fontRenderer.getStringWidth(infoLineMessageUpToThisPoint);
                	x_padding = mc.fontRenderer.getStringWidth(brackets)/2 - 6;
                	if(facing % 2 == 0)	//s,w,n,e
                		x_padding += 3;
            	}
            	
            	
            	mc.fontRenderer.drawStringWithShadow(FontCodes.RED + compassDirection, InfoLine.infoLineLocX + x + x_padding, InfoLine.infoLineLocY, 0xffffff);
            	
            	return FontCodes.GRAY + brackets;
            }
            else
            {
                return FontCodes.GRAY + "[" + FontCodes.RED + compassDirection + FontCodes.GRAY + "]";
            }
        }

        return "";
    }
    
}
