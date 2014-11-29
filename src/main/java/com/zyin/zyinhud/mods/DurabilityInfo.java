package com.zyin.zyinhud.mods;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.zyin.zyinhud.ZyinHUDRenderer;
import com.zyin.zyinhud.gui.GuiZyinHUDOptions;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ModCompatibility;

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
    public static boolean UseColoredNumbers;
    public static float DurabilityScale = 1f;

    public static final int durabilityUpdateFrequency = 600;

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
                (mc.inGameHasFocus || (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || TabIsSelectedInOptionsGui()))) &&
        		!mc.gameSettings.showDebugInfo)
        {
            //don't waste time recalculating things every tick
        	if(System.currentTimeMillis() - lastGenerate > durabilityUpdateFrequency)
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
                        GL11.glScalef(DurabilityScale, DurabilityScale, DurabilityScale);
                        
                    	if(ShowIndividualArmorIcons)
                    	{
                            int x = (int) Math.floor(durabalityLocX / DurabilityScale);
                            int y = (int) Math.floor(durabalityLocY / DurabilityScale) + (numArmors * toolY);

                            
                            DrawItemIcon(itemStack, x, y);
                            
                            numArmors++;
                    	}
                    	else
                    	{
                            int x = (int) Math.floor(durabalityLocX / DurabilityScale);
                            int y = (int) Math.floor(durabalityLocY / DurabilityScale);
                            
                            DrawBrokenArmorTexture(x, y);
                    	}
                        
                        GL11.glScalef(1f/DurabilityScale, 1f/DurabilityScale, 1f/DurabilityScale);
                    }
                }
                else //if this tool is an equipment/tool
                {
                    if (ShowItemDurability)
                    {
                        int x = (int) Math.floor(durabalityLocX / DurabilityScale);
                        int y = (int) Math.floor(durabalityLocY / DurabilityScale) + (numTools * toolY);

                        if (armorExists && ShowArmorDurability)
                            //x = (int) Math.floor(equipmentLocX / DurabilityScale);    //if armor is being rendered then push this to the right
                        	x += toolX;
                        
                        //x /= DurabilityScale;
                        //y /= DurabilityScale;
                        GL11.glScalef(DurabilityScale, DurabilityScale, DurabilityScale);
                        
                        DrawItemIcon(itemStack, x, y);
                        
                        GL11.glScalef(1f/DurabilityScale, 1f/DurabilityScale, 1f/DurabilityScale);
                        
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
		//itemRenderer.renderItemAndEffectIntoGUI(mc.fontRendererObj, mc.renderEngine, itemStack, x, y);
		itemRenderer.func_180450_b(itemStack, x, y);	//func_180450_b() is renderItemAndEffectIntoGUI()

		//render the item's durability bar
		//itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, mc.renderEngine, itemStack, x, y);
		itemRenderer.func_175030_a(mc.fontRendererObj, itemStack, x, y);	//func_175030_a() is renderItemOverlayIntoGUI()
		
		GL11.glDisable(GL11.GL_LIGHTING);	//the itemRenderer.renderItem() method enables lighting
		GL11.glDisable(GL11.GL_DEPTH_TEST);	//so the text renders above the item
		
		//render the number of durability it has left
		if(itemStack.getItemDamage() != 0)
		{
			boolean unicodeFlag = mc.fontRendererObj.getUnicodeFlag();
			mc.fontRendererObj.setUnicodeFlag(true);
			
			String damageString;
			int itemDamage = itemStack.getItemDamage();
			int itemMaxDamage = itemStack.getMaxDamage();
			
			if(ShowDamageAsPercentage)
				damageString = 100 - (int)((double)itemDamage / itemMaxDamage * 100) + "%";
			else
			{
				if(ModCompatibility.TConstruct.IsTConstructItem(itemStack.getItem()))
				{
					Integer temp = ModCompatibility.TConstruct.GetDamage(itemStack);
					if(temp != null)
					{
						itemDamage = temp;
						itemMaxDamage = ModCompatibility.TConstruct.GetMaxDamage(itemStack);
						damageString = Integer.toString(itemMaxDamage - itemDamage);
					}
					else
						damageString = "";
				}
				else
					damageString = Integer.toString(itemMaxDamage - itemDamage);
				
			}
			
			int damageX = x + toolX - mc.fontRendererObj.getStringWidth(damageString);
			int damageY = y + toolY - mc.fontRendererObj.FONT_HEIGHT - 1;
			int damageColor = 0xffffff;
			if(UseColoredNumbers)
				damageColor = GetDamageColor(itemStack.getItemDamage(), itemStack.getMaxDamage());
			
			mc.fontRendererObj.func_175063_a(damageString, damageX, damageY, damageColor);
			mc.fontRendererObj.setUnicodeFlag(unicodeFlag);
		}
	}
	
	protected static int GetDamageColor(int currentDamage, int maxDamage)
	{
		float percent = 100 - (int)((double)currentDamage / maxDamage * 100);
		
		if(percent < 50)
			return (int)(0xff0000 + ((int)(0xff * percent/50) << 8));
		else
			return (int)(0x00ff00 + ((int)(0xff * (100 - (percent-50)*2)/100) << 16));
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
		
		ZyinHUDRenderer.RenderCustomTexture(x, y, 
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
     * Examines the players first 9 inventory slots (the players hotbar) and sees if any tools are damaged.
     * It adds damaged tools to the static damagedItemsList list.
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
                if (IsTool(item))
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
     * It adds damaged armors to the static damagedItemsList list.
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
     * Determines if the item is a tool. Pickaxe, sword, bow, shears, etc.
     * @param item
     * @return
     */
    private static boolean IsTool(Item item)
    {
    	return item instanceof ItemTool
	    	|| item instanceof ItemSword
	    	|| item instanceof ItemBow
	    	|| item instanceof ItemHoe
	        || item instanceof ItemShears
	        || item instanceof ItemFishingRod
	        || ModCompatibility.TConstruct.IsTConstructHarvestTool(item)
	        || ModCompatibility.TConstruct.IsTConstructWeapon(item)
	        || ModCompatibility.TConstruct.IsTConstructBow(item);
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
                    int threshold = (item instanceof ItemFishingRod) ? 5 : 15;

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
    	durabalityLocX = MathHelper.clamp_int(x, 0, mc.displayWidth);
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
    	durabalityLocY = MathHelper.clamp_int(y, 0, mc.displayHeight);
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
    /**
     * Toggles using color
     * @return 
     */
    public static boolean ToggleUseColoredNumbers()
    {
    	return UseColoredNumbers = !UseColoredNumbers;
    }
}
