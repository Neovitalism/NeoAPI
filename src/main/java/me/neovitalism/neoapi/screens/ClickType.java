package me.neovitalism.neoapi.screens;

import net.minecraft.screen.slot.SlotActionType;

public enum ClickType {
    NUM_KEY_1,
    NUM_KEY_2,
    NUM_KEY_3,
    NUM_KEY_4,
    NUM_KEY_5,
    NUM_KEY_6,
    NUM_KEY_7,
    NUM_KEY_8,
    NUM_KEY_9,
    LEFT_CLICK,
    RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    SHIFT_RIGHT_CLICK,
    MIDDLE_CLICK,
    DOUBLE_CLICK,
    DROP,
    DROP_ALL,
    THROW,
    THROW_ALL,
    DRAG_LEFT,
    DRAG_RIGHT,
    DRAG_MIDDLE,
    OFFHAND_SWAP,
    UNKNOWN;

    public static ClickType toClickType(SlotActionType action, int button) {
        switch(action) {
            case PICKUP -> {
                return button == 0 ? LEFT_CLICK : RIGHT_CLICK;
            }
            case QUICK_MOVE -> {
                return button == 0 ? SHIFT_LEFT_CLICK : SHIFT_RIGHT_CLICK;
            }
            case SWAP -> {
                if (button >= 0 && button < 9) {
                    return ClickType.values()[button];
                } else if (button == 40) {
                    return ClickType.OFFHAND_SWAP;
                }
            }
            case CLONE -> {
                return MIDDLE_CLICK;
            }
            case THROW -> {
                return button >= 100 ? (button == 100 ? THROW_ALL : THROW) : (button == 0 ? DROP : DROP_ALL);
            }
            case QUICK_CRAFT -> {
                switch(button) {
                    case 0:
                        return DRAG_LEFT;
                    case 1:
                        return DRAG_RIGHT;
                    case 2:
                        return DRAG_MIDDLE;
                }
            }
            case PICKUP_ALL -> {
                return DOUBLE_CLICK;
            }
        }
        return UNKNOWN;
    }
}
