package me.catrix.mod.modules.impl.hud;

import me.catrix.Catrix;
import me.catrix.api.utils.DynamicIslandUtil.DynamicIslandAnimationUtil;
import me.catrix.api.utils.DynamicIslandUtil.NotificationUtil;
import me.catrix.api.utils.math.Easing;
import me.catrix.api.utils.render.RenderShadersUtil;
import me.catrix.api.utils.render.TextUtil;
import me.catrix.mod.gui.font.FontRenderers;
import me.catrix.mod.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DynamicIsland extends Module {
    public static DynamicIsland INSTANCE;

    private final List<NotificationUtil> notifications = new ArrayList<>();
    private final DynamicIslandAnimationUtil widthAnimation = new DynamicIslandAnimationUtil();
    private final DynamicIslandAnimationUtil heightAnimation = new DynamicIslandAnimationUtil();

    public DynamicIsland() {
        super("DynamicIsland", Category.Hud);
        setChinese("灵动岛");
        INSTANCE = this;
    }

    @Override
    public void onRender2D(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;
        notifications.removeIf(notification -> System.currentTimeMillis() - notification.startTime > 2000);
        if (!notifications.isEmpty()) {
            renderNotifications(context);
        } else {
            renderNormal(context);
        }
    }

    private void renderNormal(DrawContext context) {
        int ping = mc.getNetworkHandler() != null ? mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency() : 0;
        String text = String.format("%s | %s | %s | %s | %dFPS", Catrix.NAME, mc.getSession().getUsername(), getServerIP(), getPingText(ping), mc.getCurrentFps());

        double animatedWidth = widthAnimation.get((int) (TextUtil.getWidth(text) + (FontRenderers.icon.getWidth("5") + FontRenderers.icon.getWidth("4") + FontRenderers.icon2.getWidth("d") + FontRenderers.icon.getWidth("O") + FontRenderers.icon2.getWidth("a") + (6 * 2)) + 20f));
        double animatedHeight = heightAnimation.get(20);

        RenderShadersUtil.drawRoundedBlur(context.getMatrices(),
                (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 - 2, 15,
                (float) animatedWidth, (float) animatedHeight, 10f, new Color(0x35000000,true),15.0f,0.55f);

        RenderShadersUtil.drawRect(context.getMatrices(),
                (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 - 2, 15,
                (float) animatedWidth, (float) animatedHeight, 10f, new Color(0x4F000000, true));

        if (Math.min(animatedWidth / ((int) (TextUtil.getWidth(text) + (FontRenderers.icon.getWidth("5") + FontRenderers.icon.getWidth("4") + FontRenderers.icon.getWidth("L") + FontRenderers.icon.getWidth("O") + FontRenderers.icon2.getWidth("a") + (6 * 2)) + 20f)), 1.0) > 0.93) {
            String[] parts = text.split(" \\| ");

            FontRenderers.icon2.drawString(context.getMatrices(), "a",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (-4.0f),
                    16.5f + (int) ((animatedHeight - FontRenderers.icon.getFontHeight()) / 2f) + 2, getRainbowColor());

            TextUtil.drawString(context, parts[0],
                    (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2),
                    15 + (int) ((animatedHeight - TextUtil.getHeight()) / 2f) + 1, getRainbowColor());

            FontRenderers.icon.drawString(context.getMatrices(), "5",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ")),
                    15 + (int) ((animatedHeight - FontRenderers.icon.getFontHeight()) / 2f) + 2, Color.WHITE.getRGB());

            TextUtil.drawString(context, parts[1],
                    (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("5") + 2),
                    15 + (int) ((animatedHeight - TextUtil.getHeight()) / 2f) + 1, Color.WHITE.getRGB());

            FontRenderers.icon.drawString(context.getMatrices(), "4",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("5") + 2 +
                            TextUtil.getWidth(parts[1]) + TextUtil.getWidth(" | ")),
                    15 + (int) ((animatedHeight - FontRenderers.icon.getFontHeight()) / 2f) + 2, Color.WHITE.getRGB());

            TextUtil.drawString(context, parts[2],
                    (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("5") + 2 +
                            TextUtil.getWidth(parts[1]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("4") + 2),
                    15 + (int) ((animatedHeight - TextUtil.getHeight()) / 2f) + 1, Color.WHITE.getRGB());

            FontRenderers.icon2.drawString(context.getMatrices(), "d",
                    (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 8 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("5") + 2 +
                            TextUtil.getWidth(parts[1]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("4") + 2 +
                            TextUtil.getWidth(parts[2]) + TextUtil.getWidth(" | ")),
                    15 + (int) ((animatedHeight - FontRenderers.icon.getFontHeight()) / 2f) + 2 + 1.5, Color.WHITE.getRGB());

            TextUtil.drawString(context, parts[3],
                    (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("5") + 2 +
                            TextUtil.getWidth(parts[1]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("4") + 2 +
                            TextUtil.getWidth(parts[2]) + TextUtil.getWidth(" | ") + FontRenderers.icon2.getWidth("d") + 2),
                    15 + (int) ((animatedHeight - TextUtil.getHeight()) / 2f) + 1, getPingColor(ping).getRGB());

            FontRenderers.icon.drawString(context.getMatrices(), "O",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("5") + 2 +
                            TextUtil.getWidth(parts[1]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("4") + 2 +
                            TextUtil.getWidth(parts[2]) + TextUtil.getWidth(" | ") + FontRenderers.icon2.getWidth("d") + 2 +
                            TextUtil.getWidth(parts[3]) + TextUtil.getWidth(" | ")),
                    15 + (int) ((animatedHeight - FontRenderers.icon.getFontHeight()) / 2f) + 2, Color.WHITE.getRGB());

            TextUtil.drawString(context, parts[4],
                    (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + (int) (FontRenderers.icon2.getWidth("a") + 2 + TextUtil.getWidth(parts[0]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("5") + 2 +
                            TextUtil.getWidth(parts[1]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("4") + 2 +
                            TextUtil.getWidth(parts[2]) + TextUtil.getWidth(" | ") + FontRenderers.icon2.getWidth("d") + 2 +
                            TextUtil.getWidth(parts[3]) + TextUtil.getWidth(" | ") + FontRenderers.icon.getWidth("O") + 2),
                    15 + (int) ((animatedHeight - TextUtil.getHeight()) / 2f) + 1, Color.WHITE.getRGB());
        }
    }

    private void renderNotifications(DrawContext context) {
        double animatedWidth = widthAnimation.get(notifications.stream()
                .mapToInt(notification -> 25 + Math.max((int) TextUtil.getWidth("Module Toggled"), (int) TextUtil.getWidth(notification.message + " has been " + (notification.isEnable ? "Enabled" : "Disabled") + "!")))
                .max().orElse(0) + 40, 125, Easing.IOS);
        double animatedHeight = heightAnimation.get(35 + (notifications.size() > 1 ? (35 + 2) * (notifications.size() - 1) : 0), 125, Easing.IOS);

        RenderShadersUtil.drawRoundedBlur(context.getMatrices(),
                (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 - 2, 15,
                (float) animatedWidth, (float) animatedHeight, 10f, new Color(0x35000000,true),15.0f,0.55f);

        RenderShadersUtil.drawRect(context.getMatrices(),
                (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 - 2, 15,
                (float) animatedWidth, (float) animatedHeight, 10f, new Color(0x4F000000, true));

        for (int i = 0; i < notifications.size(); i++) {
            NotificationUtil notification = notifications.get(i);
            float alpha = DynamicIslandAnimationUtil.getNotificationAlphaProgress(notification.startTime);
            if (alpha <= 0) continue;

            Color switchColor = DynamicIslandAnimationUtil.getSwitchColor(notification.startTime, notification.isEnable);
            RenderShadersUtil.drawRect(context.getMatrices(),
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10,
                    15 + (i * (35 + 2)) + 17.5f - 6.5f,
                    25, 15, 15 / 2f,
                    new Color(switchColor.getRed(), switchColor.getGreen(), switchColor.getBlue(), (int)(switchColor.getAlpha() * alpha)));

            RenderShadersUtil.drawRect(context.getMatrices(),
                    DynamicIslandAnimationUtil.getSwitchPosition(
                            notification.startTime,
                            notification.isEnable,
                            (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + 2,
                            (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + 25 - (15 - 4) - 2
                    ),
                    15 + (i * (35 + 2)) + 17.5f - 6.5f + 2,
                    15 - 4, 15 - 4, (15 - 4) / 2f,
                    new Color(255, 255, 255, (int)(255 * alpha)));

            TextUtil.drawString(context, "Module Toggled",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + 30 + 5,
                    15 + (i * (35 + 2)) + 8,
                    new Color(255, 255, 255, (int)(255 * alpha)).getRGB());

            TextUtil.drawString(context, notification.message,
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + 30 + 5,
                    15 + (i * (35 + 2)) + 20,
                    new Color(255, 255, 255, (int)(255 * alpha)).getRGB());

            TextUtil.drawString(context, " has been ",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + 30 + 5 + TextUtil.getWidth(notification.message),
                    15 + (i * (35 + 2)) + 20,
                    new Color(255, 255, 255, (int)(255 * alpha)).getRGB());

            Color textColor = notification.isEnable ? new Color(0xFF00FF00) : new Color(0xFFFF0000);
            TextUtil.drawString(context, notification.isEnable ? "Enabled" : "Disabled",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + 30 + 5 + TextUtil.getWidth(notification.message + " has been "),
                    15 + (i * (35 + 2)) + 20,
                    new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), (int)(textColor.getAlpha() * alpha)).getRGB());

            TextUtil.drawString(context, "!",
                    (float) (mc.getWindow().getScaledWidth() - animatedWidth) / 2 + 10 + 30 + 5 + TextUtil.getWidth(notification.message + " has been ") + TextUtil.getWidth(notification.isEnable ? "Enabled" : "Disabled"),
                    15 + (i * (35 + 2)) + 20,
                    new Color(255, 255, 255, (int)(255 * alpha)).getRGB());
        }
    }

    public void ModuleEnableNotification(String moduleName) {
        notifications.add(new NotificationUtil(moduleName, true));
    }

    public void ModuleDisableNotification(String moduleName) {
        notifications.add(new NotificationUtil(moduleName, false));
    }

    private String getServerIP() {
        if (mc.world == null || mc.getNetworkHandler() == null) {
            return "SinglePlayer";
        }
        ServerInfo serverInfo = mc.getCurrentServerEntry();
        if (serverInfo != null) {
            return serverInfo.address;
        }
        if (mc.isInSingleplayer()) {
            return "SinglePlayer";
        }
        return "Unknown";
    }

    private int getRainbowColor() {
        double rainbowState = Math.ceil((System.currentTimeMillis() * 4 + 20 * 350) / 20.0);
        Color rainbowColor = Color.getHSBColor((float) (rainbowState % 360.0 / 360), 130.0f / 255.0f, 1.0f);
        return rainbowColor.getRGB();
    }

    private String getPingText(int ping) {
        Formatting color = Formatting.GREEN;
        if (ping >= 100) {
            color = Formatting.YELLOW;
        }
        if (ping >= 250) {
            color = Formatting.RED;
        }
        return color.toString() + ping + "ms";
    }

    private Color getPingColor(int ping) {
        if (ping >= 250) {
            return Color.RED;
        } else if (ping >= 100) {
            return Color.YELLOW;
        } else {
            return Color.GREEN;
        }
    }
}