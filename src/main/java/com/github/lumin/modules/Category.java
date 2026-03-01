package com.github.lumin.modules;

public enum Category {

    COMBAT("\uF01D", "战斗", "ComBat"),
    PLAYER("\uF002", "玩家", "Player"),
    VISUAL("\uF019", "渲染", "Render"),
    MISC("\uF008", "其他", "Others"),
    CLIENT("\uF003", "客户端", "Client");

    public final String icon;
    private final String cnName;
    public final String description;

    Category(String icon, String cnName, String description) {
        this.icon = icon;
        this.cnName = cnName;
        this.description = description;
    }

    public String getName() {
            return cnName;
        }
    }
