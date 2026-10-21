package com.khotyz.buffmobs.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class BuffMobsConfigScreen extends Screen {

    private static final int CATEGORY_WIDTH = 210;
    private static final int CATEGORY_HEIGHT = 24;
    private static final int ROW_HEIGHT = 20;
    private static final int PANEL_TOP = 32;
    private static final int RESET_WIDTH = 44;
    private static final int CONTROL_WIDTH = 150;

    private final Screen lastScreen;
    private List<ConfigMenu.Node> categories = List.of();
    private int selectedCategory = 0;
    private OptionList list;
    private CategoryList categoryList;
    private double savedScroll;
    private int categoryWidth;
    private int controlWidth = CONTROL_WIDTH;
    private int resetWidth = RESET_WIDTH;

    public BuffMobsConfigScreen(Screen lastScreen) {
        super(Component.translatable("buffmobs.config.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        if (categories.isEmpty()) categories = BuffMobsConfigMenu.create();
        if (selectedCategory >= categories.size()) selectedCategory = 0;

        int panelHeight = Math.max(ROW_HEIGHT, this.height - PANEL_TOP - 32);
        int leftWidth = Math.min(CATEGORY_WIDTH, Math.max(120, this.width / 4));
        categoryWidth = leftWidth;
        double categoryScroll = categoryList != null ? categoryList.scrollAmount() : 0.0;
        categoryList = new CategoryList(BuffMobsConfigScreen.this.minecraft, leftWidth, panelHeight, PANEL_TOP, 8);
        categoryList.setScrollAmount(categoryScroll);
        addRenderableWidget(categoryList);

        int panelX = 8 + leftWidth + 8;
        int panelWidth = Math.max(140, this.width - panelX - 12);
        resetWidth = Math.max(RESET_WIDTH, BuffMobsConfigScreen.this.font.width(
                Component.translatable("buffmobs.config.menu.reset")) + 10);
        controlWidth = Math.max(80, Math.min(CONTROL_WIDTH, (panelWidth - resetWidth - 40) / 3));
        double scroll = list != null ? list.scrollAmount() : savedScroll;
        list = new OptionList(BuffMobsConfigScreen.this.minecraft, panelWidth, panelHeight, PANEL_TOP, panelX);
        list.fill(selectedCategory());
        list.setScrollAmount(scroll);
        addRenderableWidget(list);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 26, 200, ROW_HEIGHT)
                .build());
    }

    private ConfigMenu.Category selectedCategory() {
        if (categories.isEmpty()) return null;
        ConfigMenu.Node node = categories.get(Math.min(selectedCategory, categories.size() - 1));
        return node instanceof ConfigMenu.Category category ? category : null;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(BuffMobsConfigScreen.this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        ConfigMenu.Category category = selectedCategory();
        if (category != null) {
            graphics.drawString(BuffMobsConfigScreen.this.font, category.title(), 8 + categoryWidth + 8, 12, 0xFFAA00);
        }
    }

    @Override
    public void onClose() {
        if (BuffMobsConfigScreen.this.minecraft != null) BuffMobsConfigScreen.this.minecraft.setScreen(lastScreen);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(event, doubleClick);
    }

    private String shorten(String text, int room) {
        if (BuffMobsConfigScreen.this.font.width(text) <= room) return text;
        return BuffMobsConfigScreen.this.font.plainSubstrByWidth(text,
                Math.max(4, room - BuffMobsConfigScreen.this.font.width("..."))) + "...";
    }

    private static double normalize(int value, int min, int max) {
        if (max <= min) return 0.0;
        return Math.max(0.0, Math.min(1.0, (value - min) / (double) (max - min)));
    }

    void refreshEntries() {
        if (list != null) {
            savedScroll = list.scrollAmount();
            list.fill(selectedCategory());
            list.setScrollAmount(savedScroll);
        }
    }

    private void selectCategory(int index) {
        if (index == selectedCategory) return;
        selectedCategory = index;
        if (list != null) {
            list.fill(selectedCategory());
            list.setScrollAmount(0.0);
        }
    }

    private final class CategoryList extends ContainerObjectSelectionList<CategoryEntry> {

        CategoryList(Minecraft minecraft, int width, int height, int y, int x) {
            super(minecraft, width, height, y, CATEGORY_HEIGHT + 2);
            setX(x);
            List<CategoryEntry> entries = new ArrayList<>();
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i) instanceof ConfigMenu.Category category) entries.add(new CategoryEntry(i, category));
            }
            replaceEntries(entries);
        }

        @Override
        public int getRowWidth() {
            return this.width - 8;
        }

        @Override
        protected int scrollBarX() {
            return this.getRight() - 4;
        }
    }

    private final class CategoryEntry extends ContainerObjectSelectionList.Entry<CategoryEntry> {

        private final int index;
        private final ConfigMenu.Category category;

        CategoryEntry(int index, ConfigMenu.Category category) {
            this.index = index;
            this.category = category;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            int height = getHeight();
            boolean selected = index == selectedCategory;
            graphics.fill(left, top, left + width, top + height,
                    selected ? 0x60FFAA00 : (hovering ? 0x40FFFFFF : 0x20FFFFFF));
            int room = Math.max(10, width - 10);
            String full = category.title().getString();
            int color = selected ? 0xFFFFAA00 : 0xFFFFFFFF;
            int lineHeight = font.lineHeight;
            if (font.width(full) <= room) {
                graphics.drawString(font, full, left + 5, top + (height - lineHeight) / 2 + 1, color);
            } else {
                String first = font.plainSubstrByWidth(full, room);
                String rest = full.substring(first.length()).trim();
                int textY = top + Math.max(1, (height - (lineHeight * 2 + 1)) / 2);
                graphics.drawString(font, first, left + 5, textY, color);
                graphics.drawString(font, shorten(rest, room), left + 5, textY + lineHeight + 1, color);
            }
            if (hovering) graphics.setTooltipForNextFrame(category.title(), mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) return false;
            selectCategory(index);
            return false;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    private final class OptionList extends ContainerObjectSelectionList<OptionRow> {

        OptionList(Minecraft minecraft, int width, int height, int y, int x) {
            super(minecraft, width, height, y, ROW_HEIGHT + 4);
            setX(x);
        }

        void fill(ConfigMenu.Category category) {
            List<OptionRow> rows = new ArrayList<>();
            if (category != null) collect(category.children(), rows);
            replaceEntries(rows);
        }

        private void collect(List<ConfigMenu.Node> nodes, List<OptionRow> rows) {
            for (ConfigMenu.Node node : nodes) {
                if (node instanceof ConfigMenu.Option<?> option) {
                    rows.add(new OptionEntry(option));
                } else if (node instanceof ConfigMenu.Text text) {
                    rows.add(new TextRow(text));
                } else if (node instanceof ConfigMenu.Category sub) {
                    rows.add(new HeaderRow(sub));
                    collect(sub.children(), rows);
                }
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 12;
        }

        @Override
        protected int scrollBarX() {
            return this.getRight() - 5;
        }
    }

    private abstract static class OptionRow extends ContainerObjectSelectionList.Entry<OptionRow> {
    }

    private final class HeaderRow extends OptionRow {

        private final ConfigMenu.Category category;

        HeaderRow(ConfigMenu.Category category) {
            this.category = category;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            int height = getHeight();
            graphics.fill(left, top + 2, left + width, top + height - 2, hovering ? 0x40FFFFFF : 0x28FFFFFF);
            graphics.drawString(BuffMobsConfigScreen.this.font, shorten(category.title().getString(), Math.max(10, width - 8)),
                    left + 4, top + (height - BuffMobsConfigScreen.this.font.lineHeight) / 2 + 1, 0xFFFFAA00);
            if (hovering) graphics.setTooltipForNextFrame(category.title(), mouseX, mouseY);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    private final class TextRow extends OptionRow {

        private final ConfigMenu.Text text;

        TextRow(ConfigMenu.Text text) {
            this.text = text;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            List<FormattedCharSequence> lines = BuffMobsConfigScreen.this.font.split(text.text(), width - 8);
            int y = top + 2;
            for (FormattedCharSequence line : lines) {
                graphics.drawString(BuffMobsConfigScreen.this.font, line, left + 4, y, 0xFFD0D0D0);
                y += BuffMobsConfigScreen.this.font.lineHeight + 1;
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    private final class ConfigSlider extends AbstractSliderButton {

        private final ConfigMenu.Option<Integer> option;
        private final int min;
        private final int max;

        ConfigSlider(ConfigMenu.Option<Integer> option, int width) {
            super(0, 0, width, ROW_HEIGHT, option.label(),
                    normalize(option.value(), (int) option.min(), (int) option.max()));
            this.option = option;
            this.min = (int) option.min();
            this.max = (int) option.max();
        }

        void refreshLabel() {
            updateMessage();
        }

        void applyNormalized(int currentValue) {
            this.value = normalize(currentValue, min, max);
            updateMessage();
        }

        private int currentValue() {
            return min + (int) Math.round(this.value * (max - min));
        }

        @Override
        protected void updateMessage() {
            if (option == null) {
                setMessage(Component.empty());
                return;
            }
            setMessage(Component.literal(option.label().getString() + ": ").append(option.sliderText(currentValue())));
        }

        @Override
        protected void applyValue() {
            option.set(currentValue());
        }
    }

    private final class OptionEntry extends OptionRow {

        private final ConfigMenu.Option<?> option;
        private final List<AbstractWidget> widgets = new ArrayList<>();
        private final AbstractWidget control;
        private final Button resetButton;

        OptionEntry(ConfigMenu.Option<?> option) {
            this.option = option;
            this.control = createControl(option);
            if (this.control != null) {
                if (option.tooltip() != null) this.control.setTooltip(Tooltip.create(option.tooltip()));
                this.widgets.add(this.control);
            }
            this.resetButton = Button.builder(Component.translatable("buffmobs.config.menu.reset"), button -> {
                option.resetToDefault();
                syncControl();
            }).build();
            this.resetButton.setWidth(resetWidth);
            this.resetButton.setHeight(ROW_HEIGHT);
            this.widgets.add(this.resetButton);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private AbstractWidget createControl(ConfigMenu.Option<?> option) {
            int width = controlWidth;
            switch (option.kind()) {
                case BOOLEAN -> {
                    ConfigMenu.Option<Boolean> booleanOption = (ConfigMenu.Option<Boolean>) option;
                    return CycleButton.onOffBuilder(booleanOption.value())
                            .displayOnlyValue()
                            .create(0, 0, width, ROW_HEIGHT, option.label(), (button, value) -> booleanOption.set(value));
                }
                case ENUM -> {
                    ConfigMenu.Option<Enum<?>> enumOption = (ConfigMenu.Option<Enum<?>>) option;
                    List<Enum<?>> values = new ArrayList<>();
                    Class<? extends Enum<?>> enumClass = option.enumClass();
                    for (Enum<?> constant : enumClass.getEnumConstants()) values.add(constant);
                    return CycleButton.<Enum<?>>builder(enumOption::enumLabel, enumOption.value())
                            .withValues(values)
                            .displayOnlyValue()
                            .create(0, 0, width, ROW_HEIGHT, option.label(), (button, value) -> enumOption.set(value));
                }
                case INT_SLIDER -> {
                    ConfigMenu.Option<Integer> intOption = (ConfigMenu.Option<Integer>) option;
                    ConfigSlider slider = new ConfigSlider(intOption, width);
                    slider.refreshLabel();
                    return slider;
                }
                case INTEGER, DECIMAL -> {
                    ConfigMenu.Option<?> numeric = option;
                    boolean integer = option.kind() == ConfigMenu.Kind.INTEGER;
                    EditBox box = new EditBox(BuffMobsConfigScreen.this.font, 0, 0, width, ROW_HEIGHT, option.label());
                    box.setMaxLength(32);
                    box.setValue(integer ? String.valueOf((Integer) numeric.value())
                            : String.valueOf((Double) numeric.value()));
                    box.setResponder(text -> applyNumber(box, numeric, integer, text));
                    return box;
                }
                case STRING -> {
                    ConfigMenu.Option<String> stringOption = (ConfigMenu.Option<String>) option;
                    EditBox box = new EditBox(BuffMobsConfigScreen.this.font, 0, 0, width, ROW_HEIGHT, option.label());
                    box.setMaxLength(512);
                    box.setValue(stringOption.value());
                    box.setResponder(stringOption::set);
                    return box;
                }
                case STRING_LIST -> {
                    ConfigMenu.Option<List<String>> listOption = (ConfigMenu.Option<List<String>>) option;
                    return Button.builder(Component.translatable("buffmobs.config.menu.editList",
                                    listOption.value().size()),
                            button -> {
                                if (BuffMobsConfigScreen.this.minecraft != null) {
                                    BuffMobsConfigScreen.this.minecraft.setScreen(new StringListEditScreen(BuffMobsConfigScreen.this, listOption));
                                }
                            }).build();
                }
                default -> {
                    return null;
                }
            }
        }

        private void applyNumber(EditBox box, ConfigMenu.Option<?> option, boolean integer, String text) {
            String trimmed = text.trim().replace(',', '.');
            try {
                if (integer) {
                    int parsed = (int) Math.round(Double.parseDouble(trimmed));
                    int clamped = (int) clamp(parsed, option.min(), option.max());
                    ((ConfigMenu.Option<Integer>) option).set(clamped);
                    box.setTooltip(null);
                } else {
                    double parsed = Double.parseDouble(trimmed);
                    double clamped = clamp(parsed, option.min(), option.max());
                    ((ConfigMenu.Option<Double>) option).set(clamped);
                    box.setTooltip(null);
                }
            } catch (NumberFormatException ex) {
                box.setTooltip(Tooltip.create(Component.translatable("buffmobs.config.menu.invalidNumber",
                        trim(option.min()), trim(option.max()))));
            }
        }

        private String trim(double bound) {
            if (bound == Double.NEGATIVE_INFINITY) return "-";
            if (bound == Double.POSITIVE_INFINITY) return "+";
            if (bound == Math.rint(bound)) return String.valueOf((long) bound);
            return String.valueOf(bound);
        }

        private double clamp(double value, double min, double max) {
            double result = value;
            if (min != Double.NEGATIVE_INFINITY) result = Math.max(min, result);
            if (max != Double.POSITIVE_INFINITY) result = Math.min(max, result);
            return result;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void syncControl() {
            if (control instanceof CycleButton<?> cycle) {
                ((CycleButton) cycle).setValue(option.value());
            } else if (control instanceof EditBox box) {
                String current = option.kind() == ConfigMenu.Kind.INTEGER
                        ? String.valueOf((Integer) option.value())
                        : option.kind() == ConfigMenu.Kind.DECIMAL
                        ? String.valueOf((Double) option.value())
                        : String.valueOf((String) option.value());
                if (!box.getValue().equals(current)) box.setValue(current);
            } else if (control instanceof ConfigSlider slider) {
                slider.applyNormalized((Integer) option.value());
            } else if (control instanceof Button button && option.kind() == ConfigMenu.Kind.STRING_LIST) {
                button.setMessage(Component.translatable("buffmobs.config.menu.editList",
                        ((List<?>) option.value()).size()));
            }
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            int height = getHeight();
            if (hovering) graphics.fill(left, top, left + width, top + height, 0x18FFFFFF);

            int controlWidgetWidth = control != null ? control.getWidth() : 0;
            int resetX = left + width - resetWidth;
            int controlX = resetX - 4 - controlWidgetWidth;
            int textX = left + 4;
            int textRoom = Math.max(40, controlX - 8 - textX);

            String mark = option.isModified() ? " *" : "";
            String label = option.label().getString();
            int labelRoom = Math.max(20, textRoom - BuffMobsConfigScreen.this.font.width(mark));
            String drawn = shorten(label, labelRoom);
            graphics.drawString(BuffMobsConfigScreen.this.font, drawn + mark, textX,
                    top + (height - BuffMobsConfigScreen.this.font.lineHeight) / 2 + 1,
                    option.isModified() ? 0xFFFFFF55 : 0xFFFFFFFF);

            if (option.tooltip() != null && hovering) {
                graphics.setTooltipForNextFrame(option.tooltip(), mouseX, mouseY);
            }

            if (control != null) {
                control.setX(controlX);
                control.setY(top);
                control.render(graphics, mouseX, mouseY, partialTick);
            }
            resetButton.active = option.hasDefault();
            resetButton.setX(resetX);
            resetButton.setY(top);
            resetButton.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets;
        }
    }
}
