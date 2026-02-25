package com.github.lumin.gui.clickgui.component.impl;

import com.github.lumin.gui.Component;
import com.github.lumin.settings.impl.BoolSetting;
import com.github.lumin.utils.render.MouseUtils;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class BoolSettingComponent extends Component {
    private final BoolSetting setting;

    public BoolSettingComponent(BoolSetting setting) {
        this.setting = setting;
    }

    public BoolSetting getSetting() {
        return setting;
    }

    @Override
    public void render(RendererSet set, int mouseX, int mouseY, float partialTicks) {
        if (!setting.isAvailable()) return;

        boolean hovered = MouseUtils.isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY);
        Color bg = hovered ? new Color(255, 255, 255, 18) : new Color(255, 255, 255, 10);
        set.bottomRoundRect().addRoundRect(getX(), getY(), getWidth(), getHeight(), 6.0f * scale, bg);

        String name = setting.getDisplayName();
        String value = setting.getValue() ? "ON" : "OFF";

        float textScale = 0.85f * scale;
        float textY = getY() + (getHeight() - set.font().getHeight(textScale)) / 2.0f - 0.5f * scale;
        set.font().addText(name, getX() + 6.0f * scale, textY, textScale, Color.WHITE);

        float valueW = set.font().getWidth(value, textScale);
        set.font().addText(value, getX() + getWidth() - 6.0f * scale - valueW, textY, textScale, setting.getValue() ? new Color(120, 255, 160) : new Color(200, 200, 200));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean focused) {
        if (event.button() == 0 && isHovered((float) event.x(), (float) event.y())) {
            setting.setValue(!setting.getValue());
            return true;
        }
        return super.mouseClicked(event, focused);
    }
}
