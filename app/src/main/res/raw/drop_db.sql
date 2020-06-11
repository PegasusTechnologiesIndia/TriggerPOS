    DROP TABLE IF EXISTS [orders];
    DROP TABLE IF EXISTS [order_detail];
    DROP TABLE IF EXISTS [order_payment];
    DROP TABLE IF EXISTS [order_detail_tax];
    DROP TABLE IF EXISTS [order_tax];
    DROP TABLE IF EXISTS [Pay_Collection_Detail];
    DROP TABLE IF EXISTS [Pay_Collection_Setup];
    DROP TABLE IF EXISTS [Pay_Collection];
    DROP TABLE IF EXISTS [z_close];
    DROP TABLE IF EXISTS [z_detail];
    DROP TABLE IF EXISTS [pos_balance];
        DROP TABLE IF EXISTS [stock_adjustment_header];
        DROP TABLE IF EXISTS [stock_adjustment_detail];
        DROP TABLE IF EXISTS [returns];
        DROP TABLE IF EXISTS [return_detail];
        DROP TABLE IF EXISTS [purchase];
        DROP TABLE IF EXISTS [purchase_detail];
        DROP TABLE IF EXISTS [purchase_payment];
        DROP TABLE IF EXISTS [Acc_Customer_Credit];
        DROP TABLE IF EXISTS [acc_customer_dedit];
        DROP TABLE IF EXISTS [acc_customer];

CREATE TABLE [acc_customer](
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [contact_code] NVARCHAR(50),
    [amount] DECIMAL(18, 6));

CREATE TABLE [acc_customer_dedit](
                       [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                       [order_code] NVARCHAR(50),
                       [amount] DECIMAL(18, 6),
                       [z_no] NVARCHAR(50),
                       [ref_type] NVARCHAR(2));

    CREATE TABLE [Acc_Customer_Credit](
                    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                    [trans_date] [DATETIME],
                    [contact_code] NVARCHAR(50),
                    [cr_amount] DECIMAL(18, 6),
                    [paid_amount] DECIMAL(18, 6),
                    [balance_amount] DECIMAL(18, 6),
                    [z_no] NVARCHAR(50),
                    [is_active] [BOOLEAN],
                    [modified_by] NVARCHAR(50),
                    [modified_date] [DATETIME],
                    [voucher_no] NVARCHAR(50));

CREATE TABLE [purchase](
                  [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                  [contact_code] NVARCHAR(50),
                                                    [voucher_no] NVARCHAR(50),
                                                    [ref_voucher_code] NVARCHAR(50),
                                                    [date] [DATETIME],
                                                    [remarks] NVARCHAR(50),
                                                    [total] DECIMAL(18, 6),
                                                    [is_post] [BOOLEAN],
                                                    [is_cancel] [BOOLEAN],
                                                    [is_active] [BOOLEAN],
                                                    [is_push] [BOOLEAN],
                                                    [modified_by] NVARCHAR(50),
                                                    [modified_date] [DATETIME]);

                             CREATE TABLE [purchase_detail](
                                                    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                                                    [ref_voucher_no] NVARCHAR(50),
                                                    [s_no] INTEGER(11),
                                                    [item_code] Varchar(50),
                                                    [qty] Decimal(18, 6),
                                                    [price] Decimal(18, 6),
                                                    [line_total] Decimal(18, 6));

CREATE TABLE [purchase_payment](
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [device_code] NVARCHAR(50),
    [ref_voucher_no] NVARCHAR(50),
    [sr_no] INTEGER(11),
    [pay_amount] [DECIMAL(15,3)],
    [payment_id] INTEGER(11),
    [currency_id] INTEGER(11),
    [currency_value] [DECIMAL(15,6)],
    [card_number] NVARCHAR(50),
    [card_name] NVARCHAR(50),
    [field1] TEXT,
    [field2] TEXT);

              CREATE TABLE [returns](
                                        [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                                        [contact_code] NVARCHAR(50),
                                        [voucher_no] NVARCHAR(50),
                                        [date] [DATETIME],
                                        [remarks] NVARCHAR(50),
                                        [total] DECIMAL(18, 6),
                                        [z_code] NVARCHAR(50),
                                        [is_post] [BOOLEAN],
                                        [is_cancel] [BOOLEAN],
                                        [is_active] [BOOLEAN],
                                        [is_push] [BOOLEAN],
                                        [modified_by] NVARCHAR(50),
                                        [modified_date] [DATETIME],
                                        [order_code] NVARCHAR(50),
                                        [return_type] NVARCHAR(2),
                                        [payment_id] NVARCHAR(2));

                CREATE TABLE [return_detail](
                                       [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                                       [ref_voucher_no] NVARCHAR(50),
                                       [s_no] INTEGER(11),
                                       [item_code] Varchar(50),
                                       [qty] Decimal(18, 6),
                                       [price] Decimal(18, 6),
                                       [line_total] Decimal(18, 6));

        CREATE TABLE [stock_adjustment_header](
                               [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                               [voucher_no] NVARCHAR(50),
                               [date] [DATETIME],
                               [remarks] NVARCHAR(50),
                               [is_post] [BOOLEAN],
                               [is_cancel] [BOOLEAN],
                               [is_active] [BOOLEAN],
                               [modified_by] NVARCHAR(50),
                               [modified_date] [DATETIME]);

        CREATE TABLE [stock_adjustment_detail](
                               [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                               [ref_voucher_no] NVARCHAR(50),
                               [s_no] INTEGER(11),
                               [item_code] Varchar(50),
                               [qty] Decimal(18, 6),
                               [in_out_flag] Varchar(1));

CREATE TABLE [pos_balance](
    [pos_balance_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [pos_balance_code] NVARCHAR(50),
    [device_code] NVARCHAR(50),
    [type] NVARCHAR(50),
    [date] DATETIME,
    [amount] [DECIMAL(15,6)],
    [remarks] TEXT,
    [z_code] NVARCHAR(50),
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN);


CREATE TABLE [orders](
    [order_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [device_code] NVARCHAR(50),
    [location_id] INTEGER(11),
    [order_type_id] INTEGER(11),
    [order_code] NVARCHAR(50),
    [order_date] DATETIME,
    [contact_code] NVARCHAR(50),
    [emp_code] NVARCHAR(50),
    [total_item] INTEGER(11),
    [total_quantity] [DECIMAL(15,6)],
    [sub_total] [DECIMAL(15,3)],
    [total_tax] decimal(18, 2),
    [total_discount] decimal(18, 2),
    [total] [DECIMAL(15,3)],
    [tender] [DECIMAL(15,3)],
    [change_amount] [DECIMAL(15,3)],
    [z_code] NVARCHAR(50),
    [load_id] INTEGER(11),
    [is_post] BOOLEAN,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN,
    [order_status] NVARCHAR(50),
    [remarks] TEXT,
    [table_code] TEXT,
     [delivery_date] DATETIME,
    CONSTRAINT [item_location_unique] UNIQUE([device_code], [order_code]));

    CREATE TABLE [order_detail](
        [order_detail_id] INTEGER PRIMARY KEY AUTOINCREMENT,
        [device_code] NVARCHAR(50),
        [order_code] NVARCHAR(50),
        [reference_order_code] NVARCHAR(50),
        [item_code] NVARCHAR(50),
        [sr_no] INTEGER(11),
        [cost_price] [DECIMAL(15,3)],
        [sale_price] [DECIMAL(15,3)],
        [tax] decimal(15, 2),
        [quantity] [DECIMAL(18,3)],
        [return_quantity] INTEGER(11),
        [discount] [DECIMAL(18,3)],
        [line_total] [DECIMAL(15,3)],
        [is_combo] BOOLEAN);


    CREATE TABLE [order_payment](
        [order_payment_id] INTEGER PRIMARY KEY AUTOINCREMENT,
        [device_code] NVARCHAR(50),
        [order_code] NVARCHAR(50),
        [sr_no] INTEGER(11),
        [pay_amount] [DECIMAL(15,3)],
        [payment_id] INTEGER(11),
        [currency_id] INTEGER(11),
        [currency_value] [DECIMAL(15,6)],
        [card_number] NVARCHAR(50),
        [card_name] NVARCHAR(50),
        [field1] TEXT,
        [field2] TEXT);

   CREATE TABLE [order_detail_tax](
       [order_detail_tax_id] INTEGER PRIMARY KEY AUTOINCREMENT,
       [order_code] NVARCHAR(50),
       [sr_no] INTEGER(11),
       [item_code] NVARCHAR(50),
       [tax_id] INTEGER(11),
       [tax_type] NVARCHAR(50),
       [rate] [DECIMAL(15,2)],
       [tax_value] [DECIMAL(15,3)],
       UNIQUE([order_code], [item_code], [tax_id]));


CREATE TABLE [order_tax](
    [order_tax_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [order_code] NVARCHAR(50),
    [tax_id] INTEGER(11),
    [tax_type] NVARCHAR(50),
    [rate] [DECIMAL(15,2)],
    [tax_value] [DECIMAL(15,3)],
    UNIQUE([order_code], [tax_id]));


     CREATE TABLE [Pay_Collection_Detail](
                     [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                     [collection_code] NVARCHAR(50),
                     [invoice_no] NVARCHAR(50),
                     [amount] NVARCHAR(50));

     CREATE TABLE [Pay_Collection_Setup](
                     [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                     [contact_code] NVARCHAR(50),
                     [invoice_no] NVARCHAR(50),
                     [invoice_date] NVARCHAR(50),
                     [amount] NVARCHAR(50));



     CREATE TABLE [Pay_Collection](
                 	[id] INTEGER PRIMARY KEY AUTOINCREMENT,
                 	[contact_code] NVARCHAR(50),
                 	[collection_code] NVARCHAR(50),
                 	[collection_date] [DATETIME],
                 	[amount] [DECIMAL(15,3)],
                     [payment_mode] NVARCHAR(50),
                     [ref_no] NVARCHAR(50),
                     [ref_type] 	NVARCHAR(50),
                     [on_account] NVARCHAR(50) ,
                     [remarks] NVARCHAR(50) ,
                     [is_active] [BOOLEAN] ,
                     [modified_by] NVARCHAR(50),
                     [modified_date] [DATETIME],
                     [is_push] 		 [BOOLEAN]);

     CREATE TABLE [z_close](
         [z_no] INTEGER PRIMARY KEY AUTOINCREMENT,
         [z_code] NVARCHAR(50),
         [device_code] NVARCHAR(50),
         [date] DATETIME,
         [total_amount] [DECIMAL(15,6)],
         [is_active] BOOLEAN,
         [modified_by] INTEGER(11),
         [modified_date] DATETIME,
         [is_push] BOOLEAN);

     CREATE TABLE [z_detail](
         [z_no] INTEGER PRIMARY KEY AUTOINCREMENT,
         [z_code] NVARCHAR(50),
         [device_code] NVARCHAR(50),
         [sr_no] INTEGER(11),
         [type] NVARCHAR(50),
         [amount] [DECIMAL(15,6)],
         [is_active] BOOLEAN,
         [modified_by] INTEGER(11),
         [modified_date] DATETIME,
         [is_push] BOOLEAN);