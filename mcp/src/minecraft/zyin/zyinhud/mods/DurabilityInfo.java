package zyin.zyinhud.mods;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

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
    
    public static boolean ShowArmorDurability;
    public static boolean ShowItemDurability;
    public static int DurabilityUpdateFrequency;
    public static float DurabilityDisplayThresholdForArmor;
    public static float DurabilityDisplayThresholdForItem;
    public static boolean ShowIndividualArmorIcons;
    public static boolean ShowDamageAsPercentage;
	

	protected static final ResourceLocation RESOURCE_DURABILITY_ICONS_PNG = new ResourceLocation("textures/durability_icons.png");

    //U and V is the top left part of the image
    //X and Y is the width and height of the image
    protected static int armorDurabilityU = 0;
    protected static int armorDurabilityV = 0;
    protected static int armorDurabilityX = 1 * 16;
    protected static int armorDurabilityY = 2 * 16;

    //the height/width of the tools being rendered
    public static int toolX = 1 * 16;
    public static int toolY = 1 * 16;

    //where the armor icon is rendered (these values replaced by the config settings)
    public static int durabalityLocX = 20;
    public static int durabalityLocY = 20;

    //where the tool icons are rendered (these values replaced by the config settings)
    protected static int equipmentLocX = 20 + armorDurabilityX;
    protected static int equipmentLocY = 20;

    private static Minecraft mc = Minecraft.getMinecraft();
    private static ArrayList<ItemStack> damagedItemsList = new ArrayList<ItemStack>(13);	//used to push items into the list of broken equipment to render
    private static final RenderItem itemRenderer = new RenderItem();
    private static final GuiIngame gig = new GuiIngame(mc);
    private static final TextureManager textureManager = mc.func_110434_K();

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
        	long frameTime = System.currentTimeMillis();
        	if(frameTime - lastGenerate > DurabilityUpdateFrequency)
            {
                CalculateDurabilityIcons();
            }

            boolean armorExists = false;

            for (ItemStack toolStack : damagedItemsList)
            {
                if (toolStack.getItem() instanceof ItemArmor)
                {
                    armorExists = true;
                }
            }

            int numTools = 0;
            int numArmors = 0;

            for (ItemStack toolStack : damagedItemsList)
            {
                Item tool = toolStack.getItem();

                //if this tool is an armor
                if (tool instanceof ItemArmor)
                {
                    if (ShowArmorDurability)
                    {
                    	if(ShowIndividualArmorIcons)
                    	{
                            int verticalPadding = 0;
                            int verticalSpacer = durabalityLocY + (numArmors * toolY) + verticalPadding;
                            int horizontalPosition = durabalityLocX;
                            
                            //render the armor with enchant effect
                            itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, toolStack, horizontalPosition, verticalSpacer);
                            //render the armor's durability bar
                            itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, toolStack, horizontalPosition, verticalSpacer);
                            
                            String damageString = GetDamageString(toolStack.getItemDamage(), toolStack.getMaxDamage());
                            
                            GL11.glDisable(GL11.GL_LIGHTING);	//this is needed because the itemRenderer.renderItem() method enables lighting
                            mc.fontRenderer.setUnicodeFlag(true);
                            int damageX = horizontalPosition + toolX - mc.fontRenderer.getStringWidth(damageString);
                            int damageY = verticalSpacer + toolY - mc.fontRenderer.FONT_HEIGHT - 1;
                            mc.fontRenderer.drawStringWithShadow(damageString, damageX, damageY, 0xffffff);
                            mc.fontRenderer.setUnicodeFlag(false);
                            numArmors++;
                    	}
                    	else
                    	{
                            GL11.glDisable(GL11.GL_LIGHTING);	//disable lighting so it renders at full brightness
                            
                            GL11.glEnable (GL11.GL_BLEND);	//for a transparent texture
                            GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            
                            //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture(DURABILITY_ICONS_PNG));	//ORIGINAL
                            //bind texture
                            textureManager.func_110577_a(RESOURCE_DURABILITY_ICONS_PNG);
                            GL11.glColor4f(255f, 255f, 255f, 255f);	//fixes transparency issue when a InfoLine Notification is displayed
                            gig.drawTexturedModalRect(durabalityLocX, durabalityLocY, armorDurabilityU, armorDurabilityV, armorDurabilityX, armorDurabilityY);

                            GL11.glDisable (GL11.GL_BLEND);
                    	}
                    }
                }
                else //if this tool is an equipment/tool
                {
                    if (ShowItemDurability)
                    {
                        int verticalPadding = 0;
                        int verticalSpacer = equipmentLocY + (numTools * toolY) + verticalPadding;
                        int horizontalPosition = durabalityLocX;

                        if (armorExists && ShowArmorDurability)
                        {
                            horizontalPosition = equipmentLocX;    //if armor is being rendered then push this to the right
                        }

                        //render the item with enchant effect
                        itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, toolStack, horizontalPosition, verticalSpacer);
                        //render the item's durability bar
                        itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, toolStack, horizontalPosition, verticalSpacer);
                        
                        String damageString = GetDamageString(toolStack.getItemDamage(), toolStack.getMaxDamage());
                        
                        GL11.glDisable(GL11.GL_LIGHTING);	//this is needed because the itemRenderer.renderItem() method enables lighting
                        mc.fontRenderer.setUnicodeFlag(true);
                        int damageX = horizontalPosition + toolX - mc.fontRenderer.getStringWidth(damageString);
                        int damageY = verticalSpacer + toolY - mc.fontRenderer.FONT_HEIGHT - 1;
                        mc.fontRenderer.drawStringWithShadow(damageString, damageX, damageY, 0xffffff);
                        mc.fontRenderer.setUnicodeFlag(false);
                        numTools++;
                    }
                }
            }
            
        }
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
                    		(1-(double)itemDamage / maxDamage) < DurabilityDisplayThresholdForItem)
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
                        (1-(double)itemDamage / maxDamage) < DurabilityDisplayThresholdForArmor)
                {
                    damagedItemsList.add(armorStack);
                }
            }
        }
    }
    
    private static String GetDamageString(int currentDamage, int maxDamage)
    {
        if(ShowDamageAsPercentage)
        	return (int)((double)currentDamage / maxDamage * 100) + "%";
        else
        	return (maxDamage - currentDamage) + "";
    }

    /**
     * Gets the horizontal location where the durability icons are rendered.
     * @return
     */
    public static int GetDurabalityHorizontalLocation()
    {
    	return durabalityLocX;
    }
    
    /**
     * Sets the horizontal location where the durability icons are rendered.
     * @param x
     * @return the new x location
     */
    public static int SetDurabalityHorizontalLocation(int x)
    {
    	if(x < 0)
    		x = 0;
    	else if(x > mc.displayWidth)
    		x = mc.displayWidth;
    	
    	durabalityLocX = x;
    	equipmentLocX = durabalityLocX + armorDurabilityX;
    	return durabalityLocX;
    }
    
    /**
     * Gets the vertical location where the durability icons are rendered.
     * @return
     */
    public static int GetDurabalityVerticalLocation()
    {
    	return durabalityLocY;
    }

    /**
     * Sets the vertical location where the durability icons are rendered.
     * @param y
     * @return the new y location
     */
    public static int SetDurabalityVerticalLocation(int y)
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
