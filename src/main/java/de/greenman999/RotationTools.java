package de.greenman999;

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
    private static long prevTimeMillis = 0;

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
        if(destPitch == startPitch && destYaw == startYaw) return;
        RotationTools.speed = speed / 10f;
        elapsedTime = 0;
        prevTimeMillis = System.currentTimeMillis();
        isRotated = false;
    }

    public static void render() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null || isRotated || speed == 0) return;
        float delta = (System.currentTimeMillis() - (prevTimeMillis == 0 ? System.currentTimeMillis() : prevTimeMillis )) / 1000f;
        prevTimeMillis = System.currentTimeMillis();
        elapsedTime += delta;
        float percentageComplete = elapsedTime / speed;
        if(percentageComplete >= 1) {
            isRotated = true;
            speed = 0;
            return;
        }

        float pitch = MathHelper.lerp(percentageComplete, startPitch, destPitch);
        float yaw = MathHelper.lerp(percentageComplete, startYaw, destYaw);

        mc.player.setPitch(pitch);
        mc.player.setYaw(yaw);
    }

}
