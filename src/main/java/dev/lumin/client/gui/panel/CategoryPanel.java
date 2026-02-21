package dev.lumin.client.gui.panel;

import dev.lumin.client.graphics.skija.util.SkijaHelper;
import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.animation.AnimationUtil;
import dev.lumin.client.gui.animation.Easing;
import dev.lumin.client.gui.component.Component;
import dev.lumin.client.gui.element.ModuleElement;
import dev.lumin.client.gui.theme.Theme;
import dev.lumin.client.modules.Category;
import dev.lumin.client.modules.Module;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ClipMode;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.RRect;

import java.util.ArrayList;
import java.util.List;

public class CategoryPanel extends Component {

    private final Category category;
    private final List<ModuleElement> moduleElements = new ArrayList<>();

    private float scrollOffset = 0f;
    private float targetScrollOffset = 0f;
    private float maxScrollOffset = 0f;
    private boolean scrolling = false;
    private Animation scrollAnimation;

    private ModuleElement expandedElement = null;

    private static final float HEADER_HEIGHT = 40f;
    private static final float ELEMENT_PADDING = 4f;
    private static final float SIDE_PADDING = 8f;

    public CategoryPanel(Category category) {
        this.category = category;
        this.width = 300f;
        this.height = 400f;
    }

    public void setModules(List<Module> modules) {
        moduleElements.clear();
        for (Module module : modules) {
            if (module.category == category && !module.isHidden()) {
                ModuleElement element = new ModuleElement(module);
                element.setWidth(width - SIDE_PADDING * 2);
                moduleElements.add(element);
            }
        }
        calculateMaxScroll();
    }

    private void calculateMaxScroll() {
        float totalHeight = calculateTotalContentHeight();
        float visibleHeight = height - HEADER_HEIGHT;
        maxScrollOffset = Math.max(0, totalHeight - visibleHeight);
        targetScrollOffset = Math.min(targetScrollOffset, maxScrollOffset);
        scrollOffset = Math.min(scrollOffset, maxScrollOffset);
    }

    private float calculateTotalContentHeight() {
        float total = 0;
        for (ModuleElement element : moduleElements) {
            total += element.getHeight() + ELEMENT_PADDING;
        }
        return total;
    }

    @Override
    public void render(Canvas canvas, float mouseX, float mouseY) {
        if (!visible) return;

        renderBackground(canvas);
        renderHeader(canvas);
        renderModuleList(canvas, mouseX, mouseY);
        renderScrollBar(canvas);
    }

    private void renderBackground(Canvas canvas) {
        SkijaHelper.drawRRect(x, y, width, height, Theme.Radius.LARGE, Theme.PANEL_BG);
    }

    private void renderHeader(Canvas canvas) {
        Font titleFont = new Font(null, 16);

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.TEXT_TITLE);

            String categoryName = getCategoryDisplayName();
            canvas.drawString(categoryName, x + SIDE_PADDING, y + 24, titleFont, paint);
        }

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.withAlpha(Theme.BORDER, 0.3f));

            SkijaHelper.drawLine(x + SIDE_PADDING, y + HEADER_HEIGHT - 1, x + width - SIDE_PADDING, y + HEADER_HEIGHT - 1, paint);
        }
    }

    private void renderModuleList(Canvas canvas, float mouseX, float mouseY) {
        canvas.save();

        RRect clipRect = RRect.makeXYWH(
                x, y + HEADER_HEIGHT,
                width, height - HEADER_HEIGHT,
                Theme.Radius.LARGE
        );
        canvas.clipRRect(clipRect, ClipMode.INTERSECT);

        float elementY = y + HEADER_HEIGHT + ELEMENT_PADDING - scrollOffset;
        float contentHeight = height - HEADER_HEIGHT;

        for (ModuleElement element : moduleElements) {
            if (elementY + element.getHeight() >= y + HEADER_HEIGHT && elementY <= y + height) {
                element.setX(x + SIDE_PADDING);
                element.setY(elementY);
                element.setWidth(width - SIDE_PADDING * 2);

                float adjustedMouseY = mouseY;
                boolean isHovered = mouseX >= element.getX() && mouseX <= element.getX() + element.getWidth() &&
                        adjustedMouseY >= elementY && adjustedMouseY <= elementY + element.getHeight();
                element.updateHovered(mouseX, adjustedMouseY);

                element.render(canvas, mouseX, mouseY);
            }
            elementY += element.getHeight() + ELEMENT_PADDING;
        }

        canvas.restore();
    }

    private void renderScrollBar(Canvas canvas) {
        if (maxScrollOffset <= 0) return;

        float scrollBarWidth = 4f;
        float scrollBarHeight = Math.max(30f, (height - HEADER_HEIGHT) * (height - HEADER_HEIGHT) / (calculateTotalContentHeight() + height - HEADER_HEIGHT));
        float scrollBarX = x + width - scrollBarWidth - 4f;
        float scrollBarY = y + HEADER_HEIGHT + (scrollOffset / maxScrollOffset) * (height - HEADER_HEIGHT - scrollBarHeight);

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.withAlpha(Theme.BORDER, 0.5f));

            canvas.drawRRect(
                    RRect.makeXYWH(scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight, scrollBarWidth / 2f),
                    paint
            );
        }
    }

    @Override
    public void update() {
        super.update();

        if (scrollAnimation != null) {
            scrollAnimation.update();
            scrollOffset = scrollAnimation.getValue();
            if (scrollAnimation.isFinished()) {
                scrollAnimation = null;
            }
        }

        for (ModuleElement element : moduleElements) {
            element.update();
        }

        calculateMaxScroll();
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (mouseY < y + HEADER_HEIGHT) {
            return;
        }

        float elementY = y + HEADER_HEIGHT + ELEMENT_PADDING - scrollOffset;

        for (ModuleElement element : moduleElements) {
            float elementHeight = element.getHeight();
            if (mouseY >= elementY && mouseY <= elementY + elementHeight) {
                if (mouseX >= x + SIDE_PADDING && mouseX <= x + width - SIDE_PADDING) {
                    boolean wasExpanded = element.isExpanded();

                    element.mouseClicked(mouseX, mouseY, button);

                    if (element.isExpanded() && !wasExpanded) {
                        if (expandedElement != null && expandedElement != element) {
                            expandedElement.collapse();
                        }
                        expandedElement = element;
                    } else if (!element.isExpanded() && wasExpanded) {
                        expandedElement = null;
                    }
                    return;
                }
            }
            elementY += elementHeight + ELEMENT_PADDING;
        }
    }

    @Override
    protected void onRelease(double mouseX, double mouseY, int button) {
        for (ModuleElement element : moduleElements) {
            element.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (ModuleElement element : moduleElements) {
            element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    protected void onScroll(double mouseX, double mouseY, double delta) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            float scrollAmount = (float) delta * 20f;
            targetScrollOffset = Math.max(0, Math.min(maxScrollOffset, targetScrollOffset + scrollAmount));

            scrollAnimation = new Animation(
                    scrollOffset,
                    targetScrollOffset,
                    AnimationUtil.DURATION_FAST,
                    Easing.EASE_OUT
            );
            scrollAnimation.start();
        }
    }

    private String getCategoryDisplayName() {
        return switch (category) {
            case COMBAT -> "Combat";
            case MOVEMENT -> "Movement";
            case VISUAL -> "Visual";
            case MISC -> "Misc";
            case CLIENT -> "Client";
        };
    }

    public Category getCategory() {
        return category;
    }

    public List<ModuleElement> getModuleElements() {
        return moduleElements;
    }

    public float getScrollOffset() {
        return scrollOffset;
    }

    public void scrollToElement(ModuleElement element) {
        int index = moduleElements.indexOf(element);
        if (index < 0) return;

        float elementY = HEADER_HEIGHT + ELEMENT_PADDING;
        for (int i = 0; i < index; i++) {
            elementY += moduleElements.get(i).getHeight() + ELEMENT_PADDING;
        }

        float visibleHeight = height - HEADER_HEIGHT;
        if (elementY - scrollOffset < 0) {
            targetScrollOffset = elementY - ELEMENT_PADDING;
        } else if (elementY + element.getHeight() - scrollOffset > visibleHeight) {
            targetScrollOffset = elementY + element.getHeight() - visibleHeight + ELEMENT_PADDING;
        }

        targetScrollOffset = Math.max(0, Math.min(maxScrollOffset, targetScrollOffset));
        scrollAnimation = new Animation(scrollOffset, targetScrollOffset, AnimationUtil.DURATION_NORMAL, Easing.EASE_OUT);
        scrollAnimation.start();
    }

    public void collapseAll() {
        for (ModuleElement element : moduleElements) {
            element.collapse();
        }
        expandedElement = null;
    }
}
