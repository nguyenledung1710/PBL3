package com.pbl.theme;

import com.pbl.event.EventColorChange;
import com.pbl.swing.PanelBackground;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.jdesktop.animation.timing.Animator;

public class ThemeColorChange {

    private Mode mode;
    private Animator animator;
    private PanelBackground panelBackground;
    private List<ThemeColor> themeColor;
    private List<EventColorChange> eventsColorChange;
    public static ThemeColorChange instance;

    public static ThemeColorChange getInstance() {
        if (instance == null) {
            instance = new ThemeColorChange();
        }
        return instance;
    }


    private ThemeColorChange() {
        themeColor = new ArrayList<>();
        eventsColorChange = new ArrayList<>();
        animator = new Animator(300);
        animator.setResolution(5);
    }

    public void addThemes(ThemeColor theme) {
        themeColor.add(theme);
    }

    public void changeMode(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            for (ThemeColor color : themeColor) {
                animator.removeTarget(color.getTarget());
                animator.addTarget(color.newTarget(mode));
            }
            animator.start();
        }
    }

    public void addEventColorChange(EventColorChange event) {
        eventsColorChange.add(event);
    }

    public void ruenEventColorChange(Color color) {
        for (EventColorChange evnet : eventsColorChange) {
            evnet.colorChange(color);
        }
    }

    public void initBackground(PanelBackground panelBackground) {
        this.panelBackground = panelBackground;
    }

    // Điều chỉnh backgroud , Hướng phát triển => có thể cho người dùng tự thêm ảnh background
    public void changeBackgroundImage(String image) {
        if (image.equals("")) {
            panelBackground.setImage(null);
        } else {
            String path = "/com/pbl/background/" + image;
            panelBackground.setImage(new ImageIcon(getClass().getResource(path)));
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public static enum Mode {
        DARK, LIGHT
    }
}
