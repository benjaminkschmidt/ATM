/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.sql;

/**
 *
 * @author mkamalumpundi
 */
public interface IRepository {
    
    //Checks current ATM balance
    int checkATM();
    
    //Check the current account balance of a person
    double checkBalance();
        
    //Debit the balance of the ATM
    boolean debitATM(int cash);

    //Deposit $n into the ATM
    boolean depositMoney(double money);
    
    //Sets ATM cash reserve to zero
    boolean emptyATM();

    //Greet user
    String getGreeting();    
    
    //Add $10k to the ATM cash reserve
    boolean restrockATM();
    
    //Add $n   to the ATM cash reserve
    boolean restockATM(int cash);
    
    //Take Option amount of money from ATM
    boolean withdrawMoney(int cash);

}