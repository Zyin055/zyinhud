package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MovingObjectPosition;

import com.zyin.zyinhud.util.FontCodes;
import com.zyin.zyinhud.util.Localization;

/**
 * The Distance Measurer calculates the distance from the player to whatever the player's
 * crosshairs is looking at.
 */
public class DistanceMeasurer extends ZyinHUDModBase
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
    
	/** The current mode for this mod */
	public static Modes Mode;
	
	/** The enum for the different types of Modes this mod can have */
    public static enum Modes
    {
        OFF(Localization.get("distancemeasurer.mode.off")),
        SIMPLE(Localization.get("distancemeasurer.mode.simple")),
        COORDINATE(Localization.get("distancemeasurer.mode.complex"));
        
        private String friendlyName;
        
        private Modes(String friendlyName)
        {
        	this.friendlyName = friendlyName;
        }

        /**
         * Sets the next availble mode for this mod
         */
        public static Modes ToggleMode()
        {
        	return Mode = Mode.ordinal() < Modes.values().length - 1 ? Modes.values()[Mode.ordinal() + 1] : Modes.values()[0];
        }
        
        /**
         * Gets the mode based on its internal name as written in the enum declaration
         * @param modeName
         * @return
         */
        public static Modes GetMode(String modeName)
        {
        	try {return Modes.valueOf(modeName);}
        	catch (IllegalArgumentException e) {return values()[0];}
        }
        
        public String GetFriendlyName()
        {
        	return friendlyName;
        }
    }
    

    public static void RenderOntoHUD()
    {
        //if the player is in the world
        //and not looking at a menu
        //and F3 not pressed
        if (DistanceMeasurer.Enabled && Mode != Modes.OFF &&
                (mc.inGameHasFocus || (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat))) &&
                !mc.gameSettings.showDebugInfo)
        {
        	String distanceString = CalculateDistanceString();
        	
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            int distanceStringWidth = mc.fontRenderer.getStringWidth(distanceString);
            
            mc.fontRenderer.drawStringWithShadow(distanceString, width/2 - distanceStringWidth/2, height/2 - 10, 0xffffff);
        }
    }
    
    
    

    /**
     * Calculates the distance of the block the player is pointing at
     * @return the distance to a block if Distance Measurer is enabled, otherwise "".
     */
    protected static String CalculateDistanceString()
    {
        MovingObjectPosition objectMouseOver = mc.thePlayer.rayTrace(300, 1);
        
        if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            if (Mode == Modes.SIMPLE)
            {
            	double coordX = mc.thePlayer.posX;
                double coordY = mc.thePlayer.posY;
                double coordZ = mc.thePlayer.posZ;
            	
                //add 0.5 to center the coordinate into the middle of the block
                double blockX = objectMouseOver.blockX + 0.5;
                double blockY = objectMouseOver.blockY + 0.5;
                double blockZ = objectMouseOver.blockZ + 0.5;
                
                double deltaX;
                double deltaY;
                double deltaZ;

                if(coordX < blockX - 0.5)
                	deltaX = (blockX - 0.5) - coordX;
                else if(coordX > blockX + 0.5)
                	deltaX = coordX - (blockX + 0.5);
                else
                	deltaX = coordX - blockX;
                
                if(coordY < blockY - 0.5)
                	deltaY = (blockY - 0.5) - coordY;
                else if(coordY > blockY + 0.5)
                	deltaY = coordY - (blockY + 0.5);
                else
                	deltaY = coordY - blockY;
                
                if(coordZ < blockZ - 0.5)
                	deltaZ = (blockZ - 0.5) - coordZ;
                else if(coordZ > blockZ + 0.5)
                	deltaZ = coordZ - (blockZ + 0.5);
                else
                	deltaZ = coordZ - blockZ;
                
            	double farthestHorizontalDistance = Math.max(Math.abs(deltaX), Math.abs(deltaZ));
                double farthestDistance = Math.max(Math.abs(deltaY), farthestHorizontalDistance);
                return FontCodes.GOLD + "[" + String.format("%1$,.1f", farthestDistance) + "]";
            }
            else if (Mode == Modes.COORDINATE)
            {
                double blockX = objectMouseOver.blockX;
                double blockY = objectMouseOver.blockY;
                double blockZ = objectMouseOver.blockZ;
                
                return FontCodes.GOLD + "[" + blockX + ", " + blockY + ", " + blockZ + "]";
            }
            else
            {
            	return FontCodes.GOLD + "[???]";
            }
        }
        else
        {
        	return FontCodes.GOLD + "["+Localization.get("distancemeasurer.far")+"]";
        }
    }
    
}
