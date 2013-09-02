package zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import zyin.zyinhud.util.FontCodes;
import zyin.zyinhud.util.Localization;

/**
 * The Player Locator checks for nearby players and displays their name on screen wherever they are.
 */
public class PlayerLocator
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
    public static final String HotkeyDescription = "ZyinHUD: Player Locator";

	/**
	 * 0=off<br>
	 * 1=on<br>
	 */
    public static int Mode = 0;
    
    /** The maximum number of modes that is supported */
    public static int NumberOfModes = 2;
    
    /** Shows how far you are from other players next to their name */
    public static boolean ShowDistanceToPlayers;
    
    private static Minecraft mc = Minecraft.getMinecraft();
    private static final RenderItem itemRenderer = new RenderItem();
    private static final TextureManager textureManager = mc.func_110434_K();
    private static EntityClientPlayerMP me;

    private static Icon saddleIcon = GetSaddleIcon();
    private static Icon minecartIcon = GetMinecartIcon();
    private static Icon boatIcon = GetBoatIcon();

    private static ResourceLocation saddleResource = GetSaddleResourceLocation();
    private static ResourceLocation minecartResource = GetMinecartResourceLocation();
    private static ResourceLocation boatResource = GetBoatResourceLocation();

    private static final double pi = Math.PI;

    private static final String SprintingMessagePrefix = "";
    private static final String SneakingMessagePrefix = FontCodes.ITALICS;
    private static final String RidingMessagePrefix = "    ";	//space for the saddle/minecart/boat icon

    /** Don't render players that are closer than this */
    public static int viewDistanceCutoff = 10;
    public static int minViewDistanceCutoff = 0;
    public static int maxViewDistanceCutoff = 120;	//realistic max distance the game will render entities: up to ~115 blocks away
    


    /**
     * Renders nearby players's names on the screen.
     * @param entity
     * @param x location on the HUD
     * @param y location on the HUD
     * @param isEntityBehindUs
     */
    public static void RenderEntityOverlay(Entity entity, int x, int y, boolean isEntityBehindUs)
    {
        //if(!(entity instanceof EntityCow))	//for single player testing/debugging!
        if (!(entity instanceof EntityOtherPlayerMP))
        {
            return;    //we only care about other players
        }

        //if the player is in the world
        //and not looking at a menu
        //and F3 not pressed
        if (PlayerLocator.Enabled && Mode == 1 &&
                (mc.inGameHasFocus || mc.currentScreen == null || mc.currentScreen instanceof GuiChat)
                && !mc.gameSettings.showDebugInfo)
        {
            me = mc.thePlayer;
            EntityOtherPlayerMP otherPlayer = (EntityOtherPlayerMP)entity;	//could also cast as EntityPlayer
            //only show entities that are close by
            double distanceFromMe = me.getDistanceToEntity(otherPlayer);

            if (distanceFromMe > maxViewDistanceCutoff
                    || distanceFromMe < viewDistanceCutoff
                    || distanceFromMe == 0) //don't render ourself!
            {
                return;
            }

            String otherPlayerName = otherPlayer.getEntityName();
            String overlayMessage = otherPlayerName;

            //add distance to this player into the message
            if (ShowDistanceToPlayers)
            {
                overlayMessage = "" + (int)distanceFromMe + " " + overlayMessage;
            }

            //add special effects based on what the other player is doing
            if (otherPlayer.isSprinting())
            {
                overlayMessage = SprintingMessagePrefix + overlayMessage;	//nothing
            }
            if (otherPlayer.isSneaking())
            {
                overlayMessage = SneakingMessagePrefix + overlayMessage;	//italics
            }
            if (otherPlayer.isRiding())	//this doesn't work on some servers
            {
                overlayMessage = RidingMessagePrefix + overlayMessage;		//space for the saddle icon
            }

            int overlayMessageWidth = mc.fontRenderer.getStringWidth(overlayMessage);	//the width in pixels of the message
            ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();		//~427
            int height = res.getScaledHeight();		//~240
            //check if the text is attempting to render outside of the screen, and if so, fix it to snap to the edge of the screen.
            x = (x > width) ? width : x;
            x = (x < 0) ? 0 : x;
            y = (y > height - 10) ? height - 10 : y;
            y = (y < 10) ? 10 : y;	//use 10 instead of 0 so that we don't write text onto the top left InfoLine message area

            //don't let the text go off the right side of the screen
            if (x > width - overlayMessageWidth)
            {
                x = width - overlayMessageWidth;
            }

            //calculate the color of the overlayMessage based on the distance from me
            int alpha = (int)(0x11 + 0xEE * ((maxViewDistanceCutoff - distanceFromMe) / maxViewDistanceCutoff));
            alpha = alpha << 24;	//turns it into the format: 0x##000000
            int rgb = 0xFFFFFF;
            int color = rgb + alpha;	//alpha:r:g:b
            
            //render the overlay message
            GL11.glDisable(GL11.GL_LIGHTING);
            mc.fontRenderer.drawStringWithShadow(overlayMessage, x, y, color);

            //also render whatever the player is currently riding on
            if (otherPlayer.ridingEntity instanceof EntityHorse
                    || otherPlayer.ridingEntity instanceof EntityPig)
            {
            	if(saddleResource == null)
            		saddleResource = GetSaddleResourceLocation();
            	if(saddleIcon == null)
            		saddleIcon = GetSaddleIcon();
            	
                textureManager.func_110577_a(saddleResource);	//bind texture
                itemRenderer.renderIcon(x, y - 2, saddleIcon, 12, 12);
            }
            else if (otherPlayer.ridingEntity instanceof EntityMinecart)
            {
            	if(minecartResource == null)
            		minecartResource = GetMinecartResourceLocation();
            	if(minecartIcon == null)
            		minecartIcon = GetMinecartIcon();
            	
                textureManager.func_110577_a(minecartResource);	//bind texture
                itemRenderer.renderIcon(x, y - 2, minecartIcon, 12, 12);
            }
            else if (otherPlayer.ridingEntity instanceof EntityBoat)
            {
            	if(boatResource == null)
            		boatResource = GetBoatResourceLocation();
            	if(boatIcon == null)
            		boatIcon = GetBoatIcon();
            	
                textureManager.func_110577_a(boatResource);	//bind texture
                itemRenderer.renderIcon(x, y - 2, boatIcon, 12, 12);
            }
        }
    }

    /*
    public static double AngleBetweenTwoVectors(Vec3 a, Vec3 b)
    {
        double crossX = a.yCoord * b.zCoord - a.zCoord * b.yCoord;
        double crossY = a.zCoord * b.xCoord - a.xCoord * b.zCoord;
        double crossZ = a.xCoord * b.yCoord - a.yCoord * b.xCoord;
        double cross = Math.sqrt(crossX * crossX + crossY * crossY + crossZ * crossZ);
        double dot = a.xCoord * b.xCoord + a.yCoord * b.yCoord + a.zCoord + b.zCoord;

        return Math.atan2(cross, dot);
    }
    public static double SignedAngleBetweenTwoVectors(Vec3 a, Vec3 b)
    {
    	// Get the angle in degrees between 0 and 180
    	double angle = AngleBetweenTwoVectors(b, a);

    	// the vector perpendicular to referenceForward (90 degrees clockwise)
    	// (used to determine if angle is positive or negative)
    	Vec3 referenceRight = (Vec3.createVectorHelper(0, 1, 0)).crossProduct(a);

    	// Determine if the degree value should be negative.  Here, a positive value
    	// from the dot product means that our vector is the right of the reference vector
    	// whereas a negative value means we're on the left.
    	double sign = (b.dotProduct(referenceRight) > 0.0) ? 1.0: -1.0;

    	return sign * angle;
    }
    */

    /**
     * Gets the status of the Player Locator
     * @return the string "players" if the Player Locator is enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine()
    {
        if (Mode == 0)	//off
        {
            return FontCodes.WHITE + "";
        }
        else if (Mode == 1)	//on
        {
            return FontCodes.WHITE + Localization.get("playerlocator.infoline") + InfoLine.SPACER;
        }
        else
        {
            return FontCodes.WHITE + "???" + InfoLine.SPACER;
        }
    }
    

    
    /**
     * Increments the Clock mode
     * @return The new Clock mode
     */
    public static int ToggleMode()
    {
    	Mode++;
    	if(Mode >= NumberOfModes)
    		Mode = 0;
    	return Mode;
    }
    
    /**
     * Toggle showing the distance to players
     * @return The new Clock mode
     */
    public static boolean ToggleShowDistanceToPlayers()
    {
    	ShowDistanceToPlayers = !ShowDistanceToPlayers;
    	return ShowDistanceToPlayers;
    }

    private static ResourceLocation GetSaddleResourceLocation()
    {
    	return textureManager.func_130087_a(new ItemStack(Item.saddle).getItemSpriteNumber());
    }
    private static Icon GetSaddleIcon()
    {
    	return new ItemStack(Item.saddle).getIconIndex();
    }
    private static ResourceLocation GetMinecartResourceLocation()
    {
    	return textureManager.func_130087_a(new ItemStack(Item.minecartEmpty).getItemSpriteNumber());
    }
    private static Icon GetMinecartIcon()
    {
    	return new ItemStack(Item.minecartEmpty).getIconIndex();
    }
    private static ResourceLocation GetBoatResourceLocation()
    {
    	return textureManager.func_130087_a(new ItemStack(Item.boat).getItemSpriteNumber());
    }
    private static Icon GetBoatIcon()
    {
    	return new ItemStack(Item.boat).getIconIndex();
    }
}
