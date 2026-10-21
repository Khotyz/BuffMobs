package com.khotyz.buffmobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BuffMobsConfigScreen extends Screen {

    private final Screen lastScreen;
    private final List<ConfigMenu.Node> nodes;
    private final boolean root;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final List<CommittingEditBox<?>> editBoxes = new ArrayList<>();
    private ConfigList list;
    private double savedScroll;

    public BuffMobsConfigScreen(Screen lastScreen) {
        super(styledTitle(Component.translatable("buffmobs.config.title")));
        this.lastScreen = lastScreen;
        this.nodes = BuffMobsConfigMenu.create();
        this.root = true;
    }

    private BuffMobsConfigScreen(Screen lastScreen, Component title, List<ConfigMenu.Node> nodes) {
        super(styledTitle(title));
        this.lastScreen = lastScreen;
        this.nodes = nodes;
        this.root = false;
    }

    private static Component styledTitle(Component title) {
        return title.copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }

    @Override
    protected void init() {
        editBoxes.clear();
        layout.addTitleHeader(title, font);
        list = new ConfigList(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), root ? 26 : 42);
        populateList();
        layout.addToContents(list);
        layout.addToFooter(Button.builder(Component.translatable(root ? "gui.done" : "gui.back").copy().withStyle(ChatFormatting.AQUA), button -> onClose()).width(160).build());
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    private void populateList() {
        for (int index = 0; index < nodes.size(); ) {
            ConfigMenu.Node node = nodes.get(index);
            if (node instanceof ConfigMenu.Category first
                    && index + 1 < nodes.size()
                    && nodes.get(index + 1) instanceof ConfigMenu.Category second
                    && canPairCategories(first, second)) {
                list.addCategories(first, second);
                index += 2;
            } else {
                list.addNode(node);
                index++;
            }
        }
    }

    private boolean canPairCategories(ConfigMenu.Category first, ConfigMenu.Category second) {
        int rowWidth = Math.max(180, Math.min(width - 24, 430));
        int buttonWidth = (rowWidth - 6) / 2;
        return font.width(first.title()) + 28 <= buttonWidth
                && font.width(second.title()) + 28 <= buttonWidth;
    }

    @Override
    protected void repositionElements() {
        if (list != null) savedScroll = list.getScrollAmount();
        layout.arrangeElements();
        if (list != null) {
            list.updateSize(width, layout);
            list.setScrollAmount(savedScroll);
        }
    }

    @Override
    public void onClose() {
        commitAll();
        if (lastScreen instanceof BuffMobsConfigScreen parentScreen) parentScreen.refreshEntries();
        if (minecraft != null) minecraft.setScreen(lastScreen);
    }

    @Override
    public void removed() {
        if (list != null) savedScroll = list.getScrollAmount();
        commitAll();
        super.removed();
    }

    private void commitAll() {
        for (CommittingEditBox<?> editBox : List.copyOf(editBoxes)) editBox.commit();
    }

    void refreshEntries() {
        if (list == null) return;
        for (ConfigEntry entry : list.children()) entry.refresh();
    }

    private void openCategory(ConfigMenu.Category category) {
        commitAll();
        if (minecraft != null) minecraft.setScreen(new BuffMobsConfigScreen(this, category.title(), category.children()));
    }

    @SuppressWarnings("unchecked")
    private static <T> ConfigMenu.Option<T> castOption(ConfigMenu.Option<?> option) {
        return (ConfigMenu.Option<T>) option;
    }

    private static Integer parseInteger(String text, ConfigMenu.Option<Integer> option) {
        try {
            int parsed = Integer.parseInt(text.trim());
            return parsed >= option.min() && parsed <= option.max() ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static Double parseDecimal(String text, ConfigMenu.Option<Double> option) {
        try {
            double parsed = Double.parseDouble(text.trim().replace(',', '.'));
            if (!Double.isFinite(parsed)) return null;
            return parsed >= option.min() && parsed <= option.max() ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String formatDecimal(Double value) {
        return value == null ? "" : Double.toString(value);
    }

    private static String formatBound(double bound) {
        if (bound == Math.rint(bound) && Math.abs(bound) < 1.0E15) return Long.toString((long) bound);
        return Double.toString(bound);
    }

    private final class ConfigList extends ContainerObjectSelectionList<ConfigEntry> {

        private ConfigList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        private void addNode(ConfigMenu.Node node) {
            addEntry(new ConfigEntry(node));
        }

        private void addCategories(ConfigMenu.Category first, ConfigMenu.Category second) {
            addEntry(new ConfigEntry(first, second));
        }

        @Override
        public int getRowWidth() {
            return Math.max(180, Math.min(getWidth() - 24, 430));
        }
    }

    private final class ConfigEntry extends ContainerObjectSelectionList.Entry<ConfigEntry> {

        private final ConfigMenu.Node node;
        private final List<AbstractWidget> widgets = new ArrayList<>();
        private final List<ConfigMenu.Category> categories = new ArrayList<>();
        private final List<Button> categoryButtons = new ArrayList<>();
        private MultiLineTextWidget labelWidget;
        private AbstractWidget control;
        private Button resetButton;
        private Runnable refreshAction = () -> {
        };

        private ConfigEntry(ConfigMenu.Node node) {
            this.node = node;
            if (node instanceof ConfigMenu.Category category) {
                categories.add(category);
                createCategoryButtons();
            } else if (node instanceof ConfigMenu.Text text) {
                MultiLineTextWidget textWidget = new MultiLineTextWidget(0, 0, text.text(), font);
                textWidget.setMaxWidth(430);
                textWidget.setMaxRows(3);
                textWidget.setColor(0xB8B8B8);
                control = textWidget;
                widgets.add(textWidget);
            } else if (node instanceof ConfigMenu.Option<?> option) {
                labelWidget = new MultiLineTextWidget(0, 0, option.label(), font);
                labelWidget.setMaxWidth(418);
                labelWidget.setMaxRows(2);
                labelWidget.setColor(0xFFFFFF);
                if (option.tooltip() != null) labelWidget.setTooltip(Tooltip.create(option.tooltip()));
                widgets.add(labelWidget);
                control = createControl(option);
                widgets.add(control);
                if (option.hasDefault()) {
                    resetButton = Button.builder(Component.translatable("buffmobs.config.menu.reset"), button -> {
                                option.resetToDefault();
                                refreshAction.run();
                            })
                            .tooltip(Tooltip.create(Component.translatable("buffmobs.config.menu.reset.tooltip")))
                            .build();
                    widgets.add(resetButton);
                }
            }
        }

        private ConfigEntry(ConfigMenu.Category first, ConfigMenu.Category second) {
            this.node = first;
            categories.add(first);
            categories.add(second);
            createCategoryButtons();
        }

        private void createCategoryButtons() {
            for (ConfigMenu.Category category : categories) {
                Button button = Button.builder(categoryButtonText(category), pressedButton -> openCategory(category)).build();
                categoryButtons.add(button);
                widgets.add(button);
            }
            refreshAction = () -> {
                for (int index = 0; index < categories.size(); index++) {
                    categoryButtons.get(index).setMessage(categoryButtonText(categories.get(index)));
                }
            };
        }

        private Component categoryButtonText(ConfigMenu.Category category) {
            return category.title().copy().withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(" >").withStyle(ChatFormatting.AQUA));
        }

        private void refresh() {
            refreshAction.run();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            if (hovering) guiGraphics.fill(left - 4, top + 1, left + width + 4, top + height - 1, 0x24FFFFFF);
            int accentColor = !categories.isEmpty() ? 0xFFFFAA00 : node instanceof ConfigMenu.Option<?> ? 0xFF55FFFF : 0xFFA0A0A0;
            guiGraphics.fill(left - 4, top + 4, left - 2, top + height - 4, accentColor);
            if (!categories.isEmpty()) {
                int buttonY = top + Math.max(2, (height - 20) / 2);
                if (categoryButtons.size() == 1) {
                    place(categoryButtons.getFirst(), left, buttonY, width);
                } else {
                    int gap = 6;
                    int firstWidth = (width - gap) / 2;
                    int secondWidth = width - gap - firstWidth;
                    place(categoryButtons.get(0), left, buttonY, firstWidth);
                    place(categoryButtons.get(1), left + firstWidth + gap, buttonY, secondWidth);
                }
            } else if (node instanceof ConfigMenu.Option<?> option) {
                labelWidget.setMaxWidth(Math.max(40, width - 12));
                labelWidget.setX(left);
                labelWidget.setY(top + 1);
                labelWidget.setColor(option.isModified() ? 0xFFD75E : 0xFFFFFF);
                int controlY = top + 20;
                int resetWidth = resetButton == null ? 0 : 46;
                int controlWidth = Math.max(80, width - (resetButton == null ? 0 : resetWidth + 6));
                place(control, left, controlY, controlWidth);
                if (resetButton != null) {
                    place(resetButton, left + controlWidth + 6, controlY, resetWidth);
                    resetButton.active = option.isModified();
                }
                if (option.isModified()) guiGraphics.drawString(font, "*", left + width - 7, top + 1, 0xFFD75E, true);
            } else if (control instanceof MultiLineTextWidget textWidget) {
                textWidget.setMaxWidth(width);
                textWidget.setX(left);
                textWidget.setY(top + 2);
            } else if (control != null) {
                place(control, left, top + 2, width);
            }
            for (AbstractWidget widget : widgets) widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        private void place(AbstractWidget widget, int x, int y, int widgetWidth) {
            widget.setX(x);
            widget.setY(y);
            widget.setWidth(widgetWidth);
            widget.setHeight(20);
        }

        private AbstractWidget createControl(ConfigMenu.Option<?> option) {
            return switch (option.kind()) {
                case BOOLEAN -> createBooleanControl(option);
                case ENUM -> createEnumControl(option);
                case INT_SLIDER -> createSliderControl(option);
                case INTEGER -> createIntegerControl(option);
                case DECIMAL -> createDecimalControl(option);
                case STRING -> createStringControl(option);
                case STRING_LIST -> createListControl(option);
            };
        }

        private CycleButton<Boolean> createBooleanControl(ConfigMenu.Option<?> option) {
            ConfigMenu.Option<Boolean> booleanOption = castOption(option);
            CycleButton.Builder<Boolean> builder = CycleButton.onOffBuilder(Boolean.TRUE.equals(booleanOption.value())).displayOnlyValue();
            if (booleanOption.tooltip() != null) builder.withTooltip(value -> Tooltip.create(booleanOption.tooltip()));
            CycleButton<Boolean> button = builder.create(0, 0, 120, 20, booleanOption.label(), (cycleButton, value) -> booleanOption.set(value));
            refreshAction = () -> button.setValue(Boolean.TRUE.equals(booleanOption.value()));
            return button;
        }

        private CycleButton<Enum<?>> createEnumControl(ConfigMenu.Option<?> option) {
            ConfigMenu.Option<Enum<?>> enumOption = castOption(option);
            List<Enum<?>> values = new ArrayList<>(Arrays.asList(enumOption.enumClass().getEnumConstants()));
            CycleButton.Builder<Enum<?>> builder = CycleButton.builder(enumOption::enumLabel)
                    .withValues(values)
                    .withInitialValue(enumOption.value())
                    .displayOnlyValue();
            if (enumOption.tooltip() != null) builder.withTooltip(value -> Tooltip.create(enumOption.tooltip()));
            CycleButton<Enum<?>> button = builder.create(0, 0, 120, 20, enumOption.label(), (cycleButton, value) -> enumOption.set(value));
            refreshAction = () -> button.setValue(enumOption.value());
            return button;
        }

        private IntSlider createSliderControl(ConfigMenu.Option<?> option) {
            ConfigMenu.Option<Integer> intOption = castOption(option);
            IntSlider slider = new IntSlider(intOption);
            if (intOption.tooltip() != null) slider.setTooltip(Tooltip.create(intOption.tooltip()));
            refreshAction = slider::refreshFromOption;
            return slider;
        }

        private CommittingEditBox<Integer> createIntegerControl(ConfigMenu.Option<?> option) {
            ConfigMenu.Option<Integer> intOption = castOption(option);
            Component invalid = Component.translatable("buffmobs.config.menu.invalidNumber", formatBound(intOption.min()), formatBound(intOption.max()));
            CommittingEditBox<Integer> editBox = new CommittingEditBox<>(font, intOption.label(),
                    text -> parseInteger(text, intOption), value -> Integer.toString(value), intOption::value, intOption::set,
                    intOption.tooltip(), invalid);
            editBoxes.add(editBox);
            refreshAction = editBox::refreshFromSource;
            return editBox;
        }

        private CommittingEditBox<Double> createDecimalControl(ConfigMenu.Option<?> option) {
            ConfigMenu.Option<Double> doubleOption = castOption(option);
            Component invalid = Component.translatable("buffmobs.config.menu.invalidNumber", formatBound(doubleOption.min()), formatBound(doubleOption.max()));
            CommittingEditBox<Double> editBox = new CommittingEditBox<>(font, doubleOption.label(),
                    text -> parseDecimal(text, doubleOption), BuffMobsConfigScreen::formatDecimal, doubleOption::value, doubleOption::set,
                    doubleOption.tooltip(), invalid);
            editBoxes.add(editBox);
            refreshAction = editBox::refreshFromSource;
            return editBox;
        }

        private CommittingEditBox<String> createStringControl(ConfigMenu.Option<?> option) {
            ConfigMenu.Option<String> stringOption = castOption(option);
            CommittingEditBox<String> editBox = new CommittingEditBox<>(font, stringOption.label(),
                    text -> text, value -> value == null ? "" : value, stringOption::value, stringOption::set,
                    stringOption.tooltip(), null);
            editBoxes.add(editBox);
            refreshAction = editBox::refreshFromSource;
            return editBox;
        }

        private Button createListControl(ConfigMenu.Option<?> option) {
            ConfigMenu.Option<List<String>> listOption = castOption(option);
            Button.Builder builder = Button.builder(listButtonText(listOption), button -> {
                commitAll();
                if (minecraft != null) minecraft.setScreen(new StringListEditScreen(BuffMobsConfigScreen.this, listOption));
            });
            if (listOption.tooltip() != null) builder.tooltip(Tooltip.create(listOption.tooltip()));
            Button button = builder.build();
            refreshAction = () -> button.setMessage(listButtonText(listOption));
            return button;
        }

        private Component listButtonText(ConfigMenu.Option<List<String>> listOption) {
            return Component.translatable("buffmobs.config.menu.editList", listOption.value().size());
        }
    }

    private static final class IntSlider extends AbstractSliderButton {

        private final ConfigMenu.Option<Integer> option;

        private IntSlider(ConfigMenu.Option<Integer> option) {
            super(0, 0, 120, 20, Component.empty(), normalized(option));
            this.option = option;
            updateMessage();
        }

        private static double normalized(ConfigMenu.Option<Integer> option) {
            if (option.max() <= option.min()) return 0.0;
            double normalized = (option.value() - option.min()) / (option.max() - option.min());
            return Math.max(0.0, Math.min(1.0, normalized));
        }

        private int currentValue() {
            long rounded = Math.round(option.min() + value * (option.max() - option.min()));
            return (int) Math.max(option.min(), Math.min(option.max(), rounded));
        }

        @Override
        protected void updateMessage() {
            setMessage(option.sliderText(currentValue()));
        }

        @Override
        protected void applyValue() {
            option.set(currentValue());
        }

        private void refreshFromOption() {
            value = normalized(option);
            updateMessage();
        }
    }

    private static final class CommittingEditBox<T> extends EditBox {

        private static final int VALID_COLOR = 0xE0E0E0;
        private static final int INVALID_COLOR = 0xFF5555;

        private final Function<String, T> parser;
        private final Function<T, String> formatter;
        private final Supplier<T> source;
        private final Consumer<T> committer;
        private final Component validTooltip;
        private final Component invalidTooltip;

        private CommittingEditBox(Font font, Component message, Function<String, T> parser, Function<T, String> formatter,
                                  Supplier<T> source, Consumer<T> committer, Component validTooltip, Component invalidTooltip) {
            super(font, 0, 0, 120, 20, message);
            this.parser = parser;
            this.formatter = formatter;
            this.source = source;
            this.committer = committer;
            this.validTooltip = validTooltip;
            this.invalidTooltip = invalidTooltip;
            setMaxLength(1024);
            setResponder(text -> updateValidity());
            setValue(formatter.apply(source.get()));
            updateValidity();
        }

        private void updateValidity() {
            boolean valid = parser.apply(getValue()) != null;
            setTextColor(valid ? VALID_COLOR : INVALID_COLOR);
            if (!valid && invalidTooltip != null) {
                setTooltip(Tooltip.create(invalidTooltip));
            } else {
                setTooltip(validTooltip == null ? null : Tooltip.create(validTooltip));
            }
        }

        private void commit() {
            T parsed = parser.apply(getValue());
            if (parsed != null) committer.accept(parsed);
            setValue(formatter.apply(source.get()));
            updateValidity();
        }

        private void refreshFromSource() {
            setValue(formatter.apply(source.get()));
            updateValidity();
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) commit();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                commit();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
