package com.khotyz.buffmobs.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import net.minecraft.client.input.KeyEvent;
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
    private static final int LEFT_MOUSE_BUTTON = resolveInputConstant("MOUSE_BUTTON_LEFT", 0);
    private static final int ESCAPE_KEY = resolveInputConstant("KEY_ESCAPE", 256);

    private final Screen lastScreen;
    private List<ConfigMenu.Node> categories = List.of();
    private int selectedCategory = 0;
    private OptionList list;
    private CategoryList categoryList;
    private Button doneButton;
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

        int leftWidth = Math.min(CATEGORY_WIDTH, Math.max(120, this.width / 4));
        categoryWidth = leftWidth;
        int panelX = 8 + leftWidth + 8;
        int panelWidth = Math.max(140, this.width - panelX - 12);
        int titleLines = 0;
        for (ConfigMenu.Node node : categories) {
            if (node instanceof ConfigMenu.Category category) {
                titleLines = Math.max(titleLines, font.split(category.title(), Math.max(1, panelWidth - 8)).size());
            }
        }
        int panelTop = Math.max(PANEL_TOP, 12 + titleLines * font.lineHeight + 8);
        int panelHeight = Math.max(ROW_HEIGHT, this.height - panelTop - 32);

        double categoryScroll = categoryList != null ? categoryList.scrollAmount() : 0.0;
        categoryList = new CategoryList(BuffMobsConfigScreen.this.minecraft, leftWidth, panelHeight, panelTop, 8);
        categoryList.setScrollAmount(categoryScroll);
        addRenderableWidget(categoryList);

        resetWidth = Math.max(RESET_WIDTH, BuffMobsConfigScreen.this.font.width(
                Component.translatable("buffmobs.config.menu.reset")) + 10);
        controlWidth = Math.max(80, Math.min(CONTROL_WIDTH, (panelWidth - resetWidth - 40) / 3));
        double scroll = list != null ? list.scrollAmount() : savedScroll;
        list = new OptionList(BuffMobsConfigScreen.this.minecraft, panelWidth, panelHeight, panelTop, panelX);
        list.fill(selectedCategory());
        list.setScrollAmount(scroll);
        addRenderableWidget(list);

        doneButton = Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 26, 200, ROW_HEIGHT)
                .build();
        addRenderableWidget(doneButton);
    }

    private ConfigMenu.Category selectedCategory() {
        if (categories.isEmpty()) return null;
        ConfigMenu.Node node = categories.get(Math.min(selectedCategory, categories.size() - 1));
        return node instanceof ConfigMenu.Category category ? category : null;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.text(font, title, width / 2 - font.width(title) / 2, 10, 0xFFFFFFFF);
        ConfigMenu.Category category = selectedCategory();
        if (category != null) {
            int categoryX = 8 + categoryWidth + 8;
            List<FormattedCharSequence> lines = font.split(category.title(), Math.max(1, width - categoryX - 12));
            for (int index = 0; index < lines.size(); index++) {
                graphics.text(font, lines.get(index), categoryX, 12 + index * font.lineHeight, 0xFFFFAA00);
            }
        }
    }

    @Override
    public void onClose() {
        if (BuffMobsConfigScreen.this.minecraft != null) {
            MinecraftScreenCompat.set(BuffMobsConfigScreen.this.minecraft, lastScreen);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (isEscapeKey(event)) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (doneButton != null && isLeftMouseButton(event) && doneButton.isMouseOver(event.x(), event.y())) {
            setFocused(doneButton);
            onClose();
            return true;
        }
        if (categoryList != null) {
            CategoryEntry entry = categoryList.entryAt(event.x(), event.y());
            if (entry != null) {
                categoryList.setFocused(entry);
                setFocused(categoryList);
                if (entry.mouseClicked(event, doubleClick)) return true;
            }
        }
        if (list != null) {
            OptionRow entry = list.entryAt(event.x(), event.y());
            if (entry != null) {
                list.setFocused(entry);
                setFocused(list);
                if (entry.mouseClicked(event, doubleClick)) return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private static double normalize(int value, int min, int max) {
        if (max <= min) return 0.0;
        return Math.max(0.0, Math.min(1.0, (value - min) / (double) (max - min)));
    }

    private static int resolveInputConstant(String fieldName, int fallback) {
        try {
            return InputConstants.class.getField(fieldName).getInt(null);
        } catch (ReflectiveOperationException exception) {
            return fallback;
        }
    }

    static boolean isLeftMouseButton(MouseButtonEvent event) {
        return event.button() == LEFT_MOUSE_BUTTON;
    }

    static boolean isEscapeKey(KeyEvent event) {
        return event.key() == ESCAPE_KEY;
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
                if (categories.get(i) instanceof ConfigMenu.Category category) {
                    List<FormattedCharSequence> lines = font.split(category.title(), Math.max(1, getRowWidth() - 10));
                    for (int start = 0; start < lines.size(); start += 2) {
                        entries.add(new CategoryEntry(i, category,
                                List.copyOf(lines.subList(start, Math.min(start + 2, lines.size())))));
                    }
                }
            }
            clearEntries();
            for (CategoryEntry entry : entries) addEntry(entry);
        }

        CategoryEntry entryAt(double mouseX, double mouseY) {
            return getEntryAtPosition(mouseX, mouseY);
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
        private final List<FormattedCharSequence> lines;

        CategoryEntry(int index, ConfigMenu.Category category, List<FormattedCharSequence> lines) {
            this.index = index;
            this.category = category;
            this.lines = lines;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            int height = getHeight();
            boolean selected = index == selectedCategory;
            graphics.fill(left, top, left + width, top + height,
                    selected ? 0x60FFAA00 : (hovering ? 0x40FFFFFF : 0x20FFFFFF));
            int color = selected ? 0xFFFFAA00 : 0xFFFFFFFF;
            int lineHeight = font.lineHeight;
            int textHeight = lines.size() * lineHeight + Math.max(0, lines.size() - 1);
            int textY = top + Math.max(1, (height - textHeight) / 2);
            for (int index = 0; index < lines.size(); index++) {
                graphics.text(font, lines.get(index), left + 5, textY + index * (lineHeight + 1), color);
            }
            if (hovering) graphics.setTooltipForNextFrame(BuffMobsConfigScreen.this.font, category.title(), mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!isLeftMouseButton(event)) return false;
            selectCategory(index);
            return true;
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
            clearEntries();
            for (OptionRow row : rows) addEntry(row);
        }

        OptionRow entryAt(double mouseX, double mouseY) {
            return getEntryAtPosition(mouseX, mouseY);
        }

        private void collect(List<ConfigMenu.Node> nodes, List<OptionRow> rows) {
            for (ConfigMenu.Node node : nodes) {
                if (node instanceof ConfigMenu.Option<?> option) {
                    OptionEntry entry = new OptionEntry(option);
                    rows.add(entry);
                    for (int line = 1; line < entry.labelLines.size(); line++) {
                        rows.add(new OptionLabelRow(option, entry.labelLines.get(line), line == entry.labelLines.size() - 1));
                    }
                } else if (node instanceof ConfigMenu.Text text) {
                    List<FormattedCharSequence> lines = font.split(text.text(), Math.max(1, getRowWidth() - 8));
                    for (FormattedCharSequence line : lines) rows.add(new TextRow(line));
                } else if (node instanceof ConfigMenu.Category sub) {
                    List<FormattedCharSequence> lines = font.split(sub.title(), Math.max(1, getRowWidth() - 8));
                    for (FormattedCharSequence line : lines) rows.add(new HeaderRow(sub, line));
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
        private final FormattedCharSequence line;

        HeaderRow(ConfigMenu.Category category, FormattedCharSequence line) {
            this.category = category;
            this.line = line;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            int height = getHeight();
            graphics.fill(left, top + 2, left + width, top + height - 2, hovering ? 0x40FFFFFF : 0x28FFFFFF);
            graphics.text(BuffMobsConfigScreen.this.font, line, left + 4,
                    top + (height - BuffMobsConfigScreen.this.font.lineHeight) / 2 + 1, 0xFFFFAA00);
            if (hovering) graphics.setTooltipForNextFrame(BuffMobsConfigScreen.this.font, category.title(), mouseX, mouseY);
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

        private final FormattedCharSequence line;

        TextRow(FormattedCharSequence line) {
            this.line = line;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int height = getHeight();
            graphics.text(BuffMobsConfigScreen.this.font, line, left + 4,
                    top + (height - BuffMobsConfigScreen.this.font.lineHeight) / 2 + 1, 0xFFD0D0D0);
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

    private final class OptionLabelRow extends OptionRow {

        private final ConfigMenu.Option<?> option;
        private final FormattedCharSequence line;
        private final boolean lastLine;

        OptionLabelRow(ConfigMenu.Option<?> option, FormattedCharSequence line, boolean lastLine) {
            this.option = option;
            this.line = line;
            this.lastLine = lastLine;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            int height = getHeight();
            if (hovering) graphics.fill(left, top, left + width, top + height, 0x18FFFFFF);
            int textX = left + 4;
            int textY = top + (height - font.lineHeight) / 2 + 1;
            int color = option.isModified() ? 0xFFFFFF55 : 0xFFFFFFFF;
            graphics.text(font, line, textX, textY, color);
            if (lastLine && option.isModified()) {
                graphics.text(font, " *", textX + font.width(line), textY, color);
            }
            if (option.tooltip() != null && hovering) {
                graphics.setTooltipForNextFrame(font, option.tooltip(), mouseX, mouseY);
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
        private final List<FormattedCharSequence> labelLines;

        OptionEntry(ConfigMenu.Option<?> option) {
            this.option = option;
            this.control = createControl(option);
            if (this.control != null) {
                this.control.setWidth(controlWidth);
                this.control.setHeight(ROW_HEIGHT);
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
            int widgetWidth = control == null ? 0 : control.getWidth();
            int textRoom = Math.max(40, list.getRowWidth() - resetWidth - widgetWidth - 16);
            this.labelLines = font.split(option.label(), Math.max(1, textRoom - font.width(" *")));
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
                    return CycleButton.<Enum<?>>builder(enumOption::enumLabel, () -> enumOption.value())
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
                                    MinecraftScreenCompat.set(BuffMobsConfigScreen.this.minecraft,
                                            new StringListEditScreen(BuffMobsConfigScreen.this, listOption));
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
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int left = getX();
            int top = getY();
            int width = getWidth();
            int height = getHeight();
            if (hovering) graphics.fill(left, top, left + width, top + height, 0x18FFFFFF);

            int controlWidgetWidth = control != null ? control.getWidth() : 0;
            int resetX = left + width - resetWidth;
            int controlX = resetX - 4 - controlWidgetWidth;
            int textX = left + 4;
            int textY = top + (height - BuffMobsConfigScreen.this.font.lineHeight) / 2 + 1;
            int color = option.isModified() ? 0xFFFFFF55 : 0xFFFFFFFF;
            FormattedCharSequence firstLine = labelLines.get(0);
            graphics.text(BuffMobsConfigScreen.this.font, firstLine, textX, textY, color);
            if (labelLines.size() == 1 && option.isModified()) {
                graphics.text(font, " *", textX + font.width(firstLine), textY, color);
            }

            if (option.tooltip() != null && hovering) {
                graphics.setTooltipForNextFrame(BuffMobsConfigScreen.this.font, option.tooltip(), mouseX, mouseY);
            }

            if (control != null) {
                control.setX(controlX);
                control.setY(top + (height - ROW_HEIGHT) / 2);
                control.extractRenderState(graphics, mouseX, mouseY, partialTick);
            }
            resetButton.active = option.hasDefault();
            resetButton.setX(resetX);
            resetButton.setY(top + (height - ROW_HEIGHT) / 2);
            resetButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
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
