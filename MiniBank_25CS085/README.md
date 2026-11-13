# Smit MiniBank — 25CS085

A Java console-based MiniBank project for CEUE203: Object Oriented Programming Java.

## Features
- Open SAVINGS, CURRENT, and FIXED accounts
- Deposit and withdraw money
- Transfer money between accounts
- View account statements and list accounts
- Process a batch of deposit/withdrawal transactions
- Generate an end-of-day report
- Save account data between runs
- Record transactions in a log file
- Validate input and handle insufficient funds

## Requirements
- JDK 17 or newer
- VS Code with the Java Extension Pack (optional)

## Run on Windows (PowerShell)
Open a terminal in the project root and run:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out service.MiniBank
```

## Run on macOS / Linux
From the project root:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out service.MiniBank
```

The program creates `data/accounts.ser`, `logs/transactions.log`, and `reports/end-of-day.txt` as needed.

## Menu
1. Open account
2. Deposit
3. Withdraw
4. Transfer
5. Print statement
6. List accounts
7. Process transaction batch
8. Generate end-of-day report
9. Save and exit

## Student
**Smit Prajapati — 25CS085**

> Before submitting, add your own GitHub repository URL to the footer of the Word document, as required by the assignment.
