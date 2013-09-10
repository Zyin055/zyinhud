package zyin.zyinhud.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import zyin.zyinhud.mods.AnimalInfo;

/**
 * The EntityTrackerRenderHelper finds entities in the game world.
 */
public class RenderEntityTrackerHelper
{
    private static Minecraft mc = Minecraft.getMinecraft();
    private static EntityClientPlayerMP me;


    public static int maxOverlayMessagesRendered = 200;	//Renders only the first nearest X entities

    /**
     * The list of entities that the EntitryTrackerHelper will track.
     * The class must exactly match the entity's class, don't use a parent class like EntityPlayer.
     * <p>
     * Entities must be added to this list in order for new mods to track it.
     */
    private static Class[] trackedEntities =
    {
        //EntityClientPlayerMP.class,	//this is the player
        EntityVillager.class,
        EntityHorse.class,
        EntityCow.class,
        EntitySheep.class,
        EntityChicken.class,
        EntityPig.class
    };
    
    /**
     * Send information about the positions of entities to mods that need this information.
     * <p>
     * Place new rendering methods in this function, in addition to adding the mod's mode
     * check at the start of RenderEntityInfo().
     * @param entity
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
        if (AnimalInfo.Mode == 1
                && mc.inGameHasFocus)
        {
            me = mc.thePlayer;
            int i = 0;

            //iterate over all the loaded Entity objects and find just the players
            for (Object object : mc.theWorld.loadedEntityList)
            {
                if (i > maxOverlayMessagesRendered)
                {
                    break;
                }

                //only track entities that we are tracking (i.e. players, horses)
                boolean trackingThisEntity = false;

                for (Class trackedEntity : trackedEntities)
                {
                    if (trackedEntity.getName().equals(object.getClass().getName()))
                    {
                        trackingThisEntity = true;
                        break;
                    }
                }

                if (!trackingThisEntity)
                {
                    continue;
                }

                RenderEntityInfoInWorld((Entity)object, partialTickTime);
                
                i++;
            }
        }
    }
}
