package com.github.lumin.graphics;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import static com.mojang.blaze3d.vertex.VertexFormatElement.register;

public class LuminVertexFormats {

    public static final VertexFormatElement ROUND_INNER_RECT =
            register(7, 3, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 4);

    public static final VertexFormatElement ROUND_RADIUS =
            register(8, 4, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 1);

    public static final VertexFormatElement CIRCLE_CENTER =
            register(9, 5, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);

    public static final VertexFormatElement CIRCLE_RADIUS =
            register(10, 6, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 1);

    public static final VertexFormatElement CIRCLE_THICKNESS =
            register(11, 7, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 1);

    public static final VertexFormatElement GRADIENT_COLOR1 =
            register(12, 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);

    public static final VertexFormatElement GRADIENT_COLOR2 =
            register(13, 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.GENERIC, 4);

    public static final VertexFormatElement GRADIENT_START =
            register(14, 8, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);

    public static final VertexFormatElement GRADIENT_END =
            register(15, 9, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);

    public static final VertexFormat ROUND_RECT = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("InnerRect", ROUND_INNER_RECT)
            .add("Radius", ROUND_RADIUS)
            .build();

    public static final VertexFormat LINE = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .build();

    public static final VertexFormat CIRCLE = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("Center", CIRCLE_CENTER)
            .add("Radius", CIRCLE_RADIUS)
            .add("Thickness", CIRCLE_THICKNESS)
            .build();

    public static final VertexFormat GRADIENT_RECT = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color1", GRADIENT_COLOR1)
            .add("Color2", GRADIENT_COLOR2)
            .add("GradientStart", GRADIENT_START)
            .add("GradientEnd", GRADIENT_END)
            .build();

    public static final VertexFormat BLUR = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV0", VertexFormatElement.UV0)
            .build();

}
