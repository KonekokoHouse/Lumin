package com.github.lumin.graphics.text;

import com.github.lumin.graphics.text.ttf.TtfFontLoader;
import com.github.lumin.utils.resources.ResourceLocationUtils;

public class StaticFontLoader {

    public static final TtfFontLoader DEFAULT = new TtfFontLoader(ResourceLocationUtils.getIdentifier("fonts/pingfang.ttf"));

    public static final TtfFontLoader ICONS = new TtfFontLoader(ResourceLocationUtils.getIdentifier("fonts/icons.ttf"));

}
