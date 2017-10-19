/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.sql;

/**
 * This exception when there is not enough money in the use account
 * @author keepotalize
 */
public class NoCashInTheAccountException extends Exception{
    public NoCashInTheAccountException(){
        super("There is not enough money in the account to process your withdrawal request");
    }
}
