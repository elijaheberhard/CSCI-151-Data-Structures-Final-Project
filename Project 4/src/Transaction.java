import java.time.LocalDateTime;
public class Transaction {

    private final TransactionType type;
    private final float amount;
    private final LocalDateTime date;
    private final double cashBalance;
    private final float bitcoinBalance;
    private final float bitcoinPrice;



    public Transaction(TransactionType type, LocalDateTime date,float amount, double cashBalance, float bitcoinBalance, float bitcoinPrice) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.cashBalance = cashBalance;
        this.bitcoinBalance = bitcoinBalance;
        this.bitcoinPrice = bitcoinPrice;

    }

    public TransactionType getType() {
        return type;
    }
    public float getAmount() {
        return amount;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public  double getCashBalance(){
        return cashBalance;
    }
    public  float getBitcoinBalance(){
        return bitcoinBalance;
    }
    public  float getBitcoinPrice(){
        return bitcoinPrice;
    }

}
