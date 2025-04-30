import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionLog {

    String FILE_NAME = "data.csv";

    public void saveTransaction(Transaction transaction) {
        File file = new File(FILE_NAME);

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, true))) {
            String transactionData =
                    transaction.getType() + "," +
                            transaction.getDate().format(DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss")) + "," +
                            transaction.getAmount() + "," +
                            transaction.getCashBalance() + "," +
                            transaction.getBitcoinBalance() + "," +
                            transaction.getBitcoinPrice() + "\n";

            w.write(transactionData);
            System.out.println(transactionData);
        } catch (Exception ignored) {

        }
    }

    public List<Transaction> getAllTransactions() throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss");

        try(BufferedReader r = new BufferedReader(new FileReader(FILE_NAME))){
            String line;

            while((line = r.readLine()) != null){
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                try{
                    TransactionType type = TransactionType.valueOf(parts[0]);
                    LocalDateTime date = LocalDateTime.parse(parts[1], format);
                    float amount = Float.parseFloat(parts[2]);
                    float cashBalance = Float.parseFloat(parts[3]);
                    float bitcoinBalance = Float.parseFloat(parts[4]);
                    float bitcoinPrice = Float.parseFloat(parts[5]);

                    transactions.add(new Transaction(type, date, amount, cashBalance, bitcoinBalance, bitcoinPrice));
                }catch(Exception ignored){

                }

            }

        }catch(Exception ignored){

        }
        return transactions;
    }
    public Float retrieveFromCsv(int part) throws IOException {
        float item = -1;
        try(BufferedReader r = new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while((line = r.readLine()) != null){
                if((line.trim().isEmpty())){
                    continue;
                }
                String[] parts = line.split(",");
                item = Float.parseFloat(parts[part]);
            }
        } catch(Exception ignored){

        }
        return item;
    }


    public boolean isEmpty(){
        File file = new File(FILE_NAME);
        return file.length() == 0;
    }
}
