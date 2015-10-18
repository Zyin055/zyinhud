package com.zyin.zyinhud.mods;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;

import com.zyin.zyinhud.ZyinHUDRenderer;
import com.zyin.zyinhud.mods.Coordinates.Modes;
import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * Eating Helper allows the player to eat food in their inventory by calling its Eat() method.
 */
public class EatingAid extends ZyinHUDModBase
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
        BASIC(Localization.get("eatingaid.mode.basic")),
        INTELLIGENT(Localization.get("eatingaid.mode.intelligent"));
        
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
        	return ToggleMode(true);
        }
        /**
         * Sets the next availble mode for this mod if forward=true, or previous mode if false
         */
        public static Modes ToggleMode(boolean forward)
        {
        	if (forward)
        		return Mode = Mode.ordinal() < Modes.values().length - 1 ? Modes.values()[Mode.ordinal() + 1] : Modes.values()[0];
        	else
        		return Mode = Mode.ordinal() > 0 ? Modes.values()[Mode.ordinal() - 1] : Modes.values()[Modes.values().length - 1];
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
    
    /** Such as golden carrots, golden apples */
    public static boolean EatGoldenFood;
    /** Such as raw chicken/porkchop/beef */
    public static boolean EatRawFood;
    /** Food found on the hotbar will be chosen over food found in the inventory */
    public static boolean PrioritizeFoodInHotbar;
    /** Treat mushroom stew as instant-eat */
    public static boolean UsePvPSoup;
    
    private Timer timer = new Timer();
    private TimerTask swapTimerTask;
    private TimerTask eatTimerTask;

    private Robot r = null;
    private boolean isCurrentlyEating;
    private boolean previousEatFromHotbar;

    private int foodItemIndex;
    private int currentItemInventoryIndex;
    private int currentItemHotbarIndex;
    
    
    /**
     * Use this instance for all method calls.
     */
    public static EatingAid instance = new EatingAid();

    private EatingAid()
    {
        try
        {
            r = new Robot();
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }

        isCurrentlyEating = false;
        previousEatFromHotbar = false;
    }

    /**
     * Makes the player eat a food item on their hotbar or in their inventory.
     */
    public void Eat()
    {
        //currentItemStack.onFoodEaten(mc.theWorld, mc.thePlayer);	//INSTANT EATING (single player only)
    	
        //make sure we're not about to click on a right-clickable thing, and we're not in creative mode
        if(ZyinHUDUtil.IsMouseoveredBlockRightClickable() || mc.playerController.isInCreativeMode())
        	return;

        if (isCurrentlyEating)
        {
            //if we're eating and we try to eat again, then cancel whatever we're eating
            //by releasing right click, and swapping the food back to its correct position
            StopEating();
            return;
        }
        else
        {
            //we need to eat something by first finding the best food to eat, then eat it
            if(!mc.thePlayer.getFoodStats().needFood() && !UsePvPSoup)
            {
                //if we're not hungry then don't do anything
                return;
            }
            
            foodItemIndex = GetFoodItemIndexFromInventory();
        	if(foodItemIndex < 0)
            {
        		ZyinHUDRenderer.DisplayNotification(Localization.get("eatingaid.nofood"));
                return;
            }
            
            if(foodItemIndex  > 35 && foodItemIndex < 45)	//on the hotbar
            {
            	StartEatingFromHotbar(foodItemIndex);
            }
            else //if(foodItemIndex  > 8 && foodItemIndex < 36)	//in the inventory
            {
            	StartEatingFromInventory(foodItemIndex);
            }
            
        }
    }
    
    /**
     * Changes the selected index in your hotbar to where the food is, then eats it.
     * @param foodHotbarIndex 36-44
     */
    private void StartEatingFromHotbar(int foodHotbarIndex)
    {
    	if(foodHotbarIndex < 36 || foodHotbarIndex > 44)
    		return;

		Slot slotToUse = (Slot)mc.thePlayer.inventoryContainer.inventorySlots.get(foodHotbarIndex);
		ItemFood food = (ItemFood)(slotToUse.getStack().getItem());
		
    	if(UsePvPSoup && food.equals(Items.mushroom_stew) &&
    			(mc.thePlayer.getHealth() < 20 || mc.thePlayer.getFoodStats().needFood()))
    	{
    		int previouslySelectedHotbarSlotIndex = mc.thePlayer.inventory.currentItem;
    		mc.thePlayer.inventory.currentItem = InventoryUtil.TranslateInventoryIndexToHotbarIndex(foodHotbarIndex);
    		
    		InventoryUtil.SendUseItem();
    		
    		mc.thePlayer.inventory.currentItem = previouslySelectedHotbarSlotIndex;
    	}
    	else if(mc.thePlayer.getFoodStats().needFood())
    	{
        	currentItemHotbarIndex = mc.thePlayer.inventory.currentItem;
        	foodHotbarIndex = InventoryUtil.TranslateInventoryIndexToHotbarIndex(foodHotbarIndex);
        	
        	int previouslySelectedHotbarSlotIndex = mc.thePlayer.inventory.currentItem;
        	mc.thePlayer.inventory.currentItem = foodHotbarIndex;

            r.mousePress(InputEvent.BUTTON3_MASK); //perform a right click
            isCurrentlyEating = true;
            previousEatFromHotbar = true;
            
        	ItemStack currentItemStack = mc.thePlayer.getHeldItem();
        	ItemFood currentFood = (ItemFood)currentItemStack.getItem();
        	
            int eatingDurationInMilliseconds = 1000*currentFood.itemUseDuration / 20;
            
            //after this timer runs out we'll release right click to stop eating and select the previously selected item
            eatTimerTask = new StopEatingTimerTask(r, previouslySelectedHotbarSlotIndex);
            timer.schedule(eatTimerTask, eatingDurationInMilliseconds + InventoryUtil.GetSuggestedItemSwapDelay());
    	}
    }
    
    /**
     * Swaps an item from your inventory into your hotbar, then eats it.
     * @param foodInventoryIndex 9-35
     */
    private void StartEatingFromInventory(int foodInventoryIndex)
    {
    	if(foodInventoryIndex < 9 || foodInventoryIndex > 35)
    		return;

		Slot slotToUse = (Slot)mc.thePlayer.inventoryContainer.inventorySlots.get(foodInventoryIndex);
		ItemFood food = (ItemFood)(slotToUse.getStack().getItem());
		
		//if PvP Soup is on and we don't need eat it, then return
    	if(UsePvPSoup && food.equals(Items.mushroom_stew) && mc.thePlayer.getHealth() >= 20 && !mc.thePlayer.getFoodStats().needFood())
    		return;
    	
        currentItemInventoryIndex = InventoryUtil.GetCurrentlySelectedItemInventoryIndex();
        InventoryUtil.Swap(currentItemInventoryIndex, foodInventoryIndex);
        
        r.mousePress(InputEvent.BUTTON3_MASK); //perform a right click
        previousEatFromHotbar = false;
        
        ItemStack currentItemStack = mc.thePlayer.getHeldItem();
        ItemFood currentFood = (ItemFood)currentItemStack.getItem();
        
        int eatingDurationInMilliseconds = 1000 * currentFood.itemUseDuration / 20;
        
        if(UsePvPSoup && food.equals(Items.mushroom_stew) &&
    			(mc.thePlayer.getHealth() < 20 || mc.thePlayer.getFoodStats().needFood()))	//for PvP Soup eating
        {
            isCurrentlyEating = false;
            r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
            eatingDurationInMilliseconds = 1;
            
        	InventoryUtil.SendUseItem();
        }
        else if(mc.thePlayer.getFoodStats().needFood())	//for normal eating
        {
            isCurrentlyEating = true;
            
            //after this timer runs out we'll release right click to stop eating
            eatTimerTask = new StopEatingTimerTask(r);
            timer.schedule(eatTimerTask, eatingDurationInMilliseconds);
        }
        else	//for if we try to eat something but aren't hungry
        {
        	eatingDurationInMilliseconds = 1;
        	r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
        }
        
        swapTimerTask = InventoryUtil.instance.SwapWithDelay(currentItemInventoryIndex, foodInventoryIndex,
        		eatingDurationInMilliseconds + InventoryUtil.GetSuggestedItemSwapDelay());
        
    }
    
    

    /**
     * Stops eating by releasing right click and moving the food back to its original position.
     */
    public void StopEating()
    {
    	if(previousEatFromHotbar)
    		StopEatingFromHotbar();
    	else
    		StopEatingFromInventory();
    }

    private void StopEatingFromInventory()
    {
        r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
        eatTimerTask.cancel();
    	swapTimerTask.cancel();
        InventoryUtil.Swap(currentItemInventoryIndex, foodItemIndex);
        isCurrentlyEating = false;
    }
    private void StopEatingFromHotbar()
    {
        r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
        eatTimerTask.cancel();
        mc.thePlayer.inventory.currentItem = currentItemHotbarIndex;
        isCurrentlyEating = false;
    }

    /**
     * Are we currently eating food?
     * @return
     */
    public boolean isEating()
    {
        return isCurrentlyEating;
    }
    
    
    
    public int GetFoodItemIndexFromInventory()
    {
    	if(Mode == Modes.BASIC)
    		return GetStrongestFoodItemIndexFromInventory();
    	else if(Mode == Modes.INTELLIGENT)
    		return GetBestFoodItemIndexFromInventory();
    	else
    		return -2;
    }
    
    /**
     * Finds the food with the highest saturation value and returns its index in your inventory.
     * @return
     */
    public int GetStrongestFoodItemIndexFromInventory()
    {
    	List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;
        int bestFoodMatchIndex = -1;
        float bestFoodMatchSaturation = 0;
        int foodLevel = mc.thePlayer.getFoodStats().getFoodLevel();	//max 20
        
        //iterate over the hotbar (36-44), then main inventory (9-35)
        for (int i = inventorySlots.size() - 1; i > 8; i--)
        {
        	if(PrioritizeFoodInHotbar
        		&& i == 35 && bestFoodMatchIndex > -1)
        		return bestFoodMatchIndex;
        	
        	
            Slot slot = (Slot)inventorySlots.get(i);
            ItemStack itemStack = slot.getStack();

            if (itemStack == null)
            {
                continue;
            }

            Item item = itemStack.getItem();
            
            if (item instanceof ItemFood)
            {
                ItemFood food = (ItemFood)item;
                float saturationModifier = food.getSaturationModifier(itemStack);
                
                Integer potionId = ZyinHUDUtil.GetFieldByReflection(ItemFood.class, food, "potionId", "aaaaaa");
                if (potionId == null) potionId = -1;
                
                if (UsePvPSoup && item.equals(Items.mushroom_stew))
                {
                	saturationModifier = 1000f;	//setting the saturation value very high will make it appealing to the food selection algorithm
                }
                else if (potionId == Potion.saturation.id
                		|| potionId == Potion.heal.id)	//modded foods like [Botania] Mana Cookie may have these effects
                {
                	saturationModifier = 999;	//setting the saturation value very high will make it appealing to the food selection algorithm
                }
                else if (item.equals(Items.golden_carrot)
                        || item.equals(Items.golden_apple))
                {
                    if (!EatGoldenFood)
                    {
                        continue;
                    }

                    saturationModifier = 0.0001f;	//setting the saturation value low will make it unappealing to the food selection algorithm
                }
                /*else if (item.equals(Items.chicken)	//raw chicken gives Potion.hunger effect
		        		|| item.equals(Items.porkchop)
		        		|| item.equals(Items.beef)
		        		|| item.equals(Items.mutton)
		        		|| item.equals(Items.rabbit)
		            	|| item.equals(Items.fish))*/	//Items.fish refers to UNCOOKED fish: Raw Fish, Raw Salmon, Pufferfish, Clownfish. All have a Potion id of 0
                else if(HasSmeltingRecipe(itemStack))
                {
                    if (!EatRawFood)
                    {
                        continue;
                    }

                    saturationModifier = 0.0003f;	//setting the saturation value low will make it unappealing to the food selection algorithm
                }
                else if (potionId == Potion.poison.id
                		|| potionId == Potion.hunger.id
                        || potionId == Potion.confusion.id
                        || FishType.getFishTypeForItemStack(itemStack) == FishType.PUFFERFISH) //FishType.func_150978_a(ItemStack) will probably have a friendly name like "getFishTypeFromItemStack()"
                {
                	saturationModifier = 0.0002f;	//setting the saturation value low will make it unappealing to the food selection algorithm
                }
                
                if(saturationModifier > bestFoodMatchSaturation)
                {
                	bestFoodMatchIndex = i;
                	bestFoodMatchSaturation = saturationModifier;
                	continue;
                }
            }
        }

        if (bestFoodMatchIndex > -1)
            return bestFoodMatchIndex;
        else
            return -1;
    }
    
    
    /**
     * Determines the best food that you can eat and returns its index in your inventory.
     * The best food is defined by not over eating (not wasting food), but still healing the most hunger.
     * @return the index in your inventory that has the best food to eat (9-34), or -1 if no food found.
     */
    public int GetBestFoodItemIndexFromInventory()
    {
        List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;
        int bestFoodMatchIndex = -1;
        int bestFoodMatchOvereat = 999;
        int bestFoodMatchHeal = -999;
        int foodLevel = mc.thePlayer.getFoodStats().getFoodLevel();	//max 20
        
        //iterate over the hotbar (36-44), then main inventory (9-35)
        for (int i = inventorySlots.size() - 1; i > 8; i--)
        {
        	if(PrioritizeFoodInHotbar
        		&& i == 35 && bestFoodMatchIndex > -1)
        		return bestFoodMatchIndex;
        	
        	
            Slot slot = (Slot)inventorySlots.get(i);
            ItemStack itemStack = slot.getStack();

            if (itemStack == null)
            {
                continue;
            }

            Item item = itemStack.getItem();

            if (item instanceof ItemFood)
            {
                ItemFood food = (ItemFood)item;
                int foodNeeded = 20 - foodLevel;	//amount of hunger needed to be full
                int heal = food.getHealAmount(itemStack);	//amount of hunger restored by eating this food
                int overeat = foodNeeded - heal;
                overeat = (overeat > 0) ? 0 : Math.abs(overeat);	//positive number, amount we would overeat by eating this food
                
                Integer potionId = ZyinHUDUtil.GetFieldByReflection(ItemFood.class, food, "potionId", "field_77851_ca");
                if (potionId == null) potionId = -1;

                if (UsePvPSoup && item.equals(Items.mushroom_stew))
                {
                	overeat = -1000;	//setting the overeat value very low will make it appealing to the food selection algorithm
                }
                else if (potionId == Potion.saturation.id
                		|| potionId == Potion.heal.id)	//modded foods like [Botania] Mana Cookie may have these effects
                {
                	overeat = -999;	//setting the overeat value very low will make it appealing to the food selection algorithm
                }
                else if (item.equals(Items.golden_carrot)
                        || item.equals(Items.golden_apple))	//golden food gives Potion.regeneration effect
                {
                    if (!EatGoldenFood)
                    {
                        continue;
                    }

                    overeat = 999;	//setting the overeat value high will make it unappealing to the food selection algorithm
                }
                /*else if (item.equals(Items.chicken)	//raw chicken gives Potion.hunger effect
                		|| item.equals(Items.porkchop)
                		|| item.equals(Items.beef)
                		|| item.equals(Items.mutton)
                		|| item.equals(Items.rabbit)
                    	|| item.equals(Items.fish))*/	//Items.fish refers to UNCOOKED fish: Raw Fish, Raw Salmon, Pufferfish, Clownfish. All have a Potion id of 0
                else if(HasSmeltingRecipe(itemStack))
                {
                    if (!EatRawFood)
                    {
                        continue;
                    }

                    overeat = 997;	//setting the overeat value high will make it unappealing to the food selection algorithm
                }
                else if (potionId == Potion.poison.id
                		|| potionId == Potion.hunger.id
                        || potionId == Potion.confusion.id
                        || FishType.getFishTypeForItemStack(itemStack) == FishType.PUFFERFISH)	//Pufferfish has a Potion id of 0, but still adds posion+hunger+confusion
                {
                    overeat = 998;	//setting the overeat value high will make it unappealing to the food selection algorithm
                }
                
                //this food is better if we overeat less, or the overeat is the same but it heals more hunger
                if (bestFoodMatchOvereat > overeat ||
                        ((overeat == bestFoodMatchOvereat) && (heal > bestFoodMatchHeal)))
                {
                    bestFoodMatchIndex = i;
                    bestFoodMatchOvereat = overeat;
                    bestFoodMatchHeal = heal;
                    continue;
                }
            }
        }

        if (bestFoodMatchIndex > -1)
            return bestFoodMatchIndex;
        else
            return -1;
    }
    
    /**
     * Determines if the itemStack has a smelting recipe in the furnace.
     * @param itemStack
     * @return
     */
    private static boolean HasSmeltingRecipe(ItemStack itemStack)
    {
    	//if this function ends up taking a long time to run we can save the values into our own Map (without meta values) for fast lookup
    	
        Map smeltingList = FurnaceRecipes.instance().getSmeltingList();
        
        //if(smeltingList.containsKey(itemStack)) ... ;	//this doesn't work since the meta values for the item stacks are different
        
        Iterator it = smeltingList.entrySet().iterator();
        while(it.hasNext())
        {
        	Entry entry = (Entry)it.next();
        	ItemStack furnaceRecipeItemStack = (ItemStack)entry.getKey();
        	
        	if(furnaceRecipeItemStack.getItem().equals(itemStack.getItem()))
        	{
        		return true;
        	}
        }
        return false;
    }
    

    /**
     * Toggles the whether you eat golden food or not
     * @return The state it was changed to
     */
    public static boolean ToggleEatingGoldenFood()
    {
    	return EatGoldenFood = !EatGoldenFood;
    }
    
    /**
     * Toggles the whether you eat raw (uncooked) food or not
     * @return The state it was changed to
     */
    public static boolean ToggleEatingRawFood()
    {
    	return EatRawFood = !EatRawFood;
    }

    /**
     * Toggles the prioritizing food in hotbar
     * @return The state it was changed to
     */
    public static boolean TogglePrioritizeFoodInHotbar()
    {
    	return PrioritizeFoodInHotbar = !PrioritizeFoodInHotbar;
    }
    
    /**
     * Toggles enabling using PvP Soup
     * @return The state it was changed to
     */
    public static boolean ToggleUsePvPSoup()
    {
    	return UsePvPSoup = !UsePvPSoup;
    }
    
    
    private class StopEatingTimerTask extends TimerTask
    {
        private Robot r;
        private int hotbarIndexToBeSelected = -1;

        /**
         * Helper class whose purpose is to release right click and set our status to not eating.
         */
        StopEatingTimerTask(Robot r)
        {
            this.r = r;
        }
        /**
         * Helper class whose purpose is to release right click, set our status to not eating, and select a hotbar index.
         */
        StopEatingTimerTask(Robot r, int hotbarIndexToBeSelected)
        {
            this.r = r;
            this.hotbarIndexToBeSelected = hotbarIndexToBeSelected;
        }

        @Override
        public void run()
        {
            r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
            isCurrentlyEating = false;
            
            if(hotbarIndexToBeSelected > -1)
            {
            	mc.thePlayer.inventory.currentItem = hotbarIndexToBeSelected;
            }
        }
    }
}
