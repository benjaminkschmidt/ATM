/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.exception;

/**
 * This exception is thrown when there is not enough money in the ATM
 * @author keepotalize
 */
public class NoCashInTheATMException extends Exception{
    public NoCashInTheATMException(){
        super("There is not enough money in the ATM to process your withdrawal request");
    }
}
