package com.zyin.zyinhud.mods;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import com.zyin.zyinhud.util.InventoryUtil;
import com.zyin.zyinhud.util.Localization;
import com.zyin.zyinhud.util.ZyinHUDUtil;

/**
 * Potion Aid allows the player to drink potions in their inventory by calling its Drink() method.
 */
public class PotionAid extends ZyinHUDModBase
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
    
    private Timer timer = new Timer();
    private TimerTask swapTimerTask;
    private TimerTask drinkTimerTask;

    private Robot r = null;
    private boolean isCurrentlyDrinking;
    private boolean previousDrinkFromHotbar;

    private int potionItemIndex;
    private int currentItemInventoryIndex;
    private int currentItemHotbarIndex;
    
    private static int potionDrinkDuration = 2000;
    
    /**
     * Use this instance for all method calls.
     */
    public static PotionAid instance = new PotionAid();

    private PotionAid()
    {
        try
        {
            r = new Robot();
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }

        isCurrentlyDrinking = false;
        previousDrinkFromHotbar = false;
    }

    /**
     * Makes the player drink a potion item on their hotbar or in their inventory.
     */
    public void Drink()
    {
        //make sure we're not about to click on a right-clickable thing
        if(ZyinHUDUtil.IsMouseoveredBlockRightClickable())
        	return;
        
        if (isCurrentlyDrinking)
        {
            //if we're drinking and we try to drink again, then cancel whatever we're drinking
            //by releasing right click, and swapping the potion back to its correct position
            StopDrinking();
            return;
        }
        else
        {
            potionItemIndex = GetMostAppropriatePotionItemIndexFromInventory();
        	if (potionItemIndex < 0)
            {
                ZyinHUDUtil.DisplayNotification(Localization.get("potionaid.noappropriatepotions"));
                return;
            }
            
            if(potionItemIndex  > 35 && potionItemIndex < 45)	//on the hotbar
            {
            	StartDrinkingFromHotbar(potionItemIndex);
            }
            else //if(potionItemIndex  > 8 && potionItemIndex < 36)	//in the inventory
            {
            	StartDrinkingFromInventory(potionItemIndex);
            }
        }
    }
    
    /**
     * Changes the selected index in your hotbar to where the potion is, then drinks it.
     * @param potionHotbarIndex 36-44
     */
    private void StartDrinkingFromHotbar(int potionHotbarIndex)
    {
    	if(potionHotbarIndex < 36 | potionHotbarIndex > 44)
    		return;
    	
    	currentItemHotbarIndex = mc.thePlayer.inventory.currentItem;
    	potionHotbarIndex = InventoryUtil.TranslateInventoryIndexToHotbarIndex(potionHotbarIndex);
    	
    	int previouslySelectedHotbarSlotIndex = mc.thePlayer.inventory.currentItem;
    	mc.thePlayer.inventory.currentItem = potionHotbarIndex;

        r.mousePress(InputEvent.BUTTON3_MASK); //perform a right click
        isCurrentlyDrinking = true;
        previousDrinkFromHotbar = true;
        
        //after this timer runs out we'll release right click to stop eating and select the previously selected item
        drinkTimerTask = new StopDrinkingTimerTask(r, previouslySelectedHotbarSlotIndex);
        timer.schedule(drinkTimerTask, potionDrinkDuration + InventoryUtil.GetSuggestedItemSwapDelay());
    }
    
    /**
     * Swaps a potion from your inventory into your hotbar, then drinks it.
     * @param potionInventoryIndex 9-35
     */
    private void StartDrinkingFromInventory(int potionInventoryIndex)
    {
    	if(potionInventoryIndex < 9 | potionInventoryIndex > 35)
    		return;
    	
        currentItemInventoryIndex = InventoryUtil.GetCurrentlySelectedItemInventoryIndex();
        InventoryUtil.Swap(currentItemInventoryIndex, potionInventoryIndex);
        
        r.mousePress(InputEvent.BUTTON3_MASK); //perform a right click
        isCurrentlyDrinking = true;
        previousDrinkFromHotbar = false;
        
        //after this timer runs out we'll release right click to stop eating
        drinkTimerTask = new StopDrinkingTimerTask(r);
        timer.schedule(drinkTimerTask, potionDrinkDuration);
        swapTimerTask = InventoryUtil.instance.SwapWithDelay(currentItemInventoryIndex, potionInventoryIndex,
        		potionDrinkDuration + InventoryUtil.GetSuggestedItemSwapDelay());
    }
    
    
    
    

    /**
     * Stops eating by releasing right click and moving the food back to its original position.
     */
    public void StopDrinking()
    {
    	if(previousDrinkFromHotbar)
    		StopDrinkingFromHotbar();
    	else
    		StopDrinkingFromInventory();
    }

    private void StopDrinkingFromInventory()
    {
        r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
        drinkTimerTask.cancel();
    	swapTimerTask.cancel();
        InventoryUtil.Swap(currentItemInventoryIndex, potionItemIndex);
        isCurrentlyDrinking = false;
    }
    private void StopDrinkingFromHotbar()
    {
        r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
        drinkTimerTask.cancel();
        mc.thePlayer.inventory.currentItem = currentItemHotbarIndex;
        isCurrentlyDrinking = false;
    }

    /**
     * Are we currently drinking a potion?
     * @return
     */
    public boolean isDrinking()
    {
        return isCurrentlyDrinking;
    }

    /**
     * Determines the most appropriate potion to use given the players current situation.
     * It uses the following drinkable potions (not splash potions):
     * <p>
     * Potion of Fire Resistance<br>
     * Potion of Health<br>
     * Potion of Regeneration<br>
     * Potion of Swiftness<br>
     * Potion of Strength<br>
     * Potion of Invisibility<br>
     * @return the index in your inventory that has the most appropriate potion to drink (9-34), or -1 if no appropriate potions found.
     */
    public int GetMostAppropriatePotionItemIndexFromInventory()
    {
        List inventorySlots = mc.thePlayer.inventoryContainer.inventorySlots;
        
        //indexes of potions in the player's inventory
        int fireResistancePotionIndex = -1;
        int healPotionIndex = -1;
        int regenerationPotionIndex = -1;
        int moveSpeedPotionIndex = -1;
        int damageBoostPotionIndex = -1;
        int invisibilityPotionIndex = -1;
        
        
        //iterate over the main inventory (9-35), then the hotbar (36-44) to find what potions we can use
        for (int i = 9; i < inventorySlots.size(); i++)
        {
            Slot slot = (Slot)inventorySlots.get(i);
            ItemStack itemStack = slot.getStack();

            if (itemStack == null)
            {
                continue;
            }

            Item item = itemStack.getItem();

            if (item instanceof ItemPotion)
            {
            	ItemPotion potion = (ItemPotion)item;
            	
            	boolean isSplashPotion = potion.isSplash(itemStack.getItemDamage());
            	
            	//we dont' want to use splash potions
            	if(isSplashPotion)
            		continue;
            	
            	List potionEffects = potion.getEffects(itemStack.getItemDamage());
            	PotionEffect potionEffect = (PotionEffect)potionEffects.get(0);
                String potionEffectName = potionEffect.getEffectName();

            	if(potionEffectName.equals("potion.fireResistance"))
            		fireResistancePotionIndex = i;
            	else if(potionEffectName.equals("potion.heal"))
            		healPotionIndex = i;
            	else if(potionEffectName.equals("potion.regeneration"))
            		regenerationPotionIndex = i;
            	else if(potionEffectName.equals("potion.moveSpeed"))
            		moveSpeedPotionIndex = i;
            	else if(potionEffectName.equals("potion.damageBoost"))
            		damageBoostPotionIndex = i;
            	else if(potionEffectName.equals("potion.invisibility"))
            		invisibilityPotionIndex = i;
            	
            }
        }
        
        
    	//determine what potion effects are on the player
    	boolean hasMoveSpeedPotionEffect = false;
    	boolean hasDamageBoostPotionEffect = false;
    	boolean hasInvisibilityPotionEffect = false;
    	boolean hasRegenerationPotionEffect = false;
    	boolean hasFireResistancePotionEffect = false;
    	
    	Collection potionEffects = mc.thePlayer.getActivePotionEffects();	//key:potionId, value:potionEffect
        Iterator it = potionEffects.iterator();
        while (it.hasNext())
        {
            PotionEffect potionEffect = (PotionEffect)it.next();
            String potionEffectName = potionEffect.getEffectName();

        	if(potionEffectName.equals("potion.regeneration"))
        		hasRegenerationPotionEffect = true;
        	else if(potionEffectName.equals("potion.fireResistance"))
        		hasFireResistancePotionEffect = true;
        	else if(potionEffectName.equals("potion.moveSpeed"))
        		hasMoveSpeedPotionEffect = true;
        	else if(potionEffectName.equals("potion.damageBoost"))
        		hasDamageBoostPotionEffect = true;
        	else if(potionEffectName.equals("potion.invisibility"))
        		hasInvisibilityPotionEffect = true;
        }
        
        boolean isOnFire = mc.thePlayer.isBurning();
        boolean isInjured = mc.thePlayer.shouldHeal();
    	
        
    	//we use potions in this order:
        //1) fire resist potion
        //2) health potion
        //3) regeneration potion
        //4) swiftness potion
        //5) strength potion
        //6) invisibility potion
    	if(fireResistancePotionIndex > -1 && isOnFire && !hasFireResistancePotionEffect)
    		return fireResistancePotionIndex;
    	if(healPotionIndex > -1 && isInjured)
    		return healPotionIndex;
    	if(regenerationPotionIndex > -1 && !hasRegenerationPotionEffect && isInjured)
    		return regenerationPotionIndex;
    	if(moveSpeedPotionIndex > -1 && !hasMoveSpeedPotionEffect)
    		return moveSpeedPotionIndex;
    	if(damageBoostPotionIndex > -1 && !hasDamageBoostPotionEffect)
    		return damageBoostPotionIndex;
    	if(invisibilityPotionIndex > -1 && !hasInvisibilityPotionEffect)
    		return invisibilityPotionIndex;
        
        //no appropriate potion found
        return -1;
    }

    
    private class StopDrinkingTimerTask extends TimerTask
    {
        private Robot r;
        private int hotbarIndexToBeSelected = -1;

        /**
         * Helper class whose purpose is to release right click and set our status to not drinking.
         */
        StopDrinkingTimerTask(Robot r)
        {
            this.r = r;
        }
        /**
         * Helper class whose purpose is to release right click, set our status to not drinking, and select a hotbar index.
         */
        StopDrinkingTimerTask(Robot r, int hotbarIndexToBeSelected)
        {
            this.r = r;
            this.hotbarIndexToBeSelected = hotbarIndexToBeSelected;
        }

        @Override
        public void run()
        {
            r.mouseRelease(InputEvent.BUTTON3_MASK); //release right click
            isCurrentlyDrinking = false;
            
            if(hotbarIndexToBeSelected > -1)
            {
            	mc.thePlayer.inventory.currentItem = hotbarIndexToBeSelected;
            }
        }
    }
}
