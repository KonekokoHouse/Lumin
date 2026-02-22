package com.github.lumin.graphics.renderers;

public interface IRenderer {

    void draw();

    void clear();

    default void drawAndClear() {
        draw();
        clear();
    }

    void close();

}
