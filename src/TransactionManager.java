import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {
    Map<String, Integer> transactionList = new LinkedHashMap<>();

    ReadFile rf = new ReadFile();

    String[] getCSVData(String dirPath) {
        String data = rf.readFileContentsOrNull(dirPath);
        return data.split("\\r?\\n");
    }

    void calculateBalance(String[] lines, String delimiter) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String[] column = lines[i].split(delimiter);
            if (i == 0) {
                names.addAll(Arrays.asList(column).subList(2, column.length));
                names.forEach(name -> transactionList.put(name, 0));
            } else {
                int sum = 0;
                for (int j = 2; j < column.length; j++) {
                    if (!column[j].isEmpty()) {
                        sum += Integer.parseInt(column[j]);
                        transactionList.put(names.get(j - 2), transactionList.get(names.get(j - 2)) - Integer.parseInt(column[j]));
                    }
                }
                transactionList.put(column[0], (sum + transactionList.get(column[0])));
            }
        }
    }

    Map<String, Integer> sortMaxToMin() {
        return transactionList.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    Map<String, Integer> sortMinToMax() {
        return transactionList.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    void calculateTransactions() {
        LinkedHashMap<String, Integer> debtorsSortedMap = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> creditorsSortedMap = new LinkedHashMap<>();
        sortMaxToMin().forEach((k, v) -> {
            if (v > 0) {
                creditorsSortedMap.put(k, v);
            }
        });
        sortMinToMax().forEach((k, v) -> {
            if (v < 0) {
                debtorsSortedMap.put(k, v);
            }
        });
        for (String k : creditorsSortedMap.keySet()) {
            while (!creditorsSortedMap.get(k).equals(0)) {
                Integer update = creditorsSortedMap.get(k);
                for (String key : debtorsSortedMap.keySet()) {
                    int updateDebtor = debtorsSortedMap.get(key);
                    if (update != 0 && updateDebtor != 0) {
                        if (update.equals(-updateDebtor)) {
                            update = 0;
                            updateDebtor = 0;
                            System.out.println(key + "->" + k + ":" + -debtorsSortedMap.get(key));
                        } else if (update > -updateDebtor) {
                            update += updateDebtor;
                            updateDebtor = 0;
                            System.out.println(key + "->" + k + ":" + -debtorsSortedMap.get(key));
                        } else if (update < -updateDebtor) {
                            updateDebtor += update;
                            update = 0;
                            System.out.println(key + "->" + k + ":" + creditorsSortedMap.get(k));
                        }
                    }
                    debtorsSortedMap.put(key, updateDebtor);
                }
                creditorsSortedMap.put(k, update);
            }
        }
    }
}
