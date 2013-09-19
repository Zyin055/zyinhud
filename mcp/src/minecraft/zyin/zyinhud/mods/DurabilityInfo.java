package zyin.zyinhud.mods;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import zyin.zyinhud.util.ZyinHUDUtil;

/**
 * Durability Info checks to see if any equipment (items in the hotbar, and armor) is damaged
 * and then displays info about them onto the HUD.
 */
public class DurabilityInfo
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

    private static Minecraft mc = Minecraft.getMinecraft();
	protected static final ResourceLocation durabilityIconsResourceLocation = new ResourceLocation("textures/durability_icons.png");
    
    public static boolean ShowArmorDurability;
    public static boolean ShowItemDurability;
    public static int DurabilityUpdateFrequency;
    public static float DurabilityDisplayThresholdForArmor;
    public static float DurabilityDisplayThresholdForItem;
    public static boolean ShowIndividualArmorIcons;
    public static boolean ShowDamageAsPercentage;
	

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

    private static ArrayList<ItemStack> damagedItemsList = new ArrayList<ItemStack>(13);	//used to push items into the list of broken equipment to render
    private static final RenderItem itemRenderer = new RenderItem();

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
        //and not in a menu
        //and F3 not shown
        if (DurabilityInfo.Enabled &&
                (mc.inGameHasFocus || mc.currentScreen == null || (mc.currentScreen instanceof GuiChat))
                && !mc.gameSettings.showDebugInfo)
        {
            //don't waste time recalculating things every tick
        	if(System.currentTimeMillis() - lastGenerate > DurabilityUpdateFrequency)
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

	protected static void DrawItemIcon(ItemStack toolStack, int horizontalPosition, int verticalSpacer)
	{
		//render the item with enchant effect
		itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, toolStack, horizontalPosition, verticalSpacer);
		
		//render the item's durability bar
		itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, toolStack, horizontalPosition, verticalSpacer);
		
		GL11.glDisable(GL11.GL_LIGHTING);	//this is needed because the itemRenderer.renderItem() method enables lighting

		
		mc.fontRenderer.setUnicodeFlag(true);
		String damageString = GetDamageString(toolStack.getItemDamage(), toolStack.getMaxDamage());
		int damageX = horizontalPosition + toolX - mc.fontRenderer.getStringWidth(damageString);
		int damageY = verticalSpacer + toolY - mc.fontRenderer.FONT_HEIGHT - 1;
		mc.fontRenderer.drawStringWithShadow(damageString, damageX, damageY, 0xffffff);
		mc.fontRenderer.setUnicodeFlag(false);
	}
    
    /***
     * Draws the broken durability icon
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
        //and not in a menu
        //and not typing
        if ((mc.inGameHasFocus || mc.currentScreen == null || (mc.currentScreen instanceof GuiChat))
                && !mc.gameSettings.keyBindPlayerList.pressed)
        {
            damagedItemsList.clear();
            CalculateDurabilityIconsForItems();
            CalculateDurabilityIconsForArmor();
            lastGenerate = System.currentTimeMillis();
        }
    }

    /**
     * Examines the players first 9 inventory slots (the players inventory) and sees if any tools are damaged.
     * It adds damaged tools to the damagedItemsList list.
     */
    private static void CalculateDurabilityIconsForItems()
    {
        ItemStack[] items = mc.thePlayer.inventory.mainInventory;

        for (int i = 0; i < 9; i++)
        {
            ItemStack itemStack = items[i];

            if (itemStack != null)
            {
                Item item = itemStack.getItem();

                if (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemBow || item instanceof ItemHoe)
                {
                    int itemDamage = itemStack.getItemDamage();
                    int maxDamage = itemStack.getMaxDamage();

                    if (maxDamage != 0 &&
                    		(1-(double)itemDamage / maxDamage) <= DurabilityDisplayThresholdForItem)
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
                        (1-(double)itemDamage / maxDamage) <= DurabilityDisplayThresholdForArmor)
                {
                    damagedItemsList.add(armorStack);
                }
            }
        }
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
    	ShowArmorDurability = !ShowArmorDurability;
    	return ShowArmorDurability;
    }
    /**
     * Toggles showing durability for items
     * @return 
     */
    public static boolean ToggleShowItemDurability()
    {
    	ShowItemDurability = !ShowItemDurability;
    	return ShowItemDurability;
    }
    /**
     * Toggles showing percentages for item durability
     * @return 
     */
    public static boolean ToggleShowDamageAsPercent()
    {
    	ShowDamageAsPercentage = !ShowDamageAsPercentage;
    	return ShowDamageAsPercentage;
    }
    /**
     * Toggles showing icons or an image for broken armor
     * @return 
     */
    public static boolean ToggleShowIndividualArmorIcons()
    {
    	ShowIndividualArmorIcons = !ShowIndividualArmorIcons;
    	return ShowIndividualArmorIcons;
    }
}
