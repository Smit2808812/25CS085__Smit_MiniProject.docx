package service;
 
import exception.BankException;
import exception.InsufficientFundsException;
import model.Account;
import repository.Repository;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
public class BankService {
    public enum TransactionType { DEPOSIT, WITHDRAWAL }
    public record TransactionRequest(TransactionType type, String accountNumber, long amount) {}
    private final Repository<Account> repository;
    private final TransactionProcessor processor;
    private final Path stateFile, logFile;
    private int nextAccount = 1001;
 
    public BankService(Repository<Account> repository, TransactionProcessor processor, Path stateFile, Path logFile)
            throws IOException, ClassNotFoundException {
        this.repository=repository; this.processor=processor; this.stateFile=stateFile; this.logFile=logFile;
        if(Files.exists(stateFile)) {
            try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(stateFile))) {
                Object value=in.readObject();
                if(value instanceof List<?> list) {
                    for(Object item:list) if(item instanceof Account a) repository.add(a);
                }
            } catch(EOFException ignored) {}
        }
        for(Account a:repository.all()) {
            try { nextAccount=Math.max(nextAccount,Integer.parseInt(a.getAccountNumber().replaceAll("\\D",""))+1); }
            catch(NumberFormatException ignored) {}
        }
    }
    public synchronized ArrayList<Account> listAccounts(){ return repository.all(); }
    private Account find(String number) throws BankException {
        for(Account a:repository.all()) if(a.getAccountNumber().equals(number)) return a;
        throw new BankException("Account not found: "+number);
    }
    public synchronized Account openAccount(String owner,String email,String mobile,String type,long openingBalance) throws BankException,IOException {
        if(openingBalance<0) throw new IllegalArgumentException("Opening balance cannot be negative.");
        String normalized=type.trim().toUpperCase();
        if(!List.of("SAVINGS","CURRENT","FIXED").contains(normalized)) throw new IllegalArgumentException("Account type must be SAVINGS, CURRENT or FIXED.");
        if(owner.isBlank() || !email.contains("@") || mobile.length()!=10 || !mobile.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("Enter valid owner, email and 10-digit mobile.");
        String number="AC"+(nextAccount++);
        Account account=new Account(number,owner,email,mobile,normalized,openingBalance);
        repository.add(account); log("OPEN "+number+" "+openingBalance); return account;
    }
    public synchronized void deposit(String number,long amount) throws BankException,IOException {
        Account a=find(number); a.deposit(amount); log("DEPOSIT "+number+" "+amount);
    }
    public synchronized void withdraw(String number,long amount) throws BankException,IOException {
        Account a=find(number); a.withdraw(amount); log("WITHDRAW "+number+" "+amount);
    }
    public synchronized void transfer(String from,String to,long amount) throws BankException,IOException {
        if(from.equals(to)) throw new IllegalArgumentException("Source and destination accounts must be different.");
        Account source=find(from), destination=find(to);
        source.withdraw(amount); destination.deposit(amount);
        source.record("Transfer to "+to+": "+amount); destination.record("Transfer from "+from+": "+amount);
        log("TRANSFER "+from+" "+to+" "+amount);
    }
    public synchronized String statement(String number) throws BankException { return find(number).statement(); }
    public synchronized void processBatch(ArrayList<TransactionRequest> requests) throws BankException,IOException {
        for(TransactionRequest request:requests) {
            if(request.type()==TransactionType.DEPOSIT) deposit(request.accountNumber(),request.amount());
            else withdraw(request.accountNumber(),request.amount());
        }
    }
    public synchronized String generateReport(Path logsDirectory,Path reportFile) throws IOException {
        Files.createDirectories(logsDirectory); Files.createDirectories(reportFile.getParent());
        long total=0; for(Account a:repository.all()) total+=a.getBalance();
        String report="SMIT MINIBANK - END OF DAY REPORT\nGenerated: "+LocalDateTime.now()+"\nAccounts: "+repository.all().size()+"\nTotal balance: "+total+"\n";
        Files.writeString(reportFile,report);
        return report;
    }
    private void log(String message) throws IOException {
        Path parent=logFile.getParent(); if(parent!=null) Files.createDirectories(parent);
        Files.writeString(logFile,LocalDateTime.now()+" | "+message+System.lineSeparator(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    }
    public synchronized void close() throws IOException {
        Path parent=stateFile.getParent(); if(parent!=null) Files.createDirectories(parent);
        try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(stateFile))) { out.writeObject(repository.all()); }
    }
}
