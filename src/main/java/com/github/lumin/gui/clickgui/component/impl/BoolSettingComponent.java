package com.github.lumin.gui.clickgui.component.impl;

import com.github.lumin.gui.Component;
import com.github.lumin.settings.impl.BoolSetting;

public class BoolSettingComponent extends Component {
    private final BoolSetting setting;

    public BoolSettingComponent(BoolSetting setting) {
        this.setting = setting;
    }
}
