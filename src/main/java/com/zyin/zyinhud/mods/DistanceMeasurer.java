package com.zyin.zyinhud.mods;

import net.minecraft.util.MovingObjectPosition;

import com.zyin.zyinhud.mods.Clock.Modes;
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
        COMPLEX(Localization.get("distancemeasurer.mode.complex"));
        
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
    

    /**
     * Calculates the distance of the block the player is pointing at
     * @return the distance to a block if Distance Measurer is enabled, otherwise "".
     */
    protected static String CalculateMessageForInfoLine()
    {
        if (DistanceMeasurer.Enabled && Mode != Modes.OFF)
        {
            MovingObjectPosition objectMouseOver = mc.thePlayer.rayTrace(300, 1);
            String distanceMeasurerString = "";
            
            if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
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
                

                if (Mode == Modes.SIMPLE)
                {
                	double farthestHorizontalDistance = Math.max(Math.abs(deltaX), Math.abs(deltaZ));
                    double farthestDistance = Math.max(Math.abs(deltaY), farthestHorizontalDistance);
                    return FontCodes.GOLD + "[" + String.format("%1$,.1f", farthestDistance) + "]" + InfoLine.SPACER;
                }
                else if (Mode == Modes.COMPLEX)
                {
                    double delta = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                    String x = String.format("%1$,.1f", deltaX);
                    String y = String.format("%1$,.1f", deltaY);
                    String z = String.format("%1$,.1f", deltaZ);
                    return FontCodes.GOLD + "[" + x + ", " + z + ", " + y + " (" + String.format("%1$,.1f", delta) + ")]" + InfoLine.SPACER;
                }
                else
                {
                	return FontCodes.GOLD + "[???]" + InfoLine.SPACER;
                }
            }
            else
            {
            	return FontCodes.GOLD + "["+Localization.get("distancemeasurer.far")+"]" + InfoLine.SPACER;
            }
        }

        return "";
    }
}
