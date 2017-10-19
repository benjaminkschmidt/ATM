/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.exception;

/**
 * This exception is thrown when a user tries to withdraw a invalid amount of money
 * @author keepotalize
 */
public class InvalidWithdrawAmountException extends Exception{
    public InvalidWithdrawAmountException(int amount){
        super(String.format("The amount: %d is not a valid amount of money to withdraw",amount));
    }
}
