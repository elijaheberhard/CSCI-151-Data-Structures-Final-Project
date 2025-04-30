import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TreeMap;

public class BitcoinController {


    //Basic  Variables
    private Account account;
    int startingBalance = 1000000;
    int daysBehind = 2;
    XYChart.Series<Number, Number> xyChart;

    //API variables
    API api;
    float currentBitcoinPrice;
    private TreeMap<LocalDateTime, Float> priceHistory;

    //JavaFX Line Chart Variables & Other
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    public AnchorPane root;

    //JavaFX TextVariables
    @FXML
    private Text currentPriceText;
    @FXML
    private Text cashBalanceText;
    @FXML
    private Text bitcoinBalanceText;
    @FXML
    private Text profitText;
    @FXML
    private TextField bitcoinTextField;

    //JavaFX Button Variables
    @FXML
    private Button buyButton;
    @FXML
    private Button sellButton;
    @FXML
    private Button buyAllButton;
    @FXML
    private Button sellAllButton;

    //JavaFX TransactionLog Variables
    @FXML
    private TableView<Transaction> transactionLogTable;
    @FXML
    private TableColumn<Transaction, TransactionType> transactionLogTableType;
    @FXML
    private TableColumn<Transaction, Float> transactionLogTableAmount;
    @FXML
    private TableColumn<Transaction, String> transactionLogTableDate;
    @FXML
    private TableColumn<Transaction, Float> transactionLogTableCashBalance;
    @FXML
    private TableColumn<Transaction, Float> transactionLogTableBitcoinBalance;
    @FXML
    private TableColumn<Transaction, Float> transactionLogTableBitcoinPrice;


    private class Movement extends AnimationTimer {

        private final long INTERVAL = 1000000000L; // 1 second
        private long last = 0;

        private int lastPriceMinute = -1;
        private int lastChartHour = -1;

        @Override
        public void handle(long now) {
            if (now - last < INTERVAL) {
                return;
            }
            last = now;

            LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
            int currentMinute = currentTime.getMinute();
            int currentHour = currentTime.getHour();
            int currentSecond = currentTime.getSecond();

            if (currentMinute % 5 == 0 && currentMinute != lastPriceMinute && currentMinute != 0) {
                try {
                    System.out.println("updateCurrentBitcoinPrice()  & updateAccountStats() on every 5th minute");
                    updateCurrentBitcoinPrice();
                    updateAccountStats();
                    xyChart.getData().removeLast();
                    currentPriceText.setText(Float.toString(currentBitcoinPrice));
                    xyChart.getData().add(new XYChart.Data<>((daysBehind*24)+1,currentBitcoinPrice));
                } catch (Exception ignored) {}
                lastPriceMinute = currentMinute;
            }
            if (currentSecond == 6 && currentMinute == 0 && currentHour != lastChartHour) {
                try {
                    System.out.println("updateChart() on every hour");
                    updateChart();
                } catch (Exception ignored) {}
                lastChartHour = currentHour;
            }
        }
    }

    @FXML
    public void initialize() throws IOException {
        api = new API();
        TransactionLog log = new TransactionLog();
        float savedCashBalance;
        float savedBitcoinBalance;
        try{
            if(!log.isEmpty()){
                savedCashBalance = log.retrieveFromCsv(3);
                savedBitcoinBalance = log.retrieveFromCsv(4);
                this.account = new Account(savedCashBalance);
                this.account.setBitcoinBalance(savedBitcoinBalance);
            }
        } catch (Exception e) {
            savedCashBalance = startingBalance;
            savedBitcoinBalance = 0;
        }

        if(this.account == null){
            this.account = new Account(startingBalance);
        }

        updateCurrentBitcoinPrice();
        updateAccountStats();
        updateChart();
        currentPriceText.setText(Float.toString(currentBitcoinPrice));

        cashBalanceText.setText(Double.toString(Math.ceil(account.getCashBalance()*100)/100));
        bitcoinBalanceText.setText(Float.toString(account.getBitcoinBalance()));
        profitText.setText(Float.toString(getTotalProfit()));

        transactionLogTableType.setCellValueFactory(new PropertyValueFactory<>("type"));
        transactionLogTableAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionLogTableDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        transactionLogTableCashBalance.setCellValueFactory(new PropertyValueFactory<>("cashBalance"));
        transactionLogTableBitcoinBalance.setCellValueFactory(new PropertyValueFactory<>("bitcoinBalance"));
        transactionLogTableBitcoinPrice.setCellValueFactory(new PropertyValueFactory<>("bitcoinPrice"));
        transactionLogTable.setItems(FXCollections.observableArrayList(account.getTransactionLog().getAllTransactions()));

        Movement timer = new Movement();
        timer.start();

    }
    @FXML
    public void buyBitcoin() {
        float amount = 0;

        try{
            amount = Float.parseFloat(this.bitcoinTextField.getText());

        }catch(Exception e){
            System.out.println("Enter amount correctly when buying");
        }

        if(account.getCashBalance()/currentBitcoinPrice >= amount && amount > 0){
            float currentPrice = this.currentBitcoinPrice;
            account.buyBitcoin(amount, currentPrice);
            updateAccountStats();
            transactionLogTable.getItems().add(new Transaction(
                    TransactionType.BUY,
                    LocalDateTime.now(),
                    amount,
                    account.getCashBalance(),
                    account.getBitcoinBalance(),
                    currentBitcoinPrice
            ));
        }else{
            System.out.println("Amount has to be non-zero positive");
        }

    }
    @FXML
    public void sellBitcoin() {
        float currentPrice = this.currentBitcoinPrice;
        float amount = 0;

        try{
            amount = Float.parseFloat(this.bitcoinTextField.getText());
        }catch(Exception E){
            System.out.println("Enter amount correctly when selling");
        }

        if(amount > 0 && account.getBitcoinBalance() > amount){
            account.sellBitcoin(amount, currentPrice);
            updateAccountStats();
            transactionLogTable.getItems().add(new Transaction(
                    TransactionType.SELL,
                    LocalDateTime.now(),
                    amount,
                    account.getCashBalance(),
                    account.getBitcoinBalance(),
                    currentBitcoinPrice
            ));
        } else{
            System.out.println("Amount has to be non-zero positive");
        }

    }
    @FXML
    public void buyAllBitcoin() {
        float currentPrice = this.currentBitcoinPrice;
        float cashBalance = account.getCashBalance();
        if(cashBalance > 0){
            float amount = (float) ((cashBalance / currentPrice) - 0.000000001);
            account.buyBitcoin(amount, currentPrice);
            updateAccountStats();
            transactionLogTable.getItems().add(new Transaction(
                    TransactionType.BUY,
                    LocalDateTime.now(),
                    amount,
                    account.getCashBalance(),
                    account.getBitcoinBalance(),
                    currentBitcoinPrice
            ));
        }

    }

    @FXML
    public void sellAllBitcoin() {
        float currentPrice = this.currentBitcoinPrice;
        float bitcoinBalance = account.getBitcoinBalance();

        if(bitcoinBalance > 0) {
            account.sellBitcoin(bitcoinBalance, currentPrice);
            updateAccountStats();
            transactionLogTable.getItems().add(new Transaction(
                    TransactionType.SELL,
                    LocalDateTime.now(),
                    bitcoinBalance,
                    account.getCashBalance(),
                    0,
                    currentBitcoinPrice
            ));
        }

    }
    @FXML
    public void updateCurrentBitcoinPrice() throws IOException{
        this.currentBitcoinPrice = api.getBitcoinPrice();
    }
    public void updateAccountStats() {
        cashBalanceText.setText(String.format("%.2f", account.getCashBalance()));
        bitcoinBalanceText.setText(String.format("%.4f", account.getBitcoinBalance()));
        profitText.setText(String.format("%.2f", getTotalProfit()));
    }

    public float getTotalProfit() {
        return (float)Math.ceil((account.getCashBalance() - startingBalance + account.getBitcoinBalance() * currentBitcoinPrice) * 100) / 100;
    }
    public float getHighestPrice(){
        float highest = Float.MIN_VALUE;
        for(LocalDateTime date : priceHistory.keySet()){
            float price = priceHistory.get(date);
            if (price > highest){
                highest = price;
            }
        }
        return highest;
    }
    public float getLowestPrice(){
        float lowest = Float.MAX_VALUE;
        for(LocalDateTime date : priceHistory.keySet()){
            float price = priceHistory.get(date);
            if (price < lowest){
                lowest = price;
            }
        }
        return lowest;
    }
    public void updateChart() throws IOException {
        priceHistory = api.getBTCPriceHistory("hour", daysBehind);
        profitText.setText(String.format("%.2f", getTotalProfit()));
        float lowestPrice = getLowestPrice();
        float highestPrice = getHighestPrice();

        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setLabel("Past " + (24 * daysBehind) + " Hours (Latest Hour being Present Time)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(1);
        xAxis.setUpperBound(priceHistory.size() + 3);
        xAxis.setTickUnit(1);
        

        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setLabel("Bitcoin Price");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound((Math.floor(lowestPrice * 0.001) / 0.001) - 1000);
        yAxis.setUpperBound((Math.ceil(highestPrice * 0.001) / 0.001) + 1000);
        yAxis.setTickUnit(500);
        lineChart.setCreateSymbols(false);
        XYChart.Series<Number, Number> newSeries = new XYChart.Series<>();
        newSeries.setName("Bitcoin Price");

        int x = 1;
        for (LocalDateTime date : priceHistory.keySet()) {
            float price = priceHistory.get(date);
            newSeries.getData().add(new XYChart.Data<>(x, price));
            x++;
        }

        newSeries.getData().add(new XYChart.Data<>((daysBehind * 24) + 1, this.currentBitcoinPrice));

        lineChart.getData().clear();
        lineChart.getData().add(newSeries);

        this.xyChart = newSeries;
    }

}
