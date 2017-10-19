/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.sql;

import atm.exception.MissingDatabaseException;
import atm.exception.NoCashInTheAccountException;
import atm.exception.NoCashInTheATMException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import atm.sql.*;

/**
 * This class contains methods to simulate an ATM by using a SQLite database
 * SQLite database can be found in /resources/ATM.db 
 * Use SQLite Studio to view the database
 * @author keepotalize
 */
public class Repository implements IRepository{

    /**
     * Primary key for user, utilized in ledger for storing transactions
     */
    private int UserKey;
    /**
     * First name of user
     */
    private String FirstName;
    /**
    * Last name of user
    */
    private String LastName;
    /**
     * These are the amounts to withdraw that the ATM accepts
     */
    public static final int[] WithdrawAmounts = new int[]{20,40,60,80,100,200};
    /**
     * Don't use this constructor, most methods will not work properly
     */
    private Repository(){};
    
    /**
     * Allows for transactions to the ATM
     * @param AccountNumber
     * @param PIN
     * @throws NoSuchElementException 
     */
    public Repository(String AccountNumber, String PIN) throws NoSuchElementException{
        String query = "SELECT LoginKey, FirstName, LastName FROM Login WHERE AccountNumber = ? AND PersonalNumber = ? Limit 1";
        
        try(Connection conn = this.connect();
                PreparedStatement ps  = conn.prepareStatement(query);){
            
            ps.setString(1, AccountNumber);
            ps.setString(2, PIN);
            
            ResultSet rs  = ps.executeQuery();
            if(rs.next()){
                UserKey = rs.getInt(1);
                FirstName = rs.getString(2);
                LastName = rs.getString(3);
                return;
            }
            throw new NoSuchElementException();
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
            throw new NoSuchElementException();
        }
    }
    
    /**
     * Helper method to connect to the database
     * @return
     * @throws SQLException 
     */
    private Connection connect() throws SQLException{
        // SQLite connection string
        String uri = "jdbc:sqlite:resources/ATM.db";
        try{
            return DriverManager.getConnection(uri);
        }catch(SQLException s){
            throw new MissingDatabaseException();
        }
        
    }

    /**
     * Welcome the user
     * @return Welcome, FirstName LastName!
     */
    @Override
    public String getGreeting(){
        return String.format("Welcome, %s %s!", FirstName, LastName);
    }
    
    /**
     * Set the contents of the ATM to zero
     * @return Status of if the ATM was successfully able to be emptied
     */
    @Override
    public boolean emptyATM() {
        try(Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO CashReserve(UniversalEpochTime,TotalCash) VALUES(?,?)")){
            
            //Clear Cash
            ps.setLong(1, UnitConversion.GetEpoch());
            ps.setInt(2, 0);
            ps.executeUpdate();
            return true;
            
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return false;
    }
    
    /**
     * Restock the ATM with $10000
     * @return status of restock
     */
    @Override
    public boolean restrockATM() {
        try(Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO CashReserve(UniversalEpochTime,TotalCash) VALUES(?,?)")){
            
            //Clear Cash
            ps.setLong(1, UnitConversion.GetEpoch());
            ps.setInt(2, 0);
            ps.executeUpdate();
            
            //Add Cash
            ps.setLong(1, UnitConversion.GetEpoch()+1);
            ps.setInt(2, 10000);
            ps.executeUpdate();
            
            return true;
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return false;
    }

    /**
     * Restock the ATM with a specified amount of cash
     * @param cash
     * @return status of restock
     */
    @Override
    public boolean restockATM(int cash) {
        try(Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO CashReserve(UniversalEpochTime,TotalCash) VALUES(?,?)")){
            
            //Clear Cash
            ps.setLong(1, UnitConversion.GetEpoch());
            ps.setInt(2, 0);
            ps.executeUpdate();
            
            //Add Cash
            ps.setLong(1, UnitConversion.GetEpoch()+1);
            ps.setInt(2, cash);
            ps.executeUpdate();
        } catch (SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Take money out of an account and deduct current ATM balance
     * @param cash
     * @return status of withdraw process
     * @throws NoCashInTheAccountException 
     */
    @Override
    public boolean withdrawMoney(int cash) throws NoCashInTheAccountException, NoCashInTheATMException{
        //Check if valid withdrawl amount, replace with exception in future
        if(!(IntStream.of(WithdrawAmounts).anyMatch(x -> x == cash))) return false;
        
        //Check if user has enough money in account
        if(cash > checkBalance()) throw new NoCashInTheAccountException();
        
        //Debit the ATM of the money the user will withdraw
        if(!debitATM(cash)) return false;
        
        //Debit money from account
        try(Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement ("INSERT INTO Ledger(LoginKey, UniversalEpochTime, TransactionAmount) VALUES(?,?,?)")){
            
            ps.setInt(1, UserKey);
            ps.setLong(2, UnitConversion.GetEpoch());
            ps.setInt(3, -1*cash);
            ps.executeUpdate();
            
        } catch (SQLException e){
            System.err.println(e.getMessage());
            return false; //Shouldn't happen unless someone touches db
        }
        return true;
    }

    /**
     * Deposit money into user's account
     * @param money
     * @return Status of deposit
     */
    @Override
    public boolean depositMoney(double money) {
        //Check if amount is valid
        if(money<=0) return false;
        
        //Credit money to account
        try(Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement ("INSERT INTO Ledger(LoginKey, UniversalEpochTime, TransactionAmount) VALUES(?,?,?)")){
            
            ps.setInt(1, UserKey);
            ps.setLong(2, UnitConversion.GetEpoch());
            ps.setDouble(3, money); 
            ps.executeUpdate();
            
        } catch (SQLException e){
            System.err.println(e.getMessage());
            return false; //Shouldn't happen unless someone touches db
        }
        return true; //Please deposit check
    }

    /**
     * Checks the account balance of the user
     * @return Account balance of the user
     */
    @Override
    public double checkBalance() {
        try(Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement ("SELECT Balance FROM AccountBalance WHERE LoginKey = ?")){
            
            //Set LoginKey
            ps.setInt(1, UserKey);
            ResultSet rs  = ps.executeQuery();
            
            if(!rs.next()){
                throw new NoSuchElementException();
            }
            return rs.getDouble(1);
            
        } catch (SQLException e){
            System.err.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Checks the current cash reserve of the ATM
     * @return Current cash reserve of the ATM
     */
    @Override
    public int checkATM() {
        try(Connection conn = this.connect();
            Statement s  = conn.createStatement();
            ResultSet rs = s.executeQuery ("SELECT TotalCash FROM CashReserve ORDER BY UniversalEpochTime LIMIT 1")){
            
            if(!rs.next()){
                throw new NoSuchElementException();
            }
            return rs.getInt(1);
            
        } catch (SQLException e){
            System.err.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Deduct the cash reserve of the ATM after checking the balance
     * @param cash 
     * @return Status of deduction of ATM cash reserve
     */
    @Override
    public boolean debitATM(int cash) {
        //Cheack if there is enough cash 
        if(cash > checkATM()) return false;
        
        try(Connection conn = this.connect();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO CashReserve(UniversalEpochTime,TotalCash) VALUES(?,?)")){
            
            //Debit Cash
            ps.setLong(1, UnitConversion.GetEpoch());
            ps.setInt(2, checkATM() - cash);
            ps.executeUpdate();
        } catch (SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
}
