package com.zyin.zyinhud.helper;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.Vec3;

import com.zyin.zyinhud.mods.PlayerLocator;

/**
 * The EntityTrackerHUDHelper calculates the (x,y) position on the HUD for
 * entities in the game world.
 */
public class HUDEntityTrackerHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static FloatBuffer modelMatrix = BufferUtils.createFloatBuffer(16);
    private static FloatBuffer projMatrix = BufferUtils.createFloatBuffer(16);

    private static final double pi = Math.PI;
    private static final double twoPi = 2 * Math.PI;

    /**
     * Stores world render transform matrices for later use when rendering HUD.
     */
    public static void StoreMatrices() {
        modelMatrix.rewind();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
        projMatrix.rewind();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projMatrix);
    }

    /**
     * Send information about the positions of entities to mods that need this
     * information.
     * <p>
     * Place new rendering methods for mods in this function.
     * 
     * @param entity
     * @param x
     *            location on the HUD
     * @param y
     *            location on the HUD
     */
    private static void RenderEntityInfoOnHUD(Entity entity, int x, int y) {
        PlayerLocator.RenderEntityInfoOnHUD(entity, x, y);
    }

    /**
     * Calculates the on-screen (x,y) positions of entities and renders various
     * overlays over them.
     * 
     * @param partialTickTime
     */
    public static void RenderEntityInfo(float partialTickTime)
    {
        PlayerLocator.numOverlaysRendered = 0;
        
        if (PlayerLocator.Enabled && PlayerLocator.Mode == PlayerLocator.Modes.ON
                && mc.inGameHasFocus)
        {
            EntityClientPlayerMP me = mc.thePlayer;
            
            double meX = me.lastTickPosX + (me.posX - me.lastTickPosX) * partialTickTime;
            double meY = me.lastTickPosY + (me.posY - me.lastTickPosY) * partialTickTime;
            double meZ = me.lastTickPosZ + (me.posZ - me.lastTickPosZ) * partialTickTime;
            
            double pitch = ((me.rotationPitch + 90) * Math.PI) / 180;
            double yaw  = ((me.rotationYaw + 90)  * Math.PI) / 180;

            // direction the player is facing
            Vec3 lookDir = Vec3.createVectorHelper(Math.sin(pitch) * Math.cos(yaw), Math.cos(pitch), Math.sin(pitch) * Math.sin(yaw));
            
            if (mc.gameSettings.thirdPersonView == 2)
            {
                // reversed 3rd-person view; flip the look direction
                lookDir.xCoord *= -1;
                lookDir.yCoord *= -1;
                lookDir.zCoord *= -1;
            }
            
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            
            IntBuffer viewport = BufferUtils.createIntBuffer(16);
            GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
            
            //iterate over all the loaded Entity objects and find just the entities we are tracking
            for (Object object : mc.theWorld.loadedEntityList)
            {
                //only track entities that we are tracking (i.e. other players/wolves/witherskeletons)
                if(!(object instanceof EntityOtherPlayerMP || 
                	 object instanceof EntityWolf ||
                	 (object instanceof EntitySkeleton) && ((EntitySkeleton)object).getSkeletonType() == 1))
                    continue;
                
                Entity entity = (Entity)object;
                
                double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTickTime;
                double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTickTime;
                double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTickTime;
                                
                // direction to target entity
                Vec3 toEntity = Vec3.createVectorHelper(entityX - meX, entityY - meY, entityZ - meZ);
                
                float x = (float)toEntity.xCoord;
                float y = (float)toEntity.yCoord;
                float z = (float)toEntity.zCoord;
                
                double dist = toEntity.lengthVector();
                toEntity = toEntity.normalize();
                
                if (lookDir.dotProduct(toEntity) <= 0.02)
                {
                    // angle between vectors is greater than about 89 degrees, so
                    // create a dummy target location that is 89 degrees away from look direction
                    // along the arc between look direction and direction to target entity  
                    
                    final double angle = 89.0 * pi / 180;
                    final double sin = Math.sin(angle);
                    final double cos = Math.cos(angle);
                    
                    Vec3 ortho = lookDir.crossProduct(toEntity); // vector orthogonal to look direction and direction to target entity
                    double ox = ortho.xCoord;
                    double oy = ortho.yCoord;
                    double oz = ortho.zCoord;
                    
                    // build a rotation matrix to rotate around a vector (ortho) by an angle (89 degrees)
                    // from http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
                    double m00 = cos + ox*ox*(1-cos);
                    double m01 = ox*oy*(1-cos) - oz*sin;
                    double m02 = ox*oz*(1-cos) + oy*sin;
                    double m10 = oy*ox*(1-cos) + oz*sin;
                    double m11 = cos + oy*oy*(1-cos);
                    double m12 = oy*oz*(1-cos) - ox*sin;
                    double m20 = oz*ox*(1-cos) - oy*sin;
                    double m21 = oz*oy*(1-cos) + ox*sin;
                    double m22 = cos + oz*oz*(1-cos);                    
                    
                    // transform (multiply) look direction vector with rotation matrix and scale by distance to target entity;
                    // this produces the coordinates for the dummy target
                    x = (float)(dist * (m00*lookDir.xCoord + m01*lookDir.yCoord + m02*lookDir.zCoord));
                    y = (float)(dist * (m10*lookDir.xCoord + m11*lookDir.yCoord + m12*lookDir.zCoord));
                    z = (float)(dist * (m20*lookDir.xCoord + m21*lookDir.yCoord + m22*lookDir.zCoord));
                }
                
                FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
                
                modelMatrix.rewind();
                projMatrix.rewind();
                
                // map target's object coordinates into window coordinates
                // using world render transform matrices stored by StoreMatrices()
                GLU.gluProject(x, y, z, modelMatrix, projMatrix, viewport, screenCoords);
                
                int hudX = Math.round(screenCoords.get(0)) / res.getScaleFactor();
                int hudY = height - Math.round(screenCoords.get(1)) / res.getScaleFactor();
                 
                // if <hudX, hudY> is outside the screen, scale the coordinates so they're
                // at the edge of the screen (to preserve angle)
                
                int newHudX = hudX, newHudY = hudY;
                
                //use X overshoot to scale Y
                if (hudX < 0)
                    newHudY = (int)((hudY - height / 2) / (1 - (2 * (float)hudX / width)) + height / 2);
                else if (hudX > width)
                    newHudY = (int)((hudY - height / 2) / ((2 * (float)hudX / width) - 1) + height / 2);
                
                //use Y overshoot to scale X
                if (hudY < 0)
                    newHudX = (int)((hudX - width / 2) / (1 - (2 * (float)hudY / height)) + width / 2);
                else if (hudY > height)
                    newHudX = (int)((hudX - width / 2) / ((2 * (float)hudY / height) - 1) + width / 2);
                
                hudX = newHudX;
                hudY = newHudY;
                
                RenderEntityInfoOnHUD(entity, hudX, hudY);
            }
        }
    }
}
