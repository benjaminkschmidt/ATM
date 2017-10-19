-- =============================================
-- Author: Manoj Kamalumpundi
-- Create date: 2017-10-04
-- Description: Run this file to create the necessary tables for the ATM database
-- =============================================

CREATE TABLE Login (
    LoginKey        INTEGER         NOT NULL PRIMARY KEY AUTOINCREMENT
    ,FirstName      VARCHAR(50)     NOT NULL
    ,LastName       VARCHAR(50)     NOT NULL
    ,AccountNumber  CHARACTER (16)  NOT NULL UNIQUE
    ,PersonalNumber CHARACTER (5)   NOT NULL);

CREATE TABLE CashReserve(
    CashReserveKey     INTEGER      NOT NULL PRIMARY KEY AUTOINCREMENT
    ,UniversalEpochTime BIGINT       NOT NULL --This is the number of seconds since the start of the epoch('1970-01-01')
    ,TotalCash         SMALLINT     NOT NULL
    ,UNIQUE(UniversalEpochTime,TotalCash));

CREATE TABLE Ledger(
    LedgerKey             INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT
    ,LoginKey             INTEGER   NOT NULL
    ,UniversalEpochTime   BIGINT    NOT NULL
    ,TransactionAmount    FLOAT     NOT NULL
    ,FOREIGN KEY(LoginKey) REFERENCES Login(LoginKey)
    ,UNIQUE (LoginKey, UniversalEpochTime));

--Implement logic in java, view here for demonstration
CREATE VIEW AccountBalance AS
SELECT
    n.LoginKey
    ,SUM(TransactionAmount)
FROM Login n
    LEFT JOIN Ledger r ON
        n.LoginKey = r.LoginKey
GROUP BY n.LoginKey;

--Implement logic in java, view here for demonstration
CREATE VIEW AtmBalance AS
SELECT
    cr1.UniversalEpochTime
    ,cr1.TotalCash
FROM CashReserve cr1
    LEFT JOIN CashReserve cr2
        ON cr1.CashReserveKey < cr2.CashReserveKey
WHERE cr2.CashReserveKey is null