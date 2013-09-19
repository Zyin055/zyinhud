package zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import zyin.zyinhud.util.FontCodes;
import zyin.zyinhud.util.Localization;

/**
 * The Distance Measurer calculates the distance from the player to whatever the player's
 * crosshairs is looking at.
 * <p>
 * DistanceMeasurerMode = 0: "[FarthestDistance]"<br>
 * DistanceMeasurerMode = 1: "[x, z, y (AbsoluteDistance)]"
 */
public class DistanceMeasurer
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
    
    
	/**
	 * 0=off<br>
	 * 1=simple<br>
	 * 2=complex<br>
	 */
    public static int Mode = 0;

    /** The maximum number of modes that is supported */
    public static int NumberOfModes = 3;
    
    public static String Hotkey;
    public static final String HotkeyDescription = "ZyinHUD: Distance Measurer";
    
    private static Minecraft mc = Minecraft.getMinecraft();
    private static String far;

    /**
     * Calculates the distance of the block the player is pointing at
     * @return the distance to a block if Distance Measurer is enabled, otherwise "".
     */
    protected static String CalculateMessageForInfoLine()
    {
        if (DistanceMeasurer.Enabled && Mode > 0)
        {
            MovingObjectPosition objectMouseOver = mc.thePlayer.rayTrace(300, 1);
            String distanceMeasurerString = "";

            if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
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
                

                if (Mode == 1)	//1=simple
                {
                	double farthestHorizontalDistance = Math.max(Math.abs(deltaX), Math.abs(deltaZ));
                    double farthestDistance = Math.max(Math.abs(deltaY), farthestHorizontalDistance);
                    return FontCodes.ORANGE + "[" + String.format("%1$,.1f", farthestDistance) + "]" + InfoLine.SPACER;
                }
                else if (Mode == 2)	//2=complex
                {
                    double delta = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                    String x = String.format("%1$,.1f", deltaX);
                    String y = String.format("%1$,.1f", deltaY);
                    String z = String.format("%1$,.1f", deltaZ);
                    return FontCodes.ORANGE + "[" + x + ", " + z + ", " + y + " (" + String.format("%1$,.1f", delta) + ")]" + InfoLine.SPACER;
                }
                else
                {
                	return FontCodes.ORANGE + "[???]" + InfoLine.SPACER;
                }
            }
            else
            {
            	return FontCodes.ORANGE + "["+Localization.get("distancemeasurer.far")+"]" + InfoLine.SPACER;
            }
        }

        return "";
    }
    
    /**
     * Increments the Distance Measurer mode
     * @return The new Distance Measurer mode
     */
    public static int ToggleMode()
    {
    	Mode++;
    	if(Mode >= NumberOfModes)
    		Mode = 0;
    	return Mode;
    }
}
