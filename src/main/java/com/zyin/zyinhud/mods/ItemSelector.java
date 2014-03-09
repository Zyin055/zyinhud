package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.EXTRescaleNormal;
import org.lwjgl.opengl.GL11;

/**
 * Item Selector allows the player to conveniently swap their currently selected hotbar item with something in their
 * inventory.
 */
public class ItemSelector
{
    public static final int WHEEL_UP   = 1;
    public static final int WHEEL_DOWN = -1;

    public static final RenderItem itemRenderer = new RenderItem();

    protected static Minecraft mc = Minecraft.getMinecraft();

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

    protected static int timeout;
    public static final int defaultTimeout = 500;
    public static final int minTimeout     = 10;
    public static final int maxTimeout     = 5000;

    public static int getTimeout()
    {
        return timeout;
    }

    public static void setTimeout(int value)
    {
        timeout = MathHelper.clamp_int(value, minTimeout, maxTimeout);
    }

    static int[]   slotMemory    = new int[9];
    static int     prevDirection = 0;
    static int     ticksToShow   = 0;

    static boolean     selecting        = false;
    static int         targetSlot       = 0;
    static int         currentSlot      = 0;
    static ItemStack[] currentInventory = null;

    /**
     * Scrolls the selector towards the specified direction. This will cause the item selector overlay to show.
     * @param direction Direction player is scrolling toward
     */
    public static void Scroll(int direction)
    {
        // Bind to current player state
        if (currentInventory == null)
        {
            currentSlot      = mc.thePlayer.inventory.currentItem;
            currentInventory = mc.thePlayer.inventory.mainInventory.clone();
        }

        prevDirection = direction;
        ticksToShow   = timeout;
        selecting     = true;
    }

    /**
     * Called when the player switches to another item. This will cancel any pending item select.
     */
    public static void OnItemSwitch()
    {
        done();
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
        int originZ      = screenHeight - invHeight - 32;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        mc.getTextureManager().bindTexture(widgetTexture);

        // Draws the inventory background
        for (int i = 0; i < 3; i++)
            mc.ingameGUI.drawTexturedModalRect(originX, originZ + (i * 22), 0, 0, 182, 22);

        // Draws the selection
        mc.ingameGUI.drawTexturedModalRect(originX - 1 + (1 * 20), originZ - 1, 0, 22, 24, 22);
        GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
        RenderHelper.enableGUIStandardItemLighting();

        int idx = 0;
        for (int z = 0; z < 3; z++)
        for (int x = 0; x < 9; x++)
        {
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
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);

        ticksToShow--;
        if (ticksToShow <= 0)
            selectItem();
    }

    static void selectItem()
    {
        // Check if what was actually selected still exists in player's inventory
        done();
    }

    static void done()
    {
        currentSlot      = 0;
        currentInventory = null;

        ticksToShow = 0;
        selecting   = false;
    }


}
