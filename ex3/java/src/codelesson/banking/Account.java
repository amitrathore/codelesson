package codelesson.banking;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: amit
 * Date: 1/4/12
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Account {
    private int id;
    private String type;
    private List transactions;
    private long amount;
    private Date startDate;

    public Account(int id, String type, List transactions, long amount, Date startDate) {
        this.id = id;
        this.type = type;
        this.transactions = transactions;
        this.amount = amount;
        this.startDate = startDate;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public long getBalance() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

}
