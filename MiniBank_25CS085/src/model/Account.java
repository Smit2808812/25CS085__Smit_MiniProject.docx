package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String accountNumber, owner, email, mobile, type;
    private long balance;
    private final List<String> transactions = new ArrayList<>();
    public Account(String accountNumber, String owner, String email, String mobile, String type, long balance) {
        this.accountNumber=accountNumber; this.owner=owner; this.email=email; this.mobile=mobile; this.type=type; this.balance=balance;
        transactions.add("Account opened with balance " + balance);
    }
    public String getAccountNumber(){ return accountNumber; }
    public long getBalance(){ return balance; }
    public String getOwner(){ return owner; }
    public String getType(){ return type; }
    public void deposit(long amount){ if(amount<=0) throw new IllegalArgumentException("Amount must be positive."); balance+=amount; transactions.add("Deposit: "+amount); }
    public void withdraw(long amount) throws exception.InsufficientFundsException {
        if(amount<=0) throw new IllegalArgumentException("Amount must be positive.");
        if(amount>balance) throw new exception.InsufficientFundsException(amount-balance);
        balance-=amount; transactions.add("Withdrawal: "+amount);
    }
    public void record(String entry){ transactions.add(entry); }
    public String statement(){ StringBuilder s=new StringBuilder("Account: "+accountNumber+"\nOwner: "+owner+"\nType: "+type+"\nBalance: "+balance+"\nTransactions:\n"); for(String x:transactions)s.append("- ").append(x).append('\n'); return s.toString(); }
    @Override public String toString(){ return "Account{number='"+accountNumber+"', owner='"+owner+"', type='"+type+"', balance="+balance+"}"; }
}
