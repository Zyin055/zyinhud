package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.EXTRescaleNormal;
import org.lwjgl.opengl.GL11;

import com.zyin.zyinhud.mods.Coordinates.Modes;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;

/**
 * Item Selector allows the player to conveniently swap their currently selected hotbar item with something in their
 * inventory.
 */
public class ItemSelector extends ZyinHUDModBase
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
        ALL(Localization.get("itemselector.mode.0")),
        SAME_COLUMN(Localization.get("itemselector.mode.1"));
        
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
    
    public static final int WHEEL_UP   = -1;
    public static final int WHEEL_DOWN = 1;
    
    protected static int timeout;
    public static final int defaultTimeout = 200;
    public static final int minTimeout     = 50;
    public static final int maxTimeout     = 500;

    public static int GetTimeout()
    {
        return timeout;
    }

    public static void SetTimeout(int value)
    {
        timeout = MathHelper.clamp_int(value, minTimeout, maxTimeout);
    }

    private static int[] slotMemory    = new int[9];
    private static int   ticksToShow   = 0;

    private static boolean     selecting         = false;
    private static int         targetInvSlot     = -1;
    private static int         currentHotbarSlot = 0;
    private static ItemStack[] currentInventory  = null;

    /**
     * Scrolls the selector towards the specified direction. This will cause the item selector overlay to show.
     * @param direction Direction player is scrolling toward
     */
    public static void Scroll(int direction)
    {
        // Bind to current player state
        if (currentInventory == null)
        {
            currentHotbarSlot = mc.thePlayer.inventory.currentItem;
            currentInventory = mc.thePlayer.inventory.mainInventory.clone();
        }

        if ( !mc.isSingleplayer() )
        if ( currentInventory[currentHotbarSlot] != null && currentInventory[currentHotbarSlot].isItemEnchanted() )
        {
            InfoLine.DisplayNotification( Localization.get("itemselector.error.enchant") );
            Done();
            return;
        }

        int memory = slotMemory[currentHotbarSlot];

        for (int i = 0; i < 36; i++)
        {
            memory += direction;

            if (memory < 9 || memory >= 36)
                memory = direction == WHEEL_DOWN
                        ? 9 : 35;

            if (Mode == Modes.SAME_COLUMN && memory % 9 != currentHotbarSlot)
                continue;

            if (currentInventory[memory] == null)
                continue;

            if ( !mc.isSingleplayer() && currentInventory[memory].isItemEnchanted() )
                continue;

            targetInvSlot = memory;
            break;
        }

        if (targetInvSlot == -1)
        {
            InfoLine.DisplayNotification( Localization.get("itemselector.error.empty") );
            Done();
            return;
        }

        slotMemory[currentHotbarSlot] = targetInvSlot;

        ticksToShow   = timeout;
        selecting     = true;
    }

    /**
     * Tick event that checks if selection is ongoing and the modifier key gets de-pressed
     * @param pressed
     */
    public static void CheckModifierPressed(boolean pressed)
    {
        if (!ItemSelector.Enabled)
            return;

        if (selecting && !pressed)
            SelectItem();
    }

    /**
     * If selecting an item, this draws the player's inventory on-screen with the current selection.
     * @param partialTicks
     */
    public static void RenderOntoHUD(float partialTicks)
    {
        if (!selecting)
            return;

        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        ResourceLocation widgetTexture    = new ResourceLocation("textures/gui/widgets.png");

        int screenWidth  = scaledresolution.getScaledWidth();
        int screenHeight = scaledresolution.getScaledHeight();
        int invWidth     = 182;
        int invHeight    = 22 * 3;
        int originX      = (screenWidth / 2) - (invWidth / 2);
        int originZ      = screenHeight - invHeight - 48;

        String labelText   = currentInventory[targetInvSlot].getDisplayName();
        boolean isEnchanted;
        int    labelWidth  = mc.fontRenderer.getStringWidth(labelText);
        mc.fontRenderer.drawString(labelText, (screenWidth / 2) - (labelWidth / 2), originZ - mc.fontRenderer.FONT_HEIGHT - 2, 0xFFFFAA00, true);

        GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);	//so the enchanted item effect is rendered properly
        RenderHelper.enableGUIStandardItemLighting();

        int idx = 0;
        for (int z = 0; z < 3; z++)
        for (int x = 0; x < 9; x++)
        {
            // Draws the selection
            if (idx + 9 == targetInvSlot)
            {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                mc.getTextureManager().bindTexture(widgetTexture);
                mc.ingameGUI.drawTexturedModalRect(originX + (x * 20) - 1, originZ + (z * 22) - 1, 0, 22, 24, 24);
                GL11.glDisable(GL11.GL_BLEND);
            }

            ItemStack itemStack = currentInventory[idx + 9];

            if (itemStack != null)
            {
                float anim = itemStack.animationsToGo - partialTicks;
                int   dimX = originX + (x * 20) + 3;
                int   dimZ = originZ + (z * 22) + 3;

                if (anim > 0.0F)
                {
                    GL11.glPushMatrix();
                    float f2 = 1.0F + anim / 5.0F;
                    GL11.glTranslatef(dimX + 8, dimZ + 12, 0.0F);
                    GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                    GL11.glTranslatef(-(dimX + 8), -(dimZ + 12), 0.0F);
                }

                itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, dimX, dimZ);

                if (anim > 0.0F)
                    GL11.glPopMatrix();

                itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, dimX, dimZ);
            }

            idx++;
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
		GL11.glDisable(GL11.GL_LIGHTING);	//the itemRenderer.renderItem() method enables lighting

        ticksToShow--;
        if (ticksToShow <= 0)
            Done();
    }

    private static void SelectItem()
    {
        ItemStack currentStack = mc.thePlayer.inventory.mainInventory[currentHotbarSlot];
        ItemStack targetStack  = mc.thePlayer.inventory.mainInventory[targetInvSlot];

        // Check if what was actually selected still exists in player's inventory
        if (targetStack != null)
        {
            if ( !mc.isSingleplayer() )
            {
	            if ( ( currentStack != null && currentStack.isItemEnchanted() ) || targetStack.isItemEnchanted() )
	            {
	                InfoLine.DisplayNotification( Localization.get("itemselector.error.enchant") );
	                Done();
	                return;
	            }
            }
            
            int currentInvSlot = InventoryUtil.TranslateHotbarIndexToInventoryIndex(currentHotbarSlot);
            
            InventoryUtil.Swap(currentInvSlot, targetInvSlot);
        }
        else
            InfoLine.DisplayNotification( Localization.get("itemselector.error.emptyslot") );

        Done();
    }

    private static void Done()
    {
        targetInvSlot     = -1;
        currentHotbarSlot = 0;
        currentInventory  = null;

        ticksToShow = 0;
        selecting   = false;
    }
    
}
