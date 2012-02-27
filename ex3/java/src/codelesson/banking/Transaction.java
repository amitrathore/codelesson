package codelesson.banking;

/**
 * Created by IntelliJ IDEA.
 * User: amit
 * Date: 1/4/12
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Transaction {
    private int id;
    private int accountId;
    private String type;
    private long amount;

    public Transaction(int id, int accountId, String type, long amount) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }
}
