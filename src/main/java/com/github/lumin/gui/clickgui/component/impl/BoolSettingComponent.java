package com.github.lumin.gui.clickgui.component.impl;

import com.github.lumin.gui.Component;
import com.github.lumin.modules.impl.client.InterFace;
import com.github.lumin.settings.impl.BoolSetting;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class BoolSettingComponent extends Component {
    private final BoolSetting setting;

    public BoolSettingComponent(BoolSetting setting) {
        this.setting = setting;
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        setHeight(12.0f * scale);
    }

    @Override
    public boolean isVisible() {
        return setting.isAvailable();
    }

    @Override
    public void render(RendererSet set, int mouseX, int mouseY, float deltaTicks) {
        String name = InterFace.isEnglish() ? setting.getEnglishName() : setting.getChineseName();
        float fontScale = 0.8f * scale;
        float textHeight = set.textRenderer().getHeight(fontScale);
        float textY = getY() + (getHeight() - textHeight) / 2f;
        set.textRenderer().addText(name, getX(), textY, Color.WHITE, fontScale);

        float boxSize = 7.0f * scale;
        float boxX = getX() + getWidth() - boxSize;
        float boxY = getY() + (getHeight() - boxSize) / 2.0f;

        Color onColor = InterFace.getMainColor();
        Color offColor = new Color(255, 255, 255, 40);
        set.roundRectRenderer().addRoundRect(boxX, boxY, boxSize, boxSize, 2.0f * scale, setting.getValue() ? onColor : offColor);

//        set.roundRectRenderer().drawAndClear();
//        set.rectRenderer().drawAndClear();
//        set.textRenderer().drawAndClear();
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
