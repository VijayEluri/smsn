package net.fortytwo.smsn.brain.model;

import com.google.common.base.Preconditions;
import net.fortytwo.smsn.SemanticSynchrony;
import net.fortytwo.smsn.brain.model.entities.Atom;
import net.fortytwo.smsn.config.DataSource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Filter implements Predicate<Atom>, Serializable {

    private static final Map<String, Integer> sourceToIndex;

    static {
        sourceToIndex = new HashMap<>();
        List<DataSource> sources = SemanticSynchrony.getConfiguration().getSources();
        Preconditions.checkNotNull(sources);
        Preconditions.checkArgument(sources.size() > 0);
        for (int i = 0; i < sources.size(); i++) {
            DataSource source = sources.get(i);
            sourceToIndex.put(source.getName(), i);
        }
    }

    private float minWeight;
    private float defaultWeight;
    private String minSource;
    private String defaultSource;

    private Integer minSourceIndex;

    private static final Filter NO_FILTER = new Filter();

    public static Filter noFilter() {
        return NO_FILTER;
    }

    public float getMinWeight() {
        return minWeight;
    }

    public String getMinSource() {
        return minSource;
    }

    public void setMinWeight(float minWeight) {
        this.minWeight = minWeight;
    }

    public void setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public void setMinSource(String minSource) {
        this.minSource = minSource;
        this.minSourceIndex = indexForSource(minSource);
    }

    public void setDefaultSource(String defaultSource) {
        this.defaultSource = defaultSource;
    }

    private static int indexForSource(final String source) {
        Integer index = sourceToIndex.get(source);
        Preconditions.checkNotNull(index);
        return index;
    }

    private Filter(final float minWeight,
                   final float defaultWeight,
                   final int minSourceIndex,
                   final String defaultSource) {

        checkBetweenZeroAndOne(minWeight);
        checkBetweenZeroAndOne(defaultWeight);
        Preconditions.checkArgument(defaultWeight >= minWeight, "default weight greater than minimum");

        Preconditions.checkNotNull(defaultSource);
        indexForSource(defaultSource);

        this.minSourceIndex = minSourceIndex;
        this.defaultSource = defaultSource;
        this.minWeight = minWeight;
        this.defaultWeight = defaultWeight;
    }

    private Filter() {
        this(0f, 0.5f, 0, SemanticSynchrony.getConfiguration().getSources().get(0).getName());
    }

    public Filter(final float minWeight,
                  final float defaultWeight,
                  final String minSource,
                  final String defaultSource) {
        this(minWeight, defaultWeight, indexForSource(minSource), defaultSource);
    }

    private void checkBetweenZeroAndOne(final float value) {
        Preconditions.checkArgument(value >= 0f && value <= 1f, "argument outside of range [0, 1]");
    }

    public String getDefaultSource() {
        return defaultSource;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public boolean isTrivial() {
        return minSourceIndex == 0 && minWeight == 0;
    }

    @Override
    public boolean test(final Atom atom) {
        Integer sourceIndex = getSourceIndexFor(atom);
        Float weight = atom.getWeight();

        if (null == sourceIndex || null == weight) return false;

        // The weight criterion includes the minimum; if the minimum is 0.25,
        // items with a value of 0.25 and greater will be visible.
        return sourceIndex >= minSourceIndex && weight >= minWeight;
    }

    private Integer getSourceIndexFor(final Atom atom) {
        String source = atom.getSource();
        return null == source ? null : sourceToIndex.get(source);
    }
}
