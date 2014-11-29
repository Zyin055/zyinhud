package com.zyin.zyinhud.mods;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.EXTRescaleNormal;
import org.lwjgl.opengl.GL11;

import com.zyin.zyinhud.ZyinHUDRenderer;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;

/**
 * Item Selector allows the player to conveniently swap their currently selected
 * hotbar item with something in their inventory.
 */
public class ItemSelector extends ZyinHUDModBase
{
	/** Enables/Disables this Mod */
	public static boolean Enabled;

	/**
	 * Toggles this Mod on or off
	 * 
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
		ALL(Localization.get("itemselector.mode.all")),
		SAME_COLUMN(Localization.get("itemselector.mode.column"));

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
	 * Determines if the side buttons of supported mice can be used for item selection
	 */
	public static boolean UseMouseSideButtons;

	private static final ResourceLocation widgetTexture = new ResourceLocation("textures/gui/widgets.png");

	public static final int WHEEL_UP = -1;
	public static final int WHEEL_DOWN = 1;

	protected static int timeout;
	public static final int defaultTimeout = 200;
	public static final int minTimeout = 50;
	public static final int maxTimeout = 500;

	private static int[] slotMemory = new int[InventoryPlayer.getHotbarSize()];
	private static int ticksToShow = 0;

	private static boolean isCurrentlySelecting = false;
	private static int scrollAmount = 0;
	private static int previousDir = 0;
	private static int targetInvSlot = -1;
	private static int currentHotbarSlot = 0;
	private static ItemStack[] currentInventory = null;

	/**
	 * Scrolls the selector towards the specified direction. This will cause the item selector overlay to show.
	 * @param direction
	 *            Direction player is scrolling toward
	 */
	public static void Scroll(int direction)
	{
		// Bind to current player state
		if (currentInventory == null)
		{
			currentHotbarSlot = mc.thePlayer.inventory.currentItem;
			currentInventory = mc.thePlayer.inventory.mainInventory.clone();
		}

		if (!AdjustSlot(direction))
		{
			Done();
			return;
		}

		slotMemory[currentHotbarSlot] = targetInvSlot;

		scrollAmount++;
		ticksToShow = timeout;
		isCurrentlySelecting = true;
	}

	/**
	 * Swaps the currently selected item by one toward the given direction
	 * @param direction
	 *            Direction player is scrolling toward
	 */
	public static void SideButton(int direction)
	{
		currentHotbarSlot = mc.thePlayer.inventory.currentItem;
		currentInventory = mc.thePlayer.inventory.mainInventory.clone();

		if (AdjustSlot(direction))
		{
			slotMemory[currentHotbarSlot] = targetInvSlot;
			SelectItem();
		}
		else
			Done();
	}

	/**
	 * Calculates the adjustment of the currently selected hotbar slot by the given direction
	 * @param direction
	 *            Direction to adjust towards
	 * @return True if successful, false if attempting to switch enchanted item
	 *         or no target is available
	 */
	private static boolean AdjustSlot(int direction)
	{
		if (!mc.isSingleplayer())
		{
			if (currentInventory[currentHotbarSlot] != null && currentInventory[currentHotbarSlot].isItemEnchanted())
			{
				ZyinHUDRenderer.DisplayNotification(Localization.get("itemselector.error.enchant"));
				return false;
			}
		}

		int memory = slotMemory[currentHotbarSlot];	//'memory' is where the cursor was last located for this particular hotbar slot

		for (int i = 0; i < 36; i++)
		{
			// This complicated bit of logic allows for side button mechanism to
			// go back and forth without skipping
			// slots
			if (scrollAmount != 0 || previousDir == direction)
				memory += direction;

			if (memory < 9 || memory >= 36)
				memory = direction == WHEEL_DOWN ? 9 : 35;

			previousDir = direction;

			if (Mode == Modes.SAME_COLUMN && memory % 9 != currentHotbarSlot)
				continue;

			if (currentInventory[memory] == null)
				continue;

			if (!mc.isSingleplayer()
					&& currentInventory[memory].isItemEnchanted())
				continue;

			targetInvSlot = memory;
			break;
		}

		if (targetInvSlot == -1)
		{
			ZyinHUDRenderer.DisplayNotification(Localization.get("itemselector.error.empty"));
			return false;
		}
		else
			return true;
	}

	/**
	 * Checks if selection is ongoing and the modifier key gets de-pressed.
	 */
	public static void OnHotkeyReleased()
	{
		if (!ItemSelector.Enabled)
			return;

		if (isCurrentlySelecting)
			SelectItem();
	}

	/**
	 * If selecting an item, this draws the player's inventory on-screen with the current selection.
	 * @param partialTicks
	 */
	public static void RenderOntoHUD(float partialTicks)
	{
		if (!isCurrentlySelecting)
			return;

		ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int screenWidth = scaledresolution.getScaledWidth();
		int screenHeight = scaledresolution.getScaledHeight();
		int invWidth = 182;
		int invHeight = 22 * 3;
		int originX = (screenWidth / 2) - (invWidth / 2);
		int originZ = screenHeight - invHeight - 48;

		String labelText = currentInventory[targetInvSlot].getDisplayName();
		int labelWidth = mc.fontRendererObj.getStringWidth(labelText);
		mc.fontRendererObj.func_175063_a(labelText, (screenWidth / 2) - (labelWidth / 2), originZ - mc.fontRendererObj.FONT_HEIGHT - 2, 0xFFFFAA00);

		GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
		GL11.glEnable(GL11.GL_DEPTH_TEST); // so the enchanted item effect is rendered properly
		
		RenderHelper.enableGUIStandardItemLighting();
		//OpenGlHelper.glBlendFunc(770, 771, 1, 0);

		int idx = 0;
		for (int z = 0; z < 3; z++) // 3 rows of the inventory
		{
			for (int x = 0; x < 9; x++) // 9 cols of the inventory
			{
				if (Mode == Modes.SAME_COLUMN && x != currentHotbarSlot)
				{
					// don't draw items that we will never be able to select if Same Column mode is active
					idx++;
					continue;
				}

				OpenGlHelper.glBlendFunc(770, 771, 1, 0); // so the selection graphic renders properly

				// Draws the selection
				if (idx + 9 == targetInvSlot)
				{
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
					ZyinHUDRenderer.RenderCustomTexture(originX + (x * 20) - 1, originZ + (z * 22) - 1, 0, 22, 24, 24, widgetTexture, 1f);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_BLEND);
				}

				ItemStack itemStack = currentInventory[idx + 9];

				if (itemStack != null)
				{
					float anim = itemStack.animationsToGo - partialTicks;
					int dimX = originX + (x * 20) + 3;
					int dimZ = originZ + (z * 22) + 3;
					
					if (anim > 0.0F)
					{
						GL11.glPushMatrix();
						float f2 = 1.0F + anim / 5.0F;
						GL11.glTranslatef(dimX + 8, dimZ + 12, 0.0F);
						GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
						GL11.glTranslatef(-(dimX + 8), -(dimZ + 12), 0.0F);
					}
					
					//itemRenderer.renderItemAndEffectIntoGUI(mc.fontRendererObj, mc.getTextureManager(), itemStack, dimX, dimZ);
					itemRenderer.func_180450_b(itemStack, dimX, dimZ);

					if (anim > 0.0F)
						GL11.glPopMatrix();

					//itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, mc.getTextureManager(), itemStack, dimX, dimZ);
					itemRenderer.func_175030_a(mc.fontRendererObj, itemStack, dimX, dimZ);
				}

				idx++;
			}
		}

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
		GL11.glDisable(GL11.GL_LIGHTING); // the itemRenderer.renderItem() method enables lighting

		ticksToShow--;
		if (ticksToShow <= 0)
			Done();
	}

	private static void SelectItem()
	{
		ItemStack currentStack = mc.thePlayer.inventory.mainInventory[currentHotbarSlot];
		ItemStack targetStack = mc.thePlayer.inventory.mainInventory[targetInvSlot];

		// Check if what was actually selected still exists in player's inventory
		if (targetStack != null)
		{
			if (!mc.isSingleplayer())
			{
				if ((currentStack != null && currentStack.isItemEnchanted())
						|| targetStack.isItemEnchanted())
				{
					ZyinHUDRenderer.DisplayNotification(Localization.get("itemselector.error.enchant"));
					Done();
					return;
				}
			}

			int currentInvSlot = InventoryUtil.TranslateHotbarIndexToInventoryIndex(currentHotbarSlot);
			
			if(currentInvSlot < 0)
			{
				//this can happen if the player is using a mod to increase the size of their hotbar
				ZyinHUDRenderer.DisplayNotification(Localization.get("itemselector.error.unsupportedhotbar"));
				Done();
				return;
			}
			
			InventoryUtil.Swap(currentInvSlot, targetInvSlot);
		}
		else
			ZyinHUDRenderer.DisplayNotification(Localization.get("itemselector.error.emptyslot"));

		Done();
	}

	private static void Done()
	{
		targetInvSlot = -1;
		scrollAmount = 0;
		currentHotbarSlot = 0;
		currentInventory = null;

		ticksToShow = 0;
		isCurrentlySelecting = false;
	}
	


	public static int GetTimeout()
	{
		return timeout;
	}

	public static void SetTimeout(int value)
	{
		timeout = MathHelper.clamp_int(value, minTimeout, maxTimeout);
	}

    /**
     * Toggles using the mouse forward and back buttons
     * @return 
     */
    public static boolean ToggleUseMouseSideButtons()
    {
    	return UseMouseSideButtons = !UseMouseSideButtons;
    }
}
