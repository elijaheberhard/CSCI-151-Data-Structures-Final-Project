import java.time.LocalDateTime;

public class Account {

    float cashBalance;
    float bitcoinBalance;
    private final TransactionLog transactionLog;

    public Account(float initialBalance){
        this.cashBalance = initialBalance;
        this.bitcoinBalance = 0;
        this.transactionLog = new TransactionLog();
    }

    public float getCashBalance(){
        return cashBalance;
    }
    public float getBitcoinBalance(){
        return bitcoinBalance;
    }
    public TransactionLog getTransactionLog(){
        return this.transactionLog;
    }

    public void setBitcoinBalance(float bitcoinBalance){
        this.bitcoinBalance = bitcoinBalance;
    }

    public void buyBitcoin(float amount, float currentPrice){
        float cost = (float) Math.round( (amount*currentPrice) *100 )/100;
        float balance = (float) Math.round(this.cashBalance * 100)/100;
        if(cost <= balance){
            this.cashBalance -= cost;
            this.bitcoinBalance += amount;
            this.transactionLog.saveTransaction(new Transaction(TransactionType.BUY, LocalDateTime.now(), amount, this.cashBalance, this.bitcoinBalance, currentPrice));
            System.out.println("Amount: " + amount + " | Cost: " + cost + " | Bitcoin Price: " + currentPrice);
        } else {
            System.out.println("not enough money to buy");
        }
    }
    public void sellBitcoin(float amount, float currentPrice){
        if(amount <= bitcoinBalance){
            float profitFromSale = amount * currentPrice;
            this.bitcoinBalance -= amount;
            this.cashBalance += profitFromSale;
            this.transactionLog.saveTransaction(new Transaction(TransactionType.SELL, LocalDateTime.now(), amount, this.cashBalance, this.bitcoinBalance, currentPrice));
            System.out.println("Amount: " + amount + " | Cost: " + profitFromSale + " | Bitcoin Price: " + currentPrice);
        } else {
            System.out.println("not enough bitcoin to sell");
        }
    }






}
