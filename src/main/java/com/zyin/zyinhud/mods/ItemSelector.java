package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.util.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
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
    public static final int defaultTimeout = 75;
    public static final int minTimeout     = 10;
    public static final int maxTimeout     = 500;

    public static int getTimeout()
    {
        return timeout;
    }

    public static void setTimeout(int value)
    {
        timeout = MathHelper.clamp_int(value, minTimeout, maxTimeout);
    }

    static int[] slotMemory    = new int[9];
    static int   ticksToShow   = 0;

    static boolean     selecting        = false;
    static int         targetSlot       = -1;
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

        if ( currentInventory[currentSlot] != null && currentInventory[currentSlot].isItemEnchanted() )
        {
            InfoLine.DisplayNotification( Localization.get("itemselector.error.enchant") );
            done();
            return;
        }

        int memory = slotMemory[currentSlot];

        for (int i = 0; i < 36; i++)
        {
            memory += direction;

            if (memory < 9 || memory >= 36)
                memory = direction == WHEEL_UP
                        ? 9 : 35;

            if ( currentInventory[memory] != null && !currentInventory[memory].isItemEnchanted() )
            {
                targetSlot = memory;
                break;
            }
        }

        if (targetSlot == -1)
        {
            InfoLine.DisplayNotification( Localization.get("itemselector.error.empty") );
            done();
            return;
        }

        slotMemory[currentSlot] = targetSlot;

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
        int originZ      = screenHeight - invHeight - 48;

        String labelText   = currentInventory[targetSlot].getDisplayName();
        int    labelWidth  = mc.fontRenderer.getStringWidth(labelText);
        mc.fontRenderer.drawString(labelText, (screenWidth / 2) - (labelWidth / 2), originZ - mc.fontRenderer.FONT_HEIGHT - 2, 0xFFFFAA00, true);

        GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
        RenderHelper.enableGUIStandardItemLighting();

        int idx = 0;
        for (int z = 0; z < 3; z++)
        for (int x = 0; x < 9; x++)
        {
            // Draws the selection
            if (idx + 9 == targetSlot)
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

        ticksToShow--;
        if (ticksToShow <= 0)
            selectItem();
    }

    static void selectItem()
    {
        ItemStack currentStack = mc.thePlayer.inventory.mainInventory[currentSlot];
        ItemStack targetStack  = mc.thePlayer.inventory.mainInventory[targetSlot];

        // Check if what was actually selected still exists in player's inventory
        if (targetStack != null)
        {
            if ( ( currentStack != null && currentStack.isItemEnchanted() ) || targetStack.isItemEnchanted() )
            {
                InfoLine.DisplayNotification( Localization.get("itemselector.error.enchant") );
                done();
                return;
            }

            EntityClientPlayerMP player     = mc.thePlayer;
            PlayerControllerMP   controller = mc.playerController;

            controller.windowClick(player.inventoryContainer.windowId, currentSlot + 36, 0, 0, player);
            controller.windowClick(player.inventoryContainer.windowId, targetSlot, 0, 0, player);
            controller.windowClick(player.inventoryContainer.windowId, currentSlot + 36, 0, 0, player);
        }
        else
            InfoLine.DisplayNotification( Localization.get("itemselector.error.emptyslot") );

        done();
    }

    static void done()
    {
        targetSlot       = -1;
        currentSlot      = 0;
        currentInventory = null;

        ticksToShow = 0;
        selecting   = false;
    }


}
