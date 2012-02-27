package codelesson.banking;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: amit
 * Date: 1/4/12
 * Time: 10:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bank {
    private long overdraft;
    private List accounts;

    public Bank(long overdraft, List accounts) {
        this.overdraft = overdraft;
        this.accounts = accounts;
    }
}
