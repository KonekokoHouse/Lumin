package dev.lumin.client.gui.panel;

import dev.lumin.client.graphics.skija.util.SkijaHelper;
import dev.lumin.client.gui.animation.Animation;
import dev.lumin.client.gui.component.Component;
import dev.lumin.client.gui.theme.Theme;
import dev.lumin.client.modules.Category;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;

import java.util.function.Consumer;

public class NavigationBar extends Component {

    private Category selectedCategory = Category.COMBAT;
    private final Consumer<Category> onCategoryChanged;

    private int hoveredIndex = -1;
    private Animation selectionAnimation;
    private float animatedY = 0f;
    private float animatedHeight = 0f;

    private static final float ITEM_HEIGHT = 36f;
    private static final float PADDING = 8f;
    private static final float ICON_SIZE = 16f;
    private static final float ICON_MARGIN = 12f;

    private static final Category[] CATEGORIES = Category.values();
    private static final String[] CATEGORY_NAMES = {"Combat", "Movement", "Visual", "Misc", "Client"};
    private static final String[] CATEGORY_ICONS = {"C", "M", "V", "M", "C"};

    public NavigationBar(Consumer<Category> onCategoryChanged) {
        this.onCategoryChanged = onCategoryChanged;
        this.width = 100f;
        this.height = CATEGORIES.length * ITEM_HEIGHT + PADDING * 2;
    }

    @Override
    public void render(Canvas canvas, float mouseX, float mouseY) {
        if (!visible) return;

        renderBackground(canvas);
        renderSelectionIndicator(canvas);
        renderItems(canvas, mouseX, mouseY);
    }

    private void renderBackground(Canvas canvas) {
        SkijaHelper.drawRRect(x, y, width, height, Theme.Radius.LARGE, Theme.PANEL_BG);
    }

    private void renderSelectionIndicator(Canvas canvas) {
        if (selectedCategory == null) return;

        int selectedIndex = getSelectedIndex();
        if (selectedIndex < 0) return;

        float indicatorY = y + PADDING + selectedIndex * ITEM_HEIGHT + (ITEM_HEIGHT - 28f) / 2f;

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.withAlpha(Theme.PRIMARY, 0.15f));

            SkijaHelper.drawRRect(
                    x + PADDING,
                    indicatorY,
                    width - PADDING * 2,
                    28f,
                    Theme.Radius.SMALL,
                    Theme.withAlpha(Theme.PRIMARY, 0.15f)
            );
        }

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(Theme.PRIMARY);

            SkijaHelper.drawRRect(
                    x + PADDING,
                    indicatorY,
                    3f,
                    28f,
                    1.5f,
                    Theme.PRIMARY
            );
        }
    }

    private void renderItems(Canvas canvas, float mouseX, float mouseY) {
        Font font = new Font(null, 13);

        for (int i = 0; i < CATEGORIES.length; i++) {
            float itemY = y + PADDING + i * ITEM_HEIGHT;
            boolean isSelected = CATEGORIES[i] == selectedCategory;
            boolean isHovered = i == hoveredIndex && !isSelected;

            renderIcon(canvas, i, itemY, isSelected, isHovered);
            renderLabel(canvas, i, itemY, font, isSelected, isHovered);
        }
    }

    private void renderIcon(Canvas canvas, int index, float itemY, boolean isSelected, boolean isHovered) {
        float iconX = x + PADDING + 8f;
        float iconCenterY = itemY + ITEM_HEIGHT / 2f;

        int color;
        if (isSelected) {
            color = Theme.PRIMARY;
        } else if (isHovered) {
            color = Theme.TEXT_DESCRIPTION;
        } else {
            color = Theme.TEXT_DISABLED;
        }

        Font iconFont = new Font(null, 11);
        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(color);
            canvas.drawString(CATEGORY_ICONS[index], iconX, iconCenterY + 4f, iconFont, paint);
        }
    }

    private void renderLabel(Canvas canvas, int index, float itemY, Font font, boolean isSelected, boolean isHovered) {
        float textX = x + PADDING + 24f;
        float textY = itemY + ITEM_HEIGHT / 2f + 5f;

        int color;
        if (isSelected) {
            color = Theme.TEXT_TITLE;
        } else if (isHovered) {
            color = Theme.TEXT_DESCRIPTION;
        } else {
            color = Theme.TEXT_DISABLED;
        }

        try (Paint paint = new Paint()) {
            paint.setAntiAlias(true);
            paint.setColor(color);
            canvas.drawString(CATEGORY_NAMES[index], textX, textY, font, paint);
        }
    }

    @Override
    public void update() {
        super.update();

        if (selectionAnimation != null) {
            selectionAnimation.update();
            animatedY = selectionAnimation.getValue();
            if (selectionAnimation.isFinished()) {
                selectionAnimation = null;
            }
        }
    }

    @Override
    public void updateHovered(double mouseX, double mouseY) {
        super.updateHovered(mouseX, mouseY);

        if (!hovered) {
            hoveredIndex = -1;
            return;
        }

        hoveredIndex = -1;
        for (int i = 0; i < CATEGORIES.length; i++) {
            float itemY = y + PADDING + i * ITEM_HEIGHT;
            if (mouseY >= itemY && mouseY <= itemY + ITEM_HEIGHT) {
                if (mouseX >= x + PADDING && mouseX <= x + width - PADDING) {
                    hoveredIndex = i;
                }
                break;
            }
        }
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredIndex >= 0 && hoveredIndex < CATEGORIES.length) {
            Category newCategory = CATEGORIES[hoveredIndex];
            if (newCategory != selectedCategory) {
                setSelectedCategory(newCategory);
            }
        }
    }

    public void setSelectedCategory(Category category) {
        if (category == null || category == selectedCategory) return;

        selectedCategory = category;

        if (onCategoryChanged != null) {
            onCategoryChanged.accept(category);
        }
    }

    private int getSelectedIndex() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i] == selectedCategory) {
                return i;
            }
        }
        return 0;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public float getPreferredWidth() {
        return width;
    }

    public float getPreferredHeight() {
        return height;
    }
}
