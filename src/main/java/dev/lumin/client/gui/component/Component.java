package dev.lumin.client.gui.component;

import dev.lumin.client.gui.animation.Animation;
import io.github.humbleui.skija.Canvas;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    protected boolean visible = true;
    protected boolean enabled = true;
    protected boolean hovered = false;
    protected boolean focused = false;

    protected Component parent;
    protected final List<Component> children = new ArrayList<>();

    protected final List<Animation> animations = new ArrayList<>();

    public Component() {
    }

    public Component(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(Canvas canvas, float mouseX, float mouseY);

    public void update() {
        for (int i = animations.size() - 1; i >= 0; i--) {
            Animation anim = animations.get(i);
            anim.update();
            if (anim.isFinished()) {
                animations.remove(i);
            }
        }
        for (Component child : children) {
            child.update();
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible() || !isEnabled()) {
            return false;
        }

        if (isMouseOver(mouseX, mouseY)) {
            for (int i = children.size() - 1; i >= 0; i--) {
                if (children.get(i).mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            onClick(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isVisible() || !isEnabled()) {
            return false;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (isMouseOver(mouseX, mouseY)) {
            onRelease(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isVisible() || !isEnabled()) {
            return false;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }

        if (isMouseOver(mouseX, mouseY)) {
            onDrag(mouseX, mouseY, button, deltaX, deltaY);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isVisible() || !isEnabled()) {
            return false;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }

        if (isMouseOver(mouseX, mouseY)) {
            onScroll(mouseX, mouseY, delta);
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isVisible() || !isEnabled()) {
            return false;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return onKeyPress(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!isVisible() || !isEnabled()) {
            return false;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).charTyped(chr, modifiers)) {
                return true;
            }
        }

        return onCharTyped(chr, modifiers);
    }

    public void updateHovered(double mouseX, double mouseY) {
        hovered = isMouseOver(mouseX, mouseY);
        for (Component child : children) {
            child.updateHovered(mouseX, mouseY);
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    protected void onClick(double mouseX, double mouseY, int button) {
    }

    protected void onRelease(double mouseX, double mouseY, int button) {
    }

    protected void onDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    }

    protected void onScroll(double mouseX, double mouseY, double delta) {
    }

    protected boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    protected boolean onCharTyped(char chr, int modifiers) {
        return false;
    }

    protected void addAnimation(Animation animation) {
        animations.add(animation);
        animation.start();
    }

    public void addChild(Component child) {
        child.setParent(this);
        children.add(child);
    }

    public void removeChild(Component child) {
        child.setParent(null);
        children.remove(child);
    }

    public float getAbsoluteX() {
        return parent == null ? x : parent.getAbsoluteX() + x;
    }

    public float getAbsoluteY() {
        return parent == null ? y : parent.getAbsoluteY() + y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setBounds(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isFocused() {
        return focused;
    }

    public Component getParent() {
        return parent;
    }

    public List<Component> getChildren() {
        return children;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
