package com.github.lumin.gui.clickgui.component.impl;

import com.github.lumin.gui.Component;
import com.github.lumin.settings.impl.StringSetting;

public class StringSettingComponent extends Component {
    private StringSetting setting;

    public StringSettingComponent(StringSetting setting) {
        this.setting = setting;
    }
}
