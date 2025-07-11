package org.example.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class ConfigInput {
    private int columns = 3;
    private int rows = 3;
    private Map<String, SymbolConfig> symbols;
    private Probabilities probabilities;
    @JsonProperty("win_combinations")
    private Map<String, WinCombination> winCombinations;

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public Map<String, SymbolConfig> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, SymbolConfig> symbols) {
        this.symbols = symbols;
    }

    public Probabilities getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(Probabilities probabilities) {
        this.probabilities = probabilities;
    }

    public Map<String, WinCombination> getWinCombinations() {
        return winCombinations;
    }

    public void setWinCombinations(Map<String, WinCombination> winCombinations) {
        this.winCombinations = winCombinations;
    }

    public static class SymbolConfig {
        @JsonProperty("reward_multiplier")
        private Double rewardMultiplier;
        private String type;
        private Integer extra;
        private String impact;

        public Double getRewardMultiplier() {
            return rewardMultiplier;
        }

        public void setRewardMultiplier(Double rewardMultiplier) {
            this.rewardMultiplier = rewardMultiplier;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getExtra() {
            return extra;
        }

        public void setExtra(Integer extra) {
            this.extra = extra;
        }

        public String getImpact() {
            return impact;
        }

        public void setImpact(String impact) {
            this.impact = impact;
        }
    }

    public static class Probabilities {
        @JsonProperty("standard_symbols")
        private List<StandardSymbolProbability> standardSymbols;
        @JsonProperty("bonus_symbols")
        private BonusSymbolsProbability bonusSymbols;

        public List<StandardSymbolProbability> getStandardSymbols() {
            return standardSymbols;
        }

        public void setStandardSymbols(List<StandardSymbolProbability> standardSymbols) {
            this.standardSymbols = standardSymbols;
        }

        public BonusSymbolsProbability getBonusSymbols() {
            return bonusSymbols;
        }

        public void setBonusSymbols(BonusSymbolsProbability bonusSymbols) {
            this.bonusSymbols = bonusSymbols;
        }
    }

    public static class StandardSymbolProbability {
        private int column;
        private int row;
        private Map<String, Integer> symbols;

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public Map<String, Integer> getSymbols() {
            return symbols;
        }

        public void setSymbols(Map<String, Integer> symbols) {
            this.symbols = symbols;
        }
    }

    public static class BonusSymbolsProbability {
        private Map<String, Integer> symbols;

        public Map<String, Integer> getSymbols() {
            return symbols;
        }

        public void setSymbols(Map<String, Integer> symbols) {
            this.symbols = symbols;
        }
    }

    public static class WinCombination {
        @JsonProperty("reward_multiplier")
        private double rewardMultiplier;
        private String when;
        private Integer count;
        private String group;
        @JsonProperty("covered_areas")
        private List<List<String>> coveredAreas;

        public double getRewardMultiplier() {
            return rewardMultiplier;
        }

        public void setRewardMultiplier(double rewardMultiplier) {
            this.rewardMultiplier = rewardMultiplier;
        }

        public String getWhen() {
            return when;
        }

        public void setWhen(String when) {
            this.when = when;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public List<List<String>> getCoveredAreas() {
            return coveredAreas;
        }

        public void setCoveredAreas(List<List<String>> coveredAreas) {
            this.coveredAreas = coveredAreas;
        }
    }
} 