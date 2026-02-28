package com.github.lumin.gui.clickgui.panel;

import com.github.lumin.graphics.renderers.RectRenderer;
import com.github.lumin.graphics.renderers.RoundRectRenderer;
import com.github.lumin.graphics.renderers.TextRenderer;
import com.github.lumin.graphics.shaders.BlurShader;
import com.github.lumin.graphics.text.StaticFontLoader;
import com.github.lumin.gui.IComponent;
import com.github.lumin.gui.clickgui.component.ModuleComponent;
import com.github.lumin.gui.clickgui.component.impl.ColorSettingComponent;
import com.github.lumin.managers.Managers;
import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.github.lumin.modules.impl.client.InterFace;
import com.github.lumin.utils.render.MouseUtils;
import com.github.lumin.utils.render.animation.Animation;
import com.github.lumin.utils.render.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContentPanel implements IComponent {

    private final Minecraft mc = Minecraft.getInstance();

    private float x;
    private float y;
    private float width;
    private float height;
    private Category currentCategory;

    private final Animation viewAnimation = new Animation(Easing.EASE_OUT_QUAD, 150L);
    private boolean closeSettingsRequested;
    private int currentState = 0;
    private int targetState = 0;

    private static final float CARD_ASPECT_WIDTH = 16.0f;
    private static final float CARD_ASPECT_HEIGHT = 9.0f;

    private final RoundRectRenderer listRoundRect = new RoundRectRenderer();
    private final TextRenderer listFont = new TextRenderer();
    private final List<ModuleCard> moduleCards = new ArrayList<>();

    private String listSearchText = "";
    private boolean listSearchFocused = false;
    private float listScrollOffset = 0.0f;
    private float listScrollTarget = 0.0f;
    private float listMaxScroll = 0.0f;
    private boolean listDraggingScrollbar = false;
    private float listScrollbarDragStartMouseY = 0.0f;
    private float listScrollbarDragStartScroll = 0.0f;

    private float lastSearchBoxX, lastSearchBoxY, lastSearchBoxW, lastSearchBoxH;
    private float lastListX, lastListY, lastListW, lastListH;
    private float lastScrollbarX, lastScrollbarY, lastScrollbarW, lastScrollbarH;
    private float lastThumbY, lastThumbH;

    private Module requestedSettingsModule = null;

    private final RoundRectRenderer settingsRoundRect = new RoundRectRenderer();
    private final RectRenderer settingsRect = new RectRenderer();
    private final TextRenderer settingsFont = new TextRenderer();

    private final RoundRectRenderer pickingRound = new RoundRectRenderer();
    private final RectRenderer pickingRect = new RectRenderer();
    private final RoundRectRenderer pickerRound = new RoundRectRenderer();
    private final TextRenderer pickingText = new TextRenderer();

    private ModuleComponent settingsComponent = null;
    private String settingsSearchText = "";
    private boolean settingsSearchFocused = false;

    private float settingsScrollOffset = 0.0f;
    private float settingsScrollTarget = 0.0f;
    private float settingsMaxScroll = 0.0f;

    private boolean settingsDraggingScrollbar = false;
    private float settingsScrollbarDragStartMouseY = 0.0f;
    private float settingsScrollbarDragStartScroll = 0.0f;

    private float lastIconBoxX, lastIconBoxY, lastIconBoxW, lastIconBoxH;
    private float lastSettingsSearchBoxX, lastSettingsSearchBoxY, lastSettingsSearchBoxW, lastSettingsSearchBoxH;
    private float lastSettingsX, lastSettingsY, lastSettingsW, lastSettingsH;
    private float lastSettingsScrollbarX, lastSettingsScrollbarY, lastSettingsScrollbarW, lastSettingsScrollbarH;
    private float lastSettingsThumbY, lastSettingsThumbH;

    private boolean settingsExitRequested = false;

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setCurrentCategory(Category category) {
        if (this.currentCategory == category) return;
        this.currentCategory = category;
        this.closeSettingsRequested = false;
        clearSettingsModule();
        this.currentState = 0;
        this.targetState = 0;
        this.viewAnimation.setStartValue(0.0f);
        List<Module> modules = new ArrayList<>();
        for (Module module : Managers.MODULE.getModules()) {
            if (module.category == category) {
                modules.add(module);
            }
        }
        setModules(modules);
    }

    private void setModules(List<Module> modules) {
        moduleCards.clear();
        for (Module module : modules) {
            moduleCards.add(new ModuleCard(module));
        }
        listSearchText = "";
        listSearchFocused = false;
        listScrollOffset = 0.0f;
        listScrollTarget = 0.0f;
        listMaxScroll = 0.0f;
        listDraggingScrollbar = false;
        requestedSettingsModule = null;
    }

    private Module consumeRequestedSettingsModule() {
        Module m = requestedSettingsModule;
        requestedSettingsModule = null;
        return m;
    }

    private void renderSearchBox(RendererSet set, float x, float y, float w, float h, float guiScale, boolean focused, boolean hovered, String text, String placeholder) {
        Color bgColor = focused ? new Color(50, 50, 50, 200) : (hovered ? new Color(40, 40, 40, 200) : new Color(30, 30, 30, 200));
        set.bottomRoundRect().addRoundRect(x, y, w, h, 8f * guiScale, bgColor);
        String display = text.isEmpty() && !focused ? placeholder : text;
        if (focused && (System.currentTimeMillis() % 1000 > 500)) display += "_";
        set.font().addText(display, x + 6 * guiScale, y + h / 2 - 7 * guiScale, guiScale * 0.9f, text.isEmpty() && !focused ? Color.GRAY : Color.WHITE);
    }

    private void renderIconBox(RendererSet set, float x, float y, float w, float h, float guiScale, boolean hovered, String icon) {
        set.bottomRoundRect().addRoundRect(x, y, w, h, 8f * guiScale, hovered ? new Color(40, 40, 40, 200) : new Color(30, 30, 30, 200));
        float iconScale = guiScale * 1.2f;
        float iconW = set.font().getWidth(icon, iconScale, StaticFontLoader.ICONS);
        float iconH = set.font().getHeight(iconScale, StaticFontLoader.ICONS);
        float iconX = x + (w - iconW) / 2f;
        float iconY = y + (h - iconH) / 2f - guiScale;
        set.font().addText(icon, iconX, iconY, iconScale, new Color(200, 200, 200), StaticFontLoader.ICONS);
    }

    private void renderScrollbar(RendererSet set, float x, float y, float w, float h, float thumbY, float thumbH, float guiScale, boolean dragging, boolean hovered, boolean thumbHovered) {
        Color trackColor = hovered ? new Color(255, 255, 255, 28) : new Color(255, 255, 255, 18);
        Color thumbColor = dragging ? new Color(255, 255, 255, 90) : (thumbHovered ? new Color(255, 255, 255, 75) : new Color(255, 255, 255, 55));
        set.bottomRoundRect().addRoundRect(x, y, w, h, w / 2.0f, trackColor);
        set.bottomRoundRect().addRoundRect(x, thumbY, w, thumbH, w / 2.0f, thumbColor);
    }

    private void setupScissor(float areaX, float areaY, float areaW, float areaH, float pxScale, int fbW, int fbH, int guiH) {
        int scX = Mth.floor(areaX * pxScale);
        int scY = Mth.floor((guiH - (areaY + areaH)) * pxScale);
        int scW = Mth.ceil(areaW * pxScale);
        int scH = Mth.ceil(areaH * pxScale);
        scX = Mth.clamp(scX, 0, fbW);
        scY = Mth.clamp(scY, 0, fbH);
        scW = Mth.clamp(scW, 0, fbW - scX);
        scH = Mth.clamp(scH, 0, fbH - scY);
        settingsRoundRect.setScissor(scX, scY, scW, scH);
        settingsRect.setScissor(scX, scY, scW, scH);
        settingsFont.setScissor(scX, scY, scW, scH);
    }

    private void clearScissor() {
        settingsRoundRect.clearScissor();
        settingsRect.clearScissor();
        settingsFont.clearScissor();
    }

    private void renderListView(RendererSet set, int mouseX, int mouseY, float deltaTicks) {
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;
        float padding = 8 * guiScale;
        float spacing = 4 * guiScale;
        float searchHeight = 24 * guiScale;
        float availableWidth = panelWidth - padding * 2 - spacing;
        float iconBoxWidth = availableWidth * 0.1f;
        float searchBoxWidth = availableWidth * 0.9f;

        float iconBoxX = this.x + padding;
        float searchBoxX = iconBoxX + iconBoxWidth + spacing;
        float boxY = this.y + padding;

        boolean iconBoxHovered = MouseUtils.isHovering(iconBoxX, boxY, iconBoxWidth, searchHeight, mouseX, mouseY);
        renderIconBox(set, iconBoxX, boxY, iconBoxWidth, searchHeight, guiScale, iconBoxHovered, "\uF00D");

        boolean searchHovered = MouseUtils.isHovering(searchBoxX, boxY, searchBoxWidth, searchHeight, mouseX, mouseY);
        String placeholder = InterFace.isEnglish() ? "Search..." : "搜索...";
        renderSearchBox(set, searchBoxX, boxY, searchBoxWidth, searchHeight, guiScale, listSearchFocused, searchHovered, listSearchText, placeholder);

        lastSearchBoxX = searchBoxX;
        lastSearchBoxY = boxY;
        lastSearchBoxW = searchBoxWidth;
        lastSearchBoxH = searchHeight;

        float listStartY = boxY + searchHeight + padding;
        float listBottom = this.y + panelHeight - padding;
        float listH = Math.max(0.0f, listBottom - listStartY);

        float scrollbarW = 4.0f * guiScale;
        float scrollbarGap = 4.0f * guiScale;
        float listAreaX = this.x + padding;
        float listAreaW = Math.max(0.0f, panelWidth - padding * 2 - scrollbarGap - scrollbarW);

        lastListX = listAreaX;
        lastListY = listStartY;
        lastListW = listAreaW;
        lastListH = listH;

        float scrollbarX = listAreaX + listAreaW + scrollbarGap;

        lastScrollbarX = scrollbarX;
        lastScrollbarY = listStartY;
        lastScrollbarW = scrollbarW;
        lastScrollbarH = listH;

        List<ModuleCard> visibleCards = new ArrayList<>();
        for (ModuleCard card : moduleCards) {
            if (!listSearchText.isEmpty() && !card.module.getName().toLowerCase().startsWith(listSearchText.toLowerCase())) {
                card.width = 0;
                card.height = 0;
                continue;
            }
            visibleCards.add(card);
        }

        if (visibleCards.isEmpty() || listH <= 0.0f || listAreaW <= 0.0f) {
            listMaxScroll = 0.0f;
            listScrollOffset = 0.0f;
            listScrollTarget = 0.0f;
            listDraggingScrollbar = false;
            lastThumbY = 0.0f;
            lastThumbH = 0.0f;
            return;
        }

        float itemGap = 8 * guiScale;
        float minCardWidth = 120 * guiScale;
        int columns = Math.max(3, Mth.floor((listAreaW + itemGap) / (minCardWidth + itemGap)));

        float cardWidth = (listAreaW - itemGap * (columns - 1)) / columns;
        float cardHeight = cardWidth * (CARD_ASPECT_HEIGHT / CARD_ASPECT_WIDTH);

        int totalRows = Mth.ceil(visibleCards.size() / (double) columns);
        float contentH = totalRows <= 0 ? 0.0f : totalRows * cardHeight + Math.max(0, totalRows - 1) * itemGap;

        listMaxScroll = Math.max(0.0f, contentH - listH);
        listScrollTarget = Mth.clamp(listScrollTarget, 0.0f, listMaxScroll);
        listScrollOffset = listScrollOffset + (listScrollTarget - listScrollOffset) * 0.35f;
        listScrollOffset = Math.max(0.0f, Math.min(listScrollOffset, listMaxScroll));

        float thumbH = listMaxScroll <= 0.0f ? listH : Math.max(12.0f * guiScale, listH * (listH / contentH));
        float thumbTravel = Math.max(0.0f, listH - thumbH);
        float thumbY = listMaxScroll <= 0.0f ? listStartY : listStartY + (listScrollOffset / listMaxScroll) * thumbTravel;

        lastThumbY = thumbY;
        lastThumbH = thumbH;

        if (listDraggingScrollbar && listMaxScroll > 0.0f && thumbTravel > 0.0f) {
            float mouseDelta = mouseY - listScrollbarDragStartMouseY;
            float scrollDelta = (mouseDelta / thumbTravel) * listMaxScroll;
            listScrollTarget = Math.max(0.0f, Math.min(listScrollbarDragStartScroll + scrollDelta, listMaxScroll));
        }

        float pxScale = (float) mc.getWindow().getGuiScale();
        int fbW = mc.getWindow().getWidth();
        int fbH = mc.getWindow().getHeight();
        int guiH = mc.getWindow().getGuiScaledHeight();

        int scX = Mth.floor(listAreaX * pxScale);
        int scY = Mth.floor((guiH - (listStartY + listH)) * pxScale);
        int scW = Mth.ceil(listAreaW * pxScale);
        int scH = Mth.ceil(listH * pxScale);

        scX = Mth.clamp(scX, 0, fbW);
        scY = Mth.clamp(scY, 0, fbH);
        scW = Mth.clamp(scW, 0, fbW - scX);
        scH = Mth.clamp(scH, 0, fbH - scY);

        listRoundRect.setScissor(scX, scY, scW, scH);
        listFont.setScissor(scX, scY, scW, scH);

        int visibleIndex = 0;
        for (ModuleCard card : visibleCards) {
            int row = visibleIndex / columns;
            int col = visibleIndex % columns;

            float cardX = listAreaX + col * (cardWidth + itemGap);
            float cardY = listStartY + row * (cardHeight + itemGap) - listScrollOffset;

            card.x = cardX;
            card.y = cardY;
            card.width = cardWidth;
            card.height = cardHeight;

            if (cardY + cardHeight < listStartY || cardY > listBottom) {
                visibleIndex++;
                continue;
            }

            card.render(listRoundRect, listFont, mouseX, mouseY, guiScale);
            visibleIndex++;
        }

        listRoundRect.drawAndClear();
        listFont.drawAndClear();
        listRoundRect.clearScissor();
        listFont.clearScissor();

        if (listMaxScroll > 0.0f) {
            boolean scrollbarHovered = MouseUtils.isHovering(scrollbarX, listStartY, scrollbarW, listH, mouseX, mouseY);
            boolean thumbHovered = MouseUtils.isHovering(scrollbarX, thumbY, scrollbarW, thumbH, mouseX, mouseY);
            renderScrollbar(set, scrollbarX, listStartY, scrollbarW, listH, thumbY, thumbH, guiScale, listDraggingScrollbar, scrollbarHovered, thumbHovered);
        }
    }

    private boolean listViewMouseClicked(MouseButtonEvent event, boolean focused) {
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;

        if (!MouseUtils.isHovering(x, y, panelWidth, panelHeight, event.x(), event.y())) {
            return false;
        }

        if (MouseUtils.isHovering(lastSearchBoxX, lastSearchBoxY, lastSearchBoxW, lastSearchBoxH, event.x(), event.y())) {
            if (event.button() == 1) {
                listSearchText = "";
                listScrollTarget = 0.0f;
            }
            listSearchFocused = true;
            return true;
        }

        listSearchFocused = false;

        if (event.button() == 0 && listMaxScroll > 0.0f && MouseUtils.isHovering(lastScrollbarX, lastScrollbarY, lastScrollbarW, lastScrollbarH, event.x(), event.y())) {
            float thumbTravel = Math.max(0.0f, lastScrollbarH - lastThumbH);
            if (thumbTravel > 0.0f) {
                if (MouseUtils.isHovering(lastScrollbarX, lastThumbY, lastScrollbarW, lastThumbH, event.x(), event.y())) {
                    listDraggingScrollbar = true;
                    listScrollbarDragStartMouseY = (float) event.y();
                    listScrollbarDragStartScroll = listScrollTarget;
                    return true;
                }
                float clickY = (float) event.y();
                float ratio = (clickY - lastScrollbarY - lastThumbH / 2.0f) / thumbTravel;
                ratio = Math.max(0.0f, Math.min(1.0f, ratio));
                listScrollTarget = ratio * listMaxScroll;
                listDraggingScrollbar = true;
                listScrollbarDragStartMouseY = (float) event.y();
                listScrollbarDragStartScroll = listScrollTarget;
                return true;
            }
        }

        if (event.button() == 0 && !moduleCards.isEmpty()) {
            for (ModuleCard card : moduleCards) {
                if (card.width <= 0 || card.height <= 0) continue;
                if (MouseUtils.isHovering(card.x, card.y, card.width, card.height, event.x(), event.y())) {
                    card.module.toggle();
                    return true;
                }
            }
        }

        if (event.button() == 1 && !moduleCards.isEmpty()) {
            for (ModuleCard card : moduleCards) {
                if (card.width <= 0 || card.height <= 0) continue;
                if (MouseUtils.isHovering(card.x, card.y, card.width, card.height, event.x(), event.y())) {
                    requestedSettingsModule = card.module;
                    listDraggingScrollbar = false;
                    return true;
                }
            }
        }

        return true;
    }

    private boolean listViewMouseReleased(MouseButtonEvent event) {
        listDraggingScrollbar = false;
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;
        return MouseUtils.isHovering(x, y, panelWidth, panelHeight, event.x(), event.y());
    }

    private boolean listViewMouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (listMaxScroll <= 0.0f) return false;
        if (!MouseUtils.isHovering(lastListX, lastListY, lastListW + lastScrollbarW, lastListH, mouseX, mouseY))
            return false;
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float step = 24.0f * guiScale;
        listScrollTarget = Math.max(0.0f, Math.min(listScrollTarget - (float) scrollY * step, listMaxScroll));
        return true;
    }

    private boolean listViewKeyPressed(KeyEvent event) {
        if (listSearchFocused) {
            if (event.key() == GLFW.GLFW_KEY_BACKSPACE) {
                if (!listSearchText.isEmpty()) {
                    listSearchText = listSearchText.substring(0, listSearchText.length() - 1);
                    listScrollTarget = 0.0f;
                }
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_ESCAPE || event.key() == GLFW.GLFW_KEY_ENTER) {
                listSearchFocused = false;
                return true;
            }
        }
        return false;
    }

    private boolean listViewCharTyped(CharacterEvent event) {
        if (!listSearchFocused) return false;
        String str = Character.toString(event.codepoint());
        listSearchText += str;
        listScrollTarget = 0.0f;
        return true;
    }

    private void listViewClickOutside() {
        listSearchFocused = false;
        listDraggingScrollbar = false;
    }

    private boolean isSettingsActive() {
        return settingsComponent != null;
    }

    private void setSettingsModule(Module module) {
        ColorSettingComponent.closeActivePicker();
        settingsComponent = new ModuleComponent(module);
        settingsSearchText = "";
        settingsSearchFocused = false;
        settingsScrollOffset = 0.0f;
        settingsScrollTarget = 0.0f;
        settingsMaxScroll = 0.0f;
        settingsDraggingScrollbar = false;
        settingsExitRequested = false;
    }

    private void clearSettingsModule() {
        ColorSettingComponent.closeActivePicker();
        settingsComponent = null;
        settingsSearchText = "";
        settingsSearchFocused = false;
        settingsScrollOffset = 0.0f;
        settingsScrollTarget = 0.0f;
        settingsMaxScroll = 0.0f;
        settingsDraggingScrollbar = false;
        settingsExitRequested = false;
    }

    private boolean consumeSettingsExitRequest() {
        boolean v = settingsExitRequested;
        settingsExitRequested = false;
        return v;
    }

    private void renderSettingsView(RendererSet set, int mouseX, int mouseY, float deltaTicks) {
        if (settingsComponent == null) return;

        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;
        float padding = 8 * guiScale;
        float spacing = 4 * guiScale;
        float searchHeight = 24 * guiScale;
        float availableWidth = panelWidth - padding * 2 - spacing;
        float iconBoxWidth = availableWidth * 0.1f;
        float titleBoxWidth = availableWidth * 0.9f;

        float iconBoxX = this.x + padding;
        float titleBoxX = iconBoxX + iconBoxWidth + spacing;
        float boxY = this.y + padding;

        boolean iconBoxHovered = MouseUtils.isHovering(iconBoxX, boxY, iconBoxWidth, searchHeight, mouseX, mouseY);
        renderIconBox(set, iconBoxX, boxY, iconBoxWidth, searchHeight, guiScale, iconBoxHovered, "\uF00D");

        lastIconBoxX = iconBoxX;
        lastIconBoxY = boxY;
        lastIconBoxW = iconBoxWidth;
        lastIconBoxH = searchHeight;

        boolean titleHovered = MouseUtils.isHovering(titleBoxX, boxY, titleBoxWidth, searchHeight, mouseX, mouseY);
        renderSearchBox(set, titleBoxX, boxY, titleBoxWidth, searchHeight, guiScale, settingsSearchFocused, titleHovered, settingsSearchText, "Search...");

        lastSettingsSearchBoxX = titleBoxX;
        lastSettingsSearchBoxY = boxY;
        lastSettingsSearchBoxW = titleBoxWidth;
        lastSettingsSearchBoxH = searchHeight;

        float areaX = this.x + padding;
        float areaY = boxY + searchHeight + padding;
        float areaW = Math.max(0.0f, panelWidth - padding * 2);
        float areaH = Math.max(0.0f, (this.y + panelHeight - padding) - areaY);

        float scrollbarW = 4.0f * guiScale;
        float scrollbarGap = 4.0f * guiScale;
        float contentW = Math.max(0.0f, areaW - scrollbarGap - scrollbarW);

        float titleScale = 1.15f * guiScale;
        float rowH = 18.0f * guiScale;
        float rowGap = 4.0f * guiScale;
        float innerPadding = 8.0f * guiScale;

        settingsComponent.setFilterText(settingsSearchText);
        int itemCount = settingsComponent.getFilteredVisibleCount();

        float titleH = set.font().getHeight(titleScale);
        float contentH = innerPadding + titleH + 6.0f * guiScale;
        if (itemCount > 0) {
            contentH += itemCount * rowH + Math.max(0, itemCount - 1) * rowGap;
        }
        contentH += innerPadding;

        settingsMaxScroll = Math.max(0.0f, contentH - areaH);
        settingsScrollTarget = Math.max(0.0f, Math.min(settingsScrollTarget, settingsMaxScroll));
        settingsScrollOffset = settingsScrollOffset + (settingsScrollTarget - settingsScrollOffset) * 0.35f;
        settingsScrollOffset = Math.max(0.0f, Math.min(settingsScrollOffset, settingsMaxScroll));

        float scrollbarX = areaX + contentW + scrollbarGap;

        float thumbH = settingsMaxScroll <= 0.0f ? areaH : Math.max(12.0f * guiScale, areaH * (areaH / contentH));
        float thumbTravel = Math.max(0.0f, areaH - thumbH);
        float thumbY = settingsMaxScroll <= 0.0f ? areaY : areaY + (settingsScrollOffset / settingsMaxScroll) * thumbTravel;

        lastSettingsX = areaX;
        lastSettingsY = areaY;
        lastSettingsW = contentW;
        lastSettingsH = areaH;

        lastSettingsScrollbarX = scrollbarX;
        lastSettingsScrollbarY = areaY;
        lastSettingsScrollbarW = scrollbarW;
        lastSettingsScrollbarH = areaH;

        lastSettingsThumbY = thumbY;
        lastSettingsThumbH = thumbH;

        if (settingsDraggingScrollbar && settingsMaxScroll > 0.0f && thumbTravel > 0.0f) {
            float mouseDelta = mouseY - settingsScrollbarDragStartMouseY;
            float scrollDelta = (mouseDelta / thumbTravel) * settingsMaxScroll;
            settingsScrollTarget = Math.max(0.0f, Math.min(settingsScrollbarDragStartScroll + scrollDelta, settingsMaxScroll));
        }

        float pxScale = (float) mc.getWindow().getGuiScale();
        int fbW = mc.getWindow().getWidth();
        int fbH = mc.getWindow().getHeight();
        int guiH = mc.getWindow().getGuiScaledHeight();

        setupScissor(areaX, areaY, contentW, areaH, pxScale, fbW, fbH, guiH);

        RendererSet settingsSet = new RendererSet(settingsRoundRect, set.topRoundRect(), set.texture(), settingsFont, pickingRound, pickingRect, pickerRound, pickingText);

        settingsComponent.setX(areaX);
        settingsComponent.setY(areaY - settingsScrollOffset);
        settingsComponent.setWidth(contentW);
        settingsComponent.setHeight(contentH);
        settingsComponent.render(settingsSet, mouseX, mouseY, deltaTicks);

        settingsRoundRect.drawAndClear();
        settingsRect.drawAndClear();
        settingsFont.drawAndClear();
        clearScissor();

        settingsComponent.renderOverlayBlurs(mouseX, mouseY, deltaTicks);
        settingsComponent.renderOverlays(settingsSet, mouseX, mouseY, deltaTicks);

        pickingRound.drawAndClear();
        pickingRect.drawAndClear();
        pickerRound.drawAndClear();
        pickingText.drawAndClear();

        if (settingsMaxScroll > 0.0f) {
            boolean scrollbarHovered = MouseUtils.isHovering(scrollbarX, areaY, scrollbarW, areaH, mouseX, mouseY);
            boolean thumbHovered = MouseUtils.isHovering(scrollbarX, thumbY, scrollbarW, thumbH, mouseX, mouseY);
            renderScrollbar(set, scrollbarX, areaY, scrollbarW, areaH, thumbY, thumbH, guiScale, settingsDraggingScrollbar, scrollbarHovered, thumbHovered);
        }
    }

    private boolean settingsViewMouseClicked(MouseButtonEvent event, boolean focused) {
        if (settingsComponent == null) return false;

        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;

        if (ColorSettingComponent.hasActivePicker() && !ColorSettingComponent.isMouseOutOfPicker((int) event.x(), (int) event.y())) {
            return settingsComponent.mouseClicked(event, focused);
        }

        if (!MouseUtils.isHovering(x, y, panelWidth, panelHeight, event.x(), event.y())) {
            return false;
        }

        if (event.button() == 0 && MouseUtils.isHovering(lastIconBoxX, lastIconBoxY, lastIconBoxW, lastIconBoxH, event.x(), event.y())) {
            settingsExitRequested = true;
            return true;
        }

        if (MouseUtils.isHovering(lastSettingsSearchBoxX, lastSettingsSearchBoxY, lastSettingsSearchBoxW, lastSettingsSearchBoxH, event.x(), event.y())) {
            if (event.button() == 1) {
                settingsSearchText = "";
                settingsScrollTarget = 0.0f;
            }
            settingsSearchFocused = true;
            return true;
        }

        settingsSearchFocused = false;

        if (event.button() == 0 && settingsMaxScroll > 0.0f && MouseUtils.isHovering(lastSettingsScrollbarX, lastSettingsScrollbarY, lastSettingsScrollbarW, lastSettingsScrollbarH, event.x(), event.y())) {
            float thumbTravel = Math.max(0.0f, lastSettingsScrollbarH - lastSettingsThumbH);
            if (thumbTravel > 0.0f) {
                if (MouseUtils.isHovering(lastSettingsScrollbarX, lastSettingsThumbY, lastSettingsScrollbarW, lastSettingsThumbH, event.x(), event.y())) {
                    settingsDraggingScrollbar = true;
                    settingsScrollbarDragStartMouseY = (float) event.y();
                    settingsScrollbarDragStartScroll = settingsScrollTarget;
                    return true;
                }
                float clickY = (float) event.y();
                float ratio = (clickY - lastSettingsScrollbarY - lastSettingsThumbH / 2.0f) / thumbTravel;
                ratio = Mth.clamp(ratio, 0.0f, 1.0f);
                settingsScrollTarget = ratio * settingsMaxScroll;
                settingsDraggingScrollbar = true;
                settingsScrollbarDragStartMouseY = (float) event.y();
                settingsScrollbarDragStartScroll = settingsScrollTarget;
                return true;
            }
        }

        return settingsComponent.mouseClicked(event, focused);
    }

    private boolean settingsViewMouseReleased(MouseButtonEvent event) {
        settingsDraggingScrollbar = false;
        if (settingsComponent == null) return false;
        if (ColorSettingComponent.hasActivePicker()) {
            return settingsComponent.mouseReleased(event);
        }
        if (settingsComponent.hasDraggingSetting()) {
            return settingsComponent.mouseReleased(event);
        }
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;
        return MouseUtils.isHovering(x, y, panelWidth, panelHeight, event.x(), event.y()) && settingsComponent.mouseReleased(event);
    }

    private boolean settingsViewMouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (settingsComponent == null) return false;
        if (settingsMaxScroll <= 0.0f) return false;
        if (!MouseUtils.isHovering(lastSettingsX, lastSettingsY, lastSettingsW + lastSettingsScrollbarW, lastSettingsH, mouseX, mouseY))
            return false;
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float step = 24.0f * guiScale;
        settingsScrollTarget = Math.max(0.0f, Math.min(settingsScrollTarget - (float) scrollY * step, settingsMaxScroll));
        return true;
    }

    private boolean settingsViewKeyPressed(KeyEvent event) {
        if (settingsComponent == null) return false;
        if (settingsSearchFocused) {
            if (event.key() == GLFW.GLFW_KEY_BACKSPACE) {
                if (!settingsSearchText.isEmpty()) {
                    settingsSearchText = settingsSearchText.substring(0, settingsSearchText.length() - 1);
                    settingsScrollTarget = 0.0f;
                }
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_ESCAPE || event.key() == GLFW.GLFW_KEY_ENTER) {
                settingsSearchFocused = false;
                return true;
            }
        }
        return settingsComponent.keyPressed(event);
    }

    private boolean settingsViewCharTyped(CharacterEvent event) {
        if (settingsComponent == null) return false;
        if (settingsSearchFocused) {
            String str = Character.toString(event.codepoint());
            settingsSearchText += str;
            settingsScrollTarget = 0.0f;
            return true;
        }
        return settingsComponent.charTyped(event);
    }

    private void settingsViewClickOutside() {
        settingsDraggingScrollbar = false;
        settingsSearchFocused = false;
    }

    private static final class ModuleCard {
        float x, y, width, height;
        final Module module;
        private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_QUAD, 120L);
        private final Animation enabledAnimation = new Animation(Easing.EASE_OUT_QUAD, 160L);

        private ModuleCard(Module module) {
            this.module = module;
            enabledAnimation.setStartValue(module.isEnabled() ? 1.0f : 0.0f);
        }

        private void render(RoundRectRenderer round, TextRenderer text, int mouseX, int mouseY, float guiScale) {
            if (width <= 0 || height <= 0) return;

            boolean hovered = MouseUtils.isHovering(x, y, width, height, mouseX, mouseY);

            hoverAnimation.run(hovered ? 1.0f : 0.0f);
            enabledAnimation.run(module.isEnabled() ? 1.0f : 0.0f);
            float ht = clamp01(hoverAnimation.getValue());
            float et = clamp01(enabledAnimation.getValue());

            Color offColor = new Color(40, 40, 40, 130);
            Color onColor = new Color(148, 148, 148, 130);
            Color base = lerpColor(offColor, onColor, et);
            int alphaBump = (int) (24.0f * ht);
            Color bgColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), clamp255(base.getAlpha() + alphaBump));

            float scale = 1.0f + 0.02f * ht;
            float rw = width * scale;
            float rh = height * scale;
            float rx = x - (rw - width) / 2.0f;
            float ry = y - (rh - height) / 2.0f;

            round.addRoundRect(rx, ry, rw, rh, 10f * guiScale, bgColor);

            String moduleName = module.getName();
            String moduleDescription = module.getDescription();

            float nameScale = 1.1f * guiScale;
            float maxNameWidth = rw - 14 * guiScale;
            float nameWidth = text.getWidth(moduleName, nameScale);
            if (nameWidth > maxNameWidth && nameWidth > 0) {
                nameScale *= maxNameWidth / nameWidth;
                nameWidth = maxNameWidth;
            }

            float descriptionScale = 0.62f * guiScale;
            float maxDescriptionWidth = rw - 16 * guiScale;
            float descriptionWidth = text.getWidth(moduleDescription, descriptionScale);
            if (descriptionWidth > maxDescriptionWidth && descriptionWidth > 0) {
                descriptionScale *= maxDescriptionWidth / descriptionWidth;
                descriptionWidth = maxDescriptionWidth;
            }

            float nameHeight = text.getHeight(nameScale);
            float descriptionHeight = text.getHeight(descriptionScale);
            float textGap = 3 * guiScale;

            float blockHeight = nameHeight + textGap + descriptionHeight;
            float startY = ry + (rh - blockHeight) / 2f;

            float nameX = rx + (rw - nameWidth) / 2f;
            float nameY = startY - 0.6f * guiScale;

            float descriptionX = rx + (rw - descriptionWidth) / 2f;
            float descriptionY = startY + nameHeight + textGap - 0.2f * guiScale;

            text.addText(moduleName, nameX, nameY, nameScale, Color.WHITE);
            text.addText(moduleDescription, descriptionX, descriptionY, descriptionScale, new Color(200, 200, 200));
        }

        private static float clamp01(float v) {
            return Mth.clamp(v, 0.0f, 1.0f);
        }

        private static int clamp255(int v) {
            return Mth.clamp(v, 0, 255);
        }

        private static Color lerpColor(Color a, Color b, float t) {
            t = clamp01(t);
            int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
            int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
            int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
            int al = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
            return new Color(clamp255(r), clamp255(g), clamp255(bl), clamp255(al));
        }
    }

    @Override
    public void render(RendererSet set, int mouseX, int mouseY, float deltaTicks) {
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float radius = guiScale * 8f;
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;
        BlurShader.drawRoundedBlur(x, y, panelWidth, panelHeight, 0, radius, radius, 0, new Color(30, 30, 30, 245), InterFace.INSTANCE.blurStrength.getValue().floatValue(), 1.0f);

        if (isSettingsActive() && !this.closeSettingsRequested) {
            this.targetState = 1;
        } else {
            this.targetState = 0;
        }

        if (this.currentState != this.targetState) {
            if (this.targetState == 1) {
                this.currentState = 2;
                this.viewAnimation.setStartValue(0.0f);
            } else {
                this.currentState = 3;
                this.viewAnimation.setStartValue(1.0f);
            }
        }

        if (this.currentState == 2) {
            this.viewAnimation.run(1.0f);
            float t = this.viewAnimation.getValue();
            if (t >= 0.99f) {
                this.currentState = 1;
            }
            renderSettingsView(set, mouseX, mouseY, deltaTicks);
        } else if (this.currentState == 3) {
            this.viewAnimation.run(0.0f);
            float t = 1.0f - this.viewAnimation.getValue();
            if (t <= 0.01f) {
                this.currentState = 0;
                clearSettingsModule();
            }
            renderListView(set, mouseX, mouseY, deltaTicks);
        } else if (this.currentState == 1) {
            renderSettingsView(set, mouseX, mouseY, deltaTicks);
        } else {
            renderListView(set, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean focused) {
        float guiScale = InterFace.INSTANCE.scale.getValue().floatValue();
        float panelWidth = this.width * guiScale;
        float panelHeight = this.height * guiScale;

        if (this.currentState == 2 || this.currentState == 3) {
            return true;
        }

        if (ColorSettingComponent.hasActivePicker() && ColorSettingComponent.isMouseOutOfPicker((int) event.x(), (int) event.y())) {
            ColorSettingComponent.closeActivePicker();
            return true;
        }

        if (ColorSettingComponent.hasActivePicker() && this.currentState == 1) {
            boolean handled = settingsViewMouseClicked(event, focused);
            if (consumeSettingsExitRequest()) {
                this.closeSettingsRequested = true;
                return true;
            }
            return handled;
        }

        if (!MouseUtils.isHovering(x, y, panelWidth, panelHeight, event.x(), event.y())) {
            listViewClickOutside();
            settingsViewClickOutside();
            return false;
        }

        if (this.currentState == 1) {
            boolean handled = settingsViewMouseClicked(event, focused);
            if (consumeSettingsExitRequest()) {
                this.closeSettingsRequested = true;
                return true;
            }
            return handled;
        }

        if (this.currentState == 0) {
            boolean handled = listViewMouseClicked(event, focused);
            Module open = consumeRequestedSettingsModule();
            if (open != null) {
                this.closeSettingsRequested = false;
                setSettingsModule(open);
                this.currentState = 2;
                this.viewAnimation.setStartValue(0.0f);
                return true;
            }
            return handled;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.currentState == 2 || this.currentState == 3) {
            return true;
        }

        if (ColorSettingComponent.hasActivePicker() && this.currentState == 1) {
            return settingsViewMouseReleased(event);
        }

        if (this.currentState == 1) {
            return settingsViewMouseReleased(event);
        }

        if (this.currentState == 0) {
            return listViewMouseReleased(event);
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.currentState == 2 || this.currentState == 3) {
            return true;
        }

        if (this.currentState == 1) {
            return settingsViewMouseScrolled(mouseX, mouseY, scrollY);
        }

        if (this.currentState == 0) {
            return listViewMouseScrolled(mouseX, mouseY, scrollY);
        }

        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.currentState == 2 || this.currentState == 3) {
            return true;
        }

        if (this.currentState == 1) {
            return settingsViewKeyPressed(event);
        }

        if (this.currentState == 0) {
            return listViewKeyPressed(event);
        }

        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.currentState == 2 || this.currentState == 3) {
            return true;
        }

        if (this.currentState == 1) {
            return settingsViewCharTyped(event);
        }

        if (this.currentState == 0) {
            return listViewCharTyped(event);
        }

        return false;
    }
}