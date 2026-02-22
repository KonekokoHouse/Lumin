package com.github.lumin.graphics.text;

import java.awt.*;

public interface ITextRenderer {

    void addText(String text, float x, float y, Color color, float scale);

    void draw();

    void clear();

}
