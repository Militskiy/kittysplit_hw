import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {
    private final Map<String, Integer> balanceMap = new LinkedHashMap<>();

    private final ReadFile rf = new ReadFile();

    public String[] getCSVData(String dirPath) {
        String[] splitData = null;
        if (rf.readFileContentsOrNull(dirPath) != null) {
            String data = rf.readFileContentsOrNull(dirPath);
            splitData = data.split("\\r?\\n");
        } else {
            System.out.println("Не найден файл с данными");
            System.exit(0);
        }
        return splitData;
    }

    public void calculateBalance(String[] lines, String delimiter) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String[] column = lines[i].split(delimiter);
            if (i == 0) {
                names.addAll(Arrays.asList(column).subList(2, column.length));
                names.forEach(name -> balanceMap.put(name.trim(), 0));
            } else {
                int sum = 0;
                for (int j = 2; j < column.length; j++) {
                    if (!column[j].isBlank()) {
                        sum += Integer.parseInt(column[j].trim());
                        balanceMap.put(names.get(j - 2).trim(), balanceMap.get(names.get(j - 2)
                                .trim()) - Integer.parseInt(column[j].trim()));
                    }
                }
                balanceMap.put(column[0].trim(), (sum + balanceMap.get(column[0].trim())));
            }
        }
    }

    public void calculateTransactions() {
        Map<String, Integer> debtorsSortedMap = new LinkedHashMap<>();
        Map<String, Integer> creditorsSortedMap = new LinkedHashMap<>();
        sortMaxToMin().forEach((name, sum) -> {
            if (sum > 0) {
                creditorsSortedMap.put(name, sum);
            }
        });
        sortMinToMax().forEach((name, sum) -> {
            if (sum < 0) {
                debtorsSortedMap.put(name, sum);
            }
        });
        for (String creditorName : creditorsSortedMap.keySet()) {
            while (!creditorsSortedMap.get(creditorName).equals(0)) {
                Integer creditorUpdatedSum = creditorsSortedMap.get(creditorName);
                for (String debtorName : debtorsSortedMap.keySet()) {
                    int debtorUpdatedSum = debtorsSortedMap.get(debtorName);
                    if (creditorUpdatedSum != 0 && debtorUpdatedSum != 0) {
                        if (creditorUpdatedSum.equals(-debtorUpdatedSum)) {
                            System.out.println(debtorName + "->" + creditorName + ":" + creditorUpdatedSum);
                            creditorUpdatedSum = 0;
                            debtorUpdatedSum = 0;

                        } else if (creditorUpdatedSum > -debtorUpdatedSum) {
                            System.out.println(debtorName + "->" + creditorName + ":" + -debtorUpdatedSum);
                            creditorUpdatedSum += debtorUpdatedSum;
                            debtorUpdatedSum = 0;
                        } else if (creditorUpdatedSum < -debtorUpdatedSum) {
                            debtorUpdatedSum += creditorUpdatedSum;
                            System.out.println(debtorName + "->" + creditorName + ":" + creditorUpdatedSum);
                            creditorUpdatedSum = 0;
                        }
                    }
                    debtorsSortedMap.put(debtorName, debtorUpdatedSum);
                }
                creditorsSortedMap.put(creditorName, creditorUpdatedSum);
            }
        }
    }

    private Map<String, Integer> sortMaxToMin() {
        return balanceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<String, Integer> sortMinToMax() {
        return balanceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
