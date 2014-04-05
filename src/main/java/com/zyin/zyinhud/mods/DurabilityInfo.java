package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.gui.GuiZyinHUDOptions;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * Durability Info checks to see if any equipment (items in the hotbar, and armor) is damaged
 * and then displays info about them onto the HUD.
 */
public class DurabilityInfo extends ZyinHUDModBase
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
    
	protected static final ResourceLocation durabilityIconsResourceLocation = new ResourceLocation("zyinhud:textures/durability_icons.png");
    
    public static boolean ShowArmorDurability;
    public static boolean ShowItemDurability;
	public static boolean ShowIndividualArmorIcons;
    public static boolean ShowDamageAsPercentage;
    public static boolean AutoUnequipArmor;
    public static boolean AutoUnequipTools;

    public static final int durabilityUpdateFrequency = 1000;

    //U and V is the top left part of the image
    //X and Y is the width and height of the image
    protected static float armorDurabilityScaler = 1/5f;
    protected static int armorDurabilityIconU = 0;
    protected static int armorDurabilityIconV = 0;
    protected static int armorDurabilityIconX = (int)(5*16 * armorDurabilityScaler);
    protected static int armorDurabilityIconY = (int)(7.5*16 * armorDurabilityScaler);

    //the height/width of the tools being rendered
    public static int toolX = 1 * 16;
    public static int toolY = 1 * 16;

    //where the armor icon is rendered (these values replaced by the config settings)
    public static int durabalityLocX = 30;
    public static int durabalityLocY = 20;

    //where the tool icons are rendered (these values replaced by the config settings)
    protected static int equipmentLocX = 20 + armorDurabilityIconX;
    protected static int equipmentLocY = 20;

    private static float durabilityDisplayThresholdForArmor;
    private static float durabilityDisplayThresholdForItem;

    private static ArrayList<ItemStack> damagedItemsList = new ArrayList<ItemStack>(13);	//used to push items into the list of broken equipment to render
    

    /**
     * The last time the item cache was generated
     */
    private static long lastGenerate;

    /**
     * Renders the main durability icon and any damaged tools onto the screen.
     */
    public static void RenderOntoHUD()
    {
        //if the player is in the world
        //and not in a menu (except for chat and the custom Options menu)
        //and F3 not shown
        if (DurabilityInfo.Enabled &&
                mc.inGameHasFocus ||
                (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || TabIsSelectedInOptionsGui())) &&
        		!mc.gameSettings.showDebugInfo)
        {
            //don't waste time recalculating things every tick
        	if(System.currentTimeMillis() - lastGenerate > durabilityUpdateFrequency)	//update every 1 second
            {
                CalculateDurabilityIcons();
            }

            boolean armorExists = false;

            for (ItemStack itemStack : damagedItemsList)
            {
                if (itemStack.getItem() instanceof ItemArmor)
                    armorExists = true;
            }

            int numTools = 0;
            int numArmors = 0;

            for (ItemStack itemStack : damagedItemsList)
            {
                Item tool = itemStack.getItem();

                //if this tool is an armor
                if (tool instanceof ItemArmor)
                {
                    if (ShowArmorDurability)
                    {
                    	if(ShowIndividualArmorIcons)
                    	{
                            int x = durabalityLocX;
                            int y = durabalityLocY + (numArmors * toolY);
                            
                            DrawItemIcon(itemStack, x, y);
                            numArmors++;
                    	}
                    	else
                    	{
                            DrawBrokenArmorTexture(durabalityLocX, durabalityLocY);
                    	}
                    }
                }
                else //if this tool is an equipment/tool
                {
                    if (ShowItemDurability)
                    {
                        int x = durabalityLocX;
                        int y = durabalityLocY + (numTools * toolY);

                        if (armorExists && ShowArmorDurability)
                            x = equipmentLocX;    //if armor is being rendered then push this to the right

                        DrawItemIcon(itemStack, x, y);
                        numTools++;
                    }
                }
            }
        }
    }
    
    /**
     * Draws an ItemStack at the specified location on screen with its durability bar and number.
     * @param itemStack
     * @param x
     * @param y
     */
	protected static void DrawItemIcon(ItemStack itemStack, int x, int y)
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);	//so the enchanted item effect is rendered properly
		
		//render the item with enchant effect
		itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, itemStack, x, y);
		
		//render the item's durability bar
		itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, itemStack, x, y);
		
		GL11.glDisable(GL11.GL_LIGHTING);	//the itemRenderer.renderItem() method enables lighting
		GL11.glDisable(GL11.GL_DEPTH_TEST);	//so the text renders above the item
		
		//render the number of durability it has left
		if(itemStack.getItemDamage() != 0)
		{
			boolean unicodeFlag = mc.fontRenderer.getUnicodeFlag();
			mc.fontRenderer.setUnicodeFlag(true);
			String damageString = GetDamageString(itemStack.getItemDamage(), itemStack.getMaxDamage());
			int damageX = x + toolX - mc.fontRenderer.getStringWidth(damageString);
			int damageY = y + toolY - mc.fontRenderer.FONT_HEIGHT - 1;
			mc.fontRenderer.drawStringWithShadow(damageString, damageX, damageY, 0xffffff);
			mc.fontRenderer.setUnicodeFlag(unicodeFlag);
		}
	}
    
    /***
     * Draws the broken durability image
     * @param x
     * @param y
     */
	protected static void DrawBrokenArmorTexture(int x, int y)
	{
		GL11.glEnable(GL11.GL_BLEND);	//for a transparent texture
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glColor4f(255f, 255f, 255f, 255f);	//fixes transparency issue when a InfoLine Notification is displayed
		
		ZyinHUDUtil.DrawTexture(x, y, 
				armorDurabilityIconU, armorDurabilityIconV, 
				(int)(armorDurabilityIconX/armorDurabilityScaler), (int)(armorDurabilityIconY/armorDurabilityScaler), 
				durabilityIconsResourceLocation, armorDurabilityScaler);
		
		GL11.glDisable(GL11.GL_BLEND);
	}
    
    /**
     * Finds items in the players hot bar and equipped armor that is damaged and adds them to the damagedItemsList list.
     */
    protected static void CalculateDurabilityIcons()
    {
        //if the player is in the world
        //and not in a menu (except for chat and the custom Options menu)
        //and not typing
        if (mc.inGameHasFocus ||
        		(mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiZyinHUDOptions && ((GuiZyinHUDOptions)mc.currentScreen).IsButtonTabSelected(Localization.get("durabilityinfo.name")))) &&
        		!mc.gameSettings.keyBindPlayerList.isPressed())
        {
            damagedItemsList.clear();
            UnequipDamagedArmor();
            UnequipDamagedTool();
            CalculateDurabilityIconsForTools();
            CalculateDurabilityIconsForArmor();
            lastGenerate = System.currentTimeMillis();
        }
    }

    /**
     * Examines the players first 9 inventory slots (the players inventory) and sees if any tools are damaged.
     * It adds damaged tools to the damagedItemsList list.
     */
    private static void CalculateDurabilityIconsForTools()
    {
        ItemStack[] items = mc.thePlayer.inventory.mainInventory;

        for (int i = 0; i < 9; i++)
        {
            ItemStack itemStack = items[i];

            if (itemStack != null)
            {
                Item item = itemStack.getItem();

                if (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemBow || item instanceof ItemHoe
                        || item instanceof ItemShears || item instanceof ItemFishingRod)
                {
                    int itemDamage = itemStack.getItemDamage();
                    int maxDamage = itemStack.getMaxDamage();

                    if (maxDamage != 0 &&
                    		(1-(double)itemDamage / maxDamage) <= durabilityDisplayThresholdForItem)
                    {
                        damagedItemsList.add(itemStack);
                    }
                }
            }
        }
    }

    /**
     * Examines the players current armor and sees if any of them are damaged.
     * It adds damaged armors to the damagedItemsList list.
     */
    private static void CalculateDurabilityIconsForArmor()
    {
        ItemStack[] armorStacks = mc.thePlayer.inventory.armorInventory;
        
        //iterate backwards over the armor the user is wearing so the helm is displayed first
        for(int i = armorStacks.length-1; i >= 0 ; i--)
        {
        	ItemStack armorStack = armorStacks[i];
            if (armorStack != null)
            {
                int itemDamage = armorStack.getItemDamage();
                int maxDamage = armorStack.getMaxDamage();

                if (maxDamage != 0 &&
                        (1-(double)itemDamage / maxDamage) <= durabilityDisplayThresholdForArmor)
                {
                    damagedItemsList.add(armorStack);
                }
            }
        }
    }
    
    /**
     * Takes off any armor the player is wearing if it is close to being destroyed,
     * and puts it in their inventory if the player has room in their inventory.
     */
    private static void UnequipDamagedArmor()
    {
    	if(AutoUnequipArmor)
    	{
            ItemStack[] armorStacks = mc.thePlayer.inventory.armorInventory;
            
            //iterate over the armor the user is wearing
            for(int i = 0; i < armorStacks.length; i++)
            {
            	ItemStack armorStack = armorStacks[i];
                if (armorStack != null)
                {
                    int itemDamage = armorStack.getItemDamage();
                    int maxDamage = armorStack.getMaxDamage();
                    
                    if (maxDamage != 0 &&
                    		maxDamage - itemDamage < 5)
                    {
                       InventoryUtil.MoveArmorIntoPlayerInventory(i);
                    }
                }
            }
    	}
    }

    /**
     * Takes off any tools the player is using if it is close to being destroyed,
     * and puts it in their inventory if the player has room in their inventory.
     */
    private static void UnequipDamagedTool()
    {
    	if(AutoUnequipTools)
    	{
            ItemStack itemStack = mc.thePlayer.inventory.getCurrentItem();

            if (itemStack != null)
            {
                Item item = itemStack.getItem();

                if (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemBow || item instanceof ItemHoe
                        || item instanceof ItemShears || item instanceof ItemFishingRod)
                {
                    int itemDamage = itemStack.getItemDamage();
                    int maxDamage = itemStack.getMaxDamage();
                    int threshold = (item instanceof ItemFishingRod)
                        ? 5
                        : 15;

                    if (maxDamage != 0 &&
                    		maxDamage - itemDamage < threshold)
                    {
                    	InventoryUtil.MoveHeldItemIntoPlayerInventory();
                    }
                }
            }
    	}
    }
    

    /**
     * Checks to see if the Durability Info tab is selected in GuiZyinHUDOptions
     * @return
     */
    private static boolean TabIsSelectedInOptionsGui()
    {
    	return mc.currentScreen instanceof GuiZyinHUDOptions &&
    		(((GuiZyinHUDOptions)mc.currentScreen).IsButtonTabSelected(Localization.get("durabilityinfo.name")));
    }
    
    public static float GetDurabilityDisplayThresholdForArmor()
    {
		return durabilityDisplayThresholdForArmor;
	}

	public static void SetDurabilityDisplayThresholdForArmor(float durabilityDisplayThreshold)
	{
		durabilityDisplayThresholdForArmor = durabilityDisplayThreshold;
		CalculateDurabilityIcons();
	}

    public static float GetDurabilityDisplayThresholdForItem()
    {
		return durabilityDisplayThresholdForItem;
	}

	public static void SetDurabilityDisplayThresholdForItem(float durabilityDisplayThreshold)
	{
		durabilityDisplayThresholdForItem = durabilityDisplayThreshold;
		CalculateDurabilityIcons();
	}
    
    private static String GetDamageString(int currentDamage, int maxDamage)
    {
        if(ShowDamageAsPercentage)
        	return 100 - (int)((double)currentDamage / maxDamage * 100) + "%";
        else
        	return (maxDamage - currentDamage) + "";
    }

    /**
     * Gets the horizontal location where the durability icons are rendered.
     * @return
     */
    public static int GetHorizontalLocation()
    {
    	return durabalityLocX;
    }
    
    /**
     * Sets the horizontal location where the durability icons are rendered.
     * @param x
     * @return the new x location
     */
    public static int SetHorizontalLocation(int x)
    {
    	if(x < 0)
    		x = 0;
    	else if(x > mc.displayWidth)
    		x = mc.displayWidth;
    	
    	durabalityLocX = x;
    	equipmentLocX = durabalityLocX + armorDurabilityIconX;
    	return durabalityLocX;
    }
    
    /**
     * Gets the vertical location where the durability icons are rendered.
     * @return
     */
    public static int GetVerticalLocation()
    {
    	return durabalityLocY;
    }

    /**
     * Sets the vertical location where the durability icons are rendered.
     * @param y
     * @return the new y location
     */
    public static int SetVerticalLocation(int y)
    {
    	if(y < 0)
    		y = 0;
    	else if(y > mc.displayHeight)
    		y = mc.displayHeight;
    	
    	durabalityLocY = y;
    	equipmentLocY = durabalityLocY;
    	return durabalityLocY;
    }
    
    /**
     * Toggles showing durability for armor
     * @return 
     */
    public static boolean ToggleShowArmorDurability()
    {
    	return ShowArmorDurability = !ShowArmorDurability;
    }
    /**
     * Toggles showing durability for items
     * @return 
     */
    public static boolean ToggleShowItemDurability()
    {
    	return ShowItemDurability = !ShowItemDurability;
    }
    /**
     * Toggles showing percentages for item durability
     * @return 
     */
    public static boolean ToggleShowDamageAsPercent()
    {
    	return ShowDamageAsPercentage = !ShowDamageAsPercentage;
    }
    /**
     * Toggles showing icons or an image for broken armor
     * @return 
     */
    public static boolean ToggleShowIndividualArmorIcons()
    {
    	return ShowIndividualArmorIcons = !ShowIndividualArmorIcons;
    }
    /**
     * Toggles unequipping breaking armor
     * @return 
     */
    public static boolean ToggleAutoUnequipArmor()
    {
    	return AutoUnequipArmor = !AutoUnequipArmor;
    }
    /**
     * Toggles unequipping breaking tools
     * @return 
     */
    public static boolean ToggleAutoUnequipTools()
    {
    	return AutoUnequipTools = !AutoUnequipTools;
    }
}
