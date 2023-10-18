package de.greenman999;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationTools {

    public static boolean isRotated = false;
    private static float destPitch = 0;
    private static float destYaw = 0;
    private static float startPitch = 0;
    private static float startYaw = 0;
    private static float speed = 0;
    private static float elapsedTime = 0;

    public static void smoothLookAt(Vec3d target, float speed) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null) return;
        Vec3d vec3d = EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(mc.player);
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);
        destPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        destYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F);
        startPitch = mc.player.getPitch();
        startYaw = mc.player.getYaw();
        RotationTools.speed = speed;
        elapsedTime = 0;
        if(destPitch == startPitch && destYaw == startYaw) return;
        isRotated = false;

        /*Vec3d vec3d = EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(mc.player);
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);
        float endPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float endYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F);
        float startPitch = mc.player.getPitch();
        float startYaw = mc.player.getYaw();
        System.out.println("startPitch: " + startPitch + ", startYaw: " + startYaw);
        System.out.println("endPitch: " + endPitch + ", endYaw: " + endYaw);
        float tickDelta = mc.getTickDelta();

        for(float delta = 0; delta < (long) 50; delta++) {
            float pitch = MathHelper.lerp(tickDelta, mc.player.prevPitch, endPitch);
            float yaw = MathHelper.lerp(tickDelta, mc.player.prevYaw, endYaw);
            System.out.println("pitch: " + pitch + ", yaw: " + yaw);
            //mc.player.updatePositionAndAngles(mc.player.getX(), mc.player.getY(), mc.player.getZ(), pitch, yaw);
            mc.player.setPitch(pitch);
            mc.player.setYaw(yaw);
        }*/
    }

    public static void render(WorldRenderContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null || isRotated || speed == 0) return;
        float tickDelta = context.tickDelta();
        elapsedTime += tickDelta;
        float percentageComplete = elapsedTime / speed;
        //System.out.println("percentageComplete: " + percentageComplete);
        if(percentageComplete >= 1) {
            isRotated = true;
            speed = 0;
            System.out.println("Finished Rotating");
            //mc.player.setPitch(destPitch);
            //mc.player.setYaw(destYaw);
            return;
        }

        float pitch = MathHelper.lerp(percentageComplete, startPitch, destPitch);
        float yaw = MathHelper.lerp(percentageComplete, startYaw, destYaw);

        mc.player.setPitch(pitch);
        mc.player.setYaw(yaw);
    }

}
