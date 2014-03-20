package com.zyin.zyinhud.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;

import com.zyin.zyinhud.mods.AnimalInfo;

/**
 * The RenderEntityTrackerHelper finds entities in the game world.
 */
public class RenderEntityTrackerHelper
{
    private static Minecraft mc = Minecraft.getMinecraft();
    
    /**
     * Send information about the positions of entities to mods that need this information.
     * <p>
     * Place new rendering methods for mods in this function.
     * @param entity
     * @param partialTickTime
     */
    private static void RenderEntityInfoInWorld(Entity entity, float partialTickTime)
    {
    	AnimalInfo.RenderEntityInfoInWorld(entity, partialTickTime);
    }

    /**
     * Calculates the positions of entities in the world and renders various overlays on them.
     */
    public static void RenderEntityInfo(float partialTickTime)
    {
        if (AnimalInfo.Mode == AnimalInfo.Modes.ON
                && mc.inGameHasFocus)
        {
            //iterate over all the loaded Entity objects and find just the players
            for (Object object : mc.theWorld.loadedEntityList)
            {
                if(!(object instanceof EntityAnimal ||
                		object instanceof EntityVillager))
                {
                	continue;
                }

                RenderEntityInfoInWorld((Entity)object, partialTickTime);
            }
        }
    }
}
