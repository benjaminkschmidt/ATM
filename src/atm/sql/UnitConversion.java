/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.sql;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author keepotalize
 */
public class UnitConversion{
    
    //Prevent Instances
    private UnitConversion(){};
    
    //Return datetime in UTC
    public static LocalDateTime ConvertEpoch(long epoch){
        return LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
    }
    
    //Return datetime in custom zone, use OffsetDateTime.now().getOffset() for local
    public static LocalDateTime ConvertEpoch(long epoch, ZoneOffset zone){
        return LocalDateTime.ofEpochSecond(epoch, 0, zone);
    }
    
    //Return current epoch in UTC
    public static long GetEpoch(){
        return System.currentTimeMillis() / 1000L;
    }
}
