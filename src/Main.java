public class Main {
    public static void main(String[] args) {
        String dirPath = System.getProperty("user.dir") + "\\input\\input2.csv";
        String delimiter = ",";
        TransactionManager tm = new TransactionManager();

        tm.calculateBalance(tm.getCSVData(dirPath), delimiter);
        tm.calculateTransactions();

    }
}