/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.exception;

/**
 * This exception is thrown when 
 * @author keepotalize
 */
public class MissingDatabaseException extends RuntimeException{
    public MissingDatabaseException(){
        super("This ATM is out of service, unable to make a connection to the ledger atm.db");
    }
}
