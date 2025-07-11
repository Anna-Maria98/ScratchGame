package org.example.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GamePlay {
    private final ConfigInput config;
    private final Random random = new Random();

    public GamePlay(ConfigInput config) {
        this.config = config;
    }

    public GameOutput play(double betAmount) {
        List<List<String>> matrix = generateMatrix();
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        double reward = 0;

        // 1. Count occurrences of each standard symbol
        Map<String, Integer> symbolCounts = new HashMap<>();
        for (List<String> row : matrix) {
            for (String symbol : row) {
                if (isStandardSymbol(symbol)) {
                    symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
                }
            }
        }

        // 2. Check for winning combinations for each symbol
        for (String symbol : symbolCounts.keySet()) {
            List<String> wins = winningCombinations(matrix, symbol);
            if (!wins.isEmpty()) {
                appliedWinningCombinations.put(symbol, wins);
            }
        }

        // 3. Calculate reward for each symbol and sum
        for (Map.Entry<String, List<String>> entry : appliedWinningCombinations.entrySet()) {
            String symbol = entry.getKey();
            List<String> wins = entry.getValue();
            double symbolReward = betAmount * getSymbolMultiplier(symbol);
            double winMultiplier = 1.0;
            for (String win : wins) {
                winMultiplier *= config.getWinCombinations().get(win).getRewardMultiplier();
            }
            reward += symbolReward * winMultiplier;
        }

        // 4. Apply bonus symbol if there is a win
        String appliedBonusSymbol = null;
        if (reward > 0) {
            String bonusSymbol = findBonusSymbol(matrix);
            var configSymbols = config.getSymbols();
            if (bonusSymbol != null && configSymbols.get(bonusSymbol).getType().equals("bonus")) {
                appliedBonusSymbol = bonusSymbol;
                ConfigInput.SymbolConfig bonusConfig = configSymbols.get(bonusSymbol);
                if ("multiply_reward".equals(bonusConfig.getImpact())) {
                    reward *= bonusConfig.getRewardMultiplier();
                } else if ("extra_bonus".equals(bonusConfig.getImpact())) {
                    reward += bonusConfig.getExtra();
                }
                // MISS does nothing
            }
        }

        // 5. If no win, set reward to 0 and bonus to null
        if (appliedWinningCombinations.isEmpty()) {
            reward = 0;
            appliedBonusSymbol = null;
        }

        return new GameOutput(matrix, reward, appliedWinningCombinations.isEmpty() ? null : appliedWinningCombinations, appliedBonusSymbol);
    }

    private List<List<String>> generateMatrix() {
        int rows = config.getRows();
        int cols = config.getColumns();
        List<List<String>> matrix = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<String> row = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                String symbol = randomGenerateStandardSymbol(c, r);
                row.add(symbol);
            }
            matrix.add(row);
        }
        // Place bonus symbols randomly in any cell(s)
        bonusSymbols(matrix);
        return matrix;
    }

    private String randomGenerateStandardSymbol(int col, int row) {
        ConfigInput.Probabilities probs = config.getProbabilities();
        ConfigInput.StandardSymbolProbability cellProb = null;
        var standardSymbols = probs.getStandardSymbols();
        for (ConfigInput.StandardSymbolProbability p : standardSymbols) {
            if (p.getColumn() == col && p.getRow() == row) {
                cellProb = p;
                break;
            }
        }
        if (cellProb == null && !standardSymbols.isEmpty()) {
            cellProb = probs.getStandardSymbols().get(0);
        }
        if (cellProb == null || cellProb.getSymbols() == null || cellProb.getSymbols().isEmpty()) {
            return "A"; // default fallback
        }

        var callProbSymbols = cellProb.getSymbols();
        int total = callProbSymbols.values().stream().mapToInt(Integer::intValue).sum();
        int rand = random.nextInt(total);
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : callProbSymbols.entrySet()) {
            cumulative += entry.getValue();
            if (rand < cumulative) {
                return entry.getKey();
            }
        }
        return callProbSymbols.keySet().iterator().next();
    }

    private void bonusSymbols(List<List<String>> matrix) {
        ConfigInput.Probabilities probs = config.getProbabilities();
        if (probs.getBonusSymbols() == null || probs.getBonusSymbols().getSymbols() == null) return;

        int rows = matrix.size();
        int cols = matrix.get(0).size();

        // Pick one random cell to place a bonus symbol
        int r = random.nextInt(rows);
        int c = random.nextInt(cols);
        String bonus = randomGenerateBonusSymbol();
        matrix.get(r).set(c, bonus);
    }

    private String randomGenerateBonusSymbol() {
        Map<String, Integer> bonusProbs = config.getProbabilities().getBonusSymbols().getSymbols();
        int total = bonusProbs.values().stream().mapToInt(Integer::intValue).sum();
        int rand = random.nextInt(total);
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : bonusProbs.entrySet()) {
            cumulative += entry.getValue();
            if (rand < cumulative) {
                return entry.getKey();
            }
        }
        return bonusProbs.keySet().iterator().next(); // fallback
    }

    private String findBonusSymbol(List<List<String>> matrix) {
        for (List<String> row : matrix) {
            for (String symbol : row) {
                if (config.getSymbols().containsKey(symbol) && "bonus".equals(config.getSymbols().get(symbol).getType())) {
                    return symbol;
                }
            }
        }
        return null;
    }

    private boolean isStandardSymbol(String symbol) {
        return config.getSymbols().containsKey(symbol) && "standard".equals(config.getSymbols().get(symbol).getType());
    }

    private double getSymbolMultiplier(String symbol) {
        ConfigInput.SymbolConfig sc = config.getSymbols().get(symbol);
        return sc != null && sc.getRewardMultiplier() != null ? sc.getRewardMultiplier() : 1.0;
    }

    private List<String> winningCombinations(List<List<String>> matrix, String symbol) {
        Map<String, String> groupWins = new HashMap<>(); // group -> best win combination
        int count = (int) matrix.stream().flatMap(Collection::stream).filter(symbol::equals).count();

        // Same_symbols win combinations
        config.getWinCombinations().forEach((key, wc) -> {
            if ("same_symbols".equals(wc.getWhen()) && wc.getCount() != null && count >= wc.getCount()) {
                var group = wc.getGroup();

                if (!groupWins.containsKey(group) ||
                        wc.getRewardMultiplier() > config.getWinCombinations().get(groupWins.get(group)).getRewardMultiplier()) {
                    groupWins.put(group, key);
                }
            }
        });

        // Linear_symbols win combinations
        config.getWinCombinations().forEach((key, wc) -> {
            if ("linear_symbols".equals(wc.getWhen()) && wc.getCoveredAreas() != null) {
                for (List<String> area : wc.getCoveredAreas()) {
                    boolean allMatch = true;
                    for (String pos : area) {
                        String[] parts = pos.split(":");
                        int r = Integer.parseInt(parts[0]);
                        int c = Integer.parseInt(parts[1]);
                        if (r >= matrix.size() || c >= matrix.get(0).size() || !symbol.equals(matrix.get(r).get(c))) {
                            allMatch = false;
                            break;
                        }
                    }
                    if (allMatch) {
                        var group = wc.getGroup();

                        if (!groupWins.containsKey(group) ||
                                wc.getRewardMultiplier() > config.getWinCombinations().get(groupWins.get(group)).getRewardMultiplier()) {
                            groupWins.put(group, key);
                        }
                        break;
                    }
                }
            }
        });

        return new ArrayList<>(groupWins.values());
    }
} 