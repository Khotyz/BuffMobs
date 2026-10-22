package com.khotyz.buffmobs.config;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigMenu {

    public interface Node {
    }

    public static final class Category implements Node {
        private final Supplier<Component> titleSupplier;
        private final List<Node> children = new ArrayList<>();

        private Category(Component title) {
            this.titleSupplier = () -> title;
        }

        private Category(Supplier<Component> titleSupplier) {
            this.titleSupplier = titleSupplier;
        }

        public Component title() {
            return titleSupplier.get();
        }

        public List<Node> children() {
            return children;
        }

        public void addEntry(Node node) {
            children.add(node);
        }
    }

    public record Text(Component text) implements Node {
    }

    public enum Kind {
        BOOLEAN, INTEGER, INT_SLIDER, DECIMAL, STRING, STRING_LIST, ENUM
    }

    public static final class Option<T> implements Node {
        private final Kind kind;
        private final Component label;
        private Component tooltip;
        private T value;
        private T defaultValue;
        private boolean hasDefault;
        private Consumer<T> saveConsumer;
        private double min = Double.NEGATIVE_INFINITY;
        private double max = Double.POSITIVE_INFINITY;
        private Class<? extends Enum<?>> enumClass;
        private Function<Enum<?>, Component> enumNameProvider;
        private Function<Integer, Component> sliderText;

        private Option(Kind kind, Component label, T value) {
            this.kind = kind;
            this.label = label;
            this.value = copyValue(value);
        }

        public Kind kind() {
            return kind;
        }

        public Component label() {
            return label;
        }

        public Component tooltip() {
            return tooltip;
        }

        public T value() {
            return copyValue(value);
        }

        public T defaultValue() {
            return copyValue(defaultValue);
        }

        public boolean hasDefault() {
            return hasDefault;
        }

        public double min() {
            return min;
        }

        public double max() {
            return max;
        }

        public Class<? extends Enum<?>> enumClass() {
            return enumClass;
        }

        public Component enumLabel(Enum<?> enumValue) {
            if (enumNameProvider != null) return enumNameProvider.apply(enumValue);
            return Component.literal(enumValue.name());
        }

        public Component sliderText(int sliderValue) {
            if (sliderText != null) return sliderText.apply(sliderValue);
            return Component.literal(String.valueOf(sliderValue));
        }

        public void set(T newValue) {
            T copied = copyValue(newValue);
            if (Objects.equals(value, copied)) return;
            value = copied;
            if (saveConsumer != null) saveConsumer.accept(copyValue(copied));
            BuffMobsConfig.save();
        }

        public boolean isModified() {
            return hasDefault && !Objects.equals(value, defaultValue);
        }

        public void resetToDefault() {
            if (hasDefault) set(defaultValue);
        }

        @SuppressWarnings("unchecked")
        private T copyValue(T toCopy) {
            if (toCopy instanceof List<?> list) return (T) new ArrayList<>(list);
            return toCopy;
        }
    }

    public static final class Builder {
        private final List<Category> categories = new ArrayList<>();
        private final EntryBuilder entryBuilder = new EntryBuilder();

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public EntryBuilder entryBuilder() {
            return entryBuilder;
        }

        public Category getOrCreateCategory(Component title) {
            Category category = new Category(title);
            categories.add(category);
            return category;
        }

        public List<Node> build() {
            return List.copyOf(categories);
        }
    }

    public static final class EntryBuilder {

        public BooleanFieldBuilder startBooleanToggle(Component label, boolean value) {
            return new BooleanFieldBuilder(new Option<>(Kind.BOOLEAN, label, value));
        }

        public DoubleFieldBuilder startDoubleField(Component label, double value) {
            return new DoubleFieldBuilder(new Option<>(Kind.DECIMAL, label, value));
        }

        public IntFieldBuilder startIntField(Component label, int value) {
            return new IntFieldBuilder(new Option<>(Kind.INTEGER, label, value));
        }

        public IntSliderBuilder startIntSlider(Component label, int value, int min, int max) {
            Option<Integer> option = new Option<>(Kind.INT_SLIDER, label, value);
            option.min = min;
            option.max = max;
            return new IntSliderBuilder(option);
        }

        public StringFieldBuilder startStrField(Component label, String value) {
            return new StringFieldBuilder(new Option<>(Kind.STRING, label, value == null ? "" : value));
        }

        public StringListBuilder startStrList(Component label, List<String> value) {
            return new StringListBuilder(new Option<>(Kind.STRING_LIST, label, value));
        }

        public <E extends Enum<E>> EnumFieldBuilder<E> startEnumSelector(Component label, Class<E> enumClass, E value) {
            Option<E> option = new Option<>(Kind.ENUM, label, value);
            option.enumClass = enumClass;
            return new EnumFieldBuilder<>(option, enumClass);
        }

        public TextBuilder startTextDescription(Component text) {
            return new TextBuilder(text);
        }

        public SubCategoryBuilder startSubCategory(Component title) {
            return new SubCategoryBuilder(new Category(title));
        }

        public SubCategoryBuilder startSubCategory(Supplier<Component> titleSupplier) {
            return new SubCategoryBuilder(new Category(titleSupplier));
        }
    }

    abstract static class FieldBuilder<T, B extends FieldBuilder<T, B>> {
        protected final Option<T> option;

        private FieldBuilder(Option<T> option) {
            this.option = option;
        }

        @SuppressWarnings("unchecked")
        private B self() {
            return (B) this;
        }

        public B setDefaultValue(T value) {
            option.defaultValue = option.copyValue(value);
            option.hasDefault = true;
            return self();
        }

        public B setTooltip(Component tooltip) {
            option.tooltip = tooltip;
            return self();
        }

        public B setSaveConsumer(Consumer<T> saveConsumer) {
            option.saveConsumer = saveConsumer;
            return self();
        }

        @SuppressWarnings("unchecked")
        public Option<T> build() {
            if (!option.hasDefault && option.kind == Kind.STRING) {
                option.defaultValue = (T) "";
                option.hasDefault = true;
            }
            return option;
        }
    }

    public static final class BooleanFieldBuilder extends FieldBuilder<Boolean, BooleanFieldBuilder> {
        private BooleanFieldBuilder(Option<Boolean> option) {
            super(option);
        }
    }

    public static final class DoubleFieldBuilder extends FieldBuilder<Double, DoubleFieldBuilder> {
        private DoubleFieldBuilder(Option<Double> option) {
            super(option);
        }

        public DoubleFieldBuilder setMin(double min) {
            option.min = min;
            return this;
        }

        public DoubleFieldBuilder setMax(double max) {
            option.max = max;
            return this;
        }
    }

    public static final class IntFieldBuilder extends FieldBuilder<Integer, IntFieldBuilder> {
        private IntFieldBuilder(Option<Integer> option) {
            super(option);
        }

        public IntFieldBuilder setMin(int min) {
            option.min = min;
            return this;
        }

        public IntFieldBuilder setMax(int max) {
            option.max = max;
            return this;
        }
    }

    public static final class IntSliderBuilder extends FieldBuilder<Integer, IntSliderBuilder> {
        private IntSliderBuilder(Option<Integer> option) {
            super(option);
        }

        public IntSliderBuilder setTextGetter(Function<Integer, Component> sliderText) {
            option.sliderText = sliderText;
            return this;
        }
    }

    public static final class StringFieldBuilder extends FieldBuilder<String, StringFieldBuilder> {
        private StringFieldBuilder(Option<String> option) {
            super(option);
        }
    }

    public static final class StringListBuilder extends FieldBuilder<List<String>, StringListBuilder> {
        private StringListBuilder(Option<List<String>> option) {
            super(option);
        }
    }

    public static final class EnumFieldBuilder<E extends Enum<E>> extends FieldBuilder<E, EnumFieldBuilder<E>> {
        private final Class<E> enumClass;

        private EnumFieldBuilder(Option<E> option, Class<E> enumClass) {
            super(option);
            this.enumClass = enumClass;
        }

        public EnumFieldBuilder<E> setEnumNameProvider(Function<E, Component> provider) {
            option.enumNameProvider = value -> provider.apply(enumClass.cast(value));
            return this;
        }
    }

    public static final class TextBuilder {
        private final Component text;

        private TextBuilder(Component text) {
            this.text = text;
        }

        public Text build() {
            return new Text(text);
        }
    }

    public static final class SubCategoryBuilder {
        private final Category category;

        private SubCategoryBuilder(Category category) {
            this.category = category;
        }

        public SubCategoryBuilder add(Node node) {
            category.addEntry(node);
            return this;
        }

        public Category build() {
            return category;
        }
    }
}
