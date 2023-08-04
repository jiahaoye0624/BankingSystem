CONNECT TO CS157A@
--
--
DROP PROCEDURE P2.CUST_CRT@
DROP PROCEDURE P2.CUST_LOGIN@
DROP PROCEDURE P2.ACCT_OPN@
DROP PROCEDURE P2.ACCT_CLS@
DROP PROCEDURE P2.ACCT_DEP@
DROP PROCEDURE P2.ACCT_WTH@
DROP PROCEDURE P2.ACCT_TRX@
DROP PROCEDURE P2.ADD_INTEREST@
DROP PROCEDURE P2.ACCT_IDT@
--
--
CREATE PROCEDURE P2.CUST_CRT
(IN p_name CHAR(15), IN p_gender CHAR(1), IN p_age INTEGER, IN p_pin INTEGER, OUT id INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    IF p_gender != 'M' AND p_gender != 'F' THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid gender';
    ELSEIF p_age <= 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid age';
    ELSEIF p_pin < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid pin';
    ELSE
      INSERT INTO p2.customer (name, gender, age, pin) VALUES (p_name, p_gender, p_age, p_pin);
      SET id = (SELECT id FROM p2.customer WHERE name = p_name and gender = p_gender and age = p_age and pin = p_pin);
      SET sql_code = 0;
    END IF;
END@

CREATE PROCEDURE P2.CUST_LOGIN
(IN p_id INTEGER, IN p_pin INTEGER, OUT Valid INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    SET Valid = (SELECT COUNT(*) FROM p2.customer WHERE id = p_id and pin = p_pin);
    IF Valid != 1 THEN
      SET Valid = 0;
      SET sql_code = -100;
      SET err_msg = 'Incorrect id or pin';
    ELSE
      SET sql_code = 0;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_OPN
(IN p_id INTEGER, IN p_balance INTEGER, IN p_type CHAR(1), OUT Number INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE flag int;
    SET flag = (SELECT COUNT(*) FROM p2.customer WHERE id = p_id);
    IF flag = 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid customer id';
    ELSEIF p_balance < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid balance';
    ELSE
      INSERT INTO p2.account (ID, balance, Type, Status) VALUES (p_id, p_balance, p_type, 'A');
      SET Number = (SELECT Number FROM p2.account WHERE ID = p_id and balance = p_balance and Type = p_type);
      SET sql_code = 0;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_CLS
(IN p_number INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE flag int;
    SET flag = (SELECT COUNT(*) FROM p2.account WHERE Number = p_number and status = 'A');
    IF flag = 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid account number';
    ELSE
      UPDATE p2.account SET status = 'I', balance = 0 WHERE number = p_number;
      SET sql_code = 0;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_DEP
(IN p_number INTEGER, IN Amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE flag int;
    SET flag = (SELECT COUNT(*) FROM p2.account WHERE Number = p_number and status = 'A');
    IF flag = 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid account number';
    ELSEIF Amt < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid amount';
    ELSE
      UPDATE p2.account SET balance = balance + Amt WHERE number = p_number AND status = 'A';
      SET sql_code = 0;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_WTH
(IN p_number INTEGER, IN Amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    DECLARE flag int;
    DECLARE temp_balance int;
    SET flag = (SELECT COUNT(*) FROM p2.account WHERE Number = p_number and status = 'A');
    SET temp_balance = (SELECT balance FROM p2.account WHERE Number = p_number and status = 'A');
    IF flag = 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid account number';
    ELSEIF Amt < 0 THEN
      SET sql_code = -100;
      SET err_msg = 'Invalid amount';
    ELSEIF temp_balance < Amt THEN
      SET sql_code = -100;
      SET err_msg = 'Not enough funds';
    ELSE
      UPDATE p2.account SET balance = balance - Amt WHERE number = p_number AND status = 'A';
      SET sql_code = 0;
    END IF;
END@

CREATE PROCEDURE P2.ACCT_TRX
(IN Src_Acct INTEGER, IN Dest_Acct INTEGER, IN Amt INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    CALL p2.ACCT_WTH(Src_Acct, Amt, sql_code, err_msg);
    CALL p2.ACCT_DEP(Dest_Acct, Amt, sql_code, err_msg);
END@

CREATE PROCEDURE P2.ADD_INTEREST
(IN Saving_Rate float, IN Checking_Rate float, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    UPDATE p2.account SET balance = balance + (balance * Saving_Rate) WHERE Type = 'S' AND status = 'A';
    UPDATE p2.account SET balance = balance + (balance * Checking_Rate) WHERE Type = 'C' AND status = 'A';
    SET sql_code = 0;
END@

CREATE PROCEDURE P2.ACCT_IDT
(IN p_id INTEGER, IN p_number INTEGER, OUT flag INTEGER, OUT sql_code INTEGER, OUT err_msg CHAR(100))
LANGUAGE SQL
  BEGIN
    SET flag = (SELECT count(*) FROM p2.account WHERE id = p_id AND number = p_number);
    IF flag = 0 THEN
      SET sql_code = -100;
      SET err_msg = 'This is not your Account';
    ELSE
      SET sql_code = 0;
    END IF;
END@
--
TERMINATE@
--
--
