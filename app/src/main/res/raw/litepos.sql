CREATE TABLE [pro_loyalty_setup](
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [min_purchase_value] DECIMAL(18, 6),
    [base_value] DECIMAL(18, 6),
    [earn_point] INTEGER(11),
    [earn_value] DECIMAL(18, 6),
    [mis_redeem_value] DECIMAL(18, 6),
    [loyalty_type] NVARCHAR(150),
    [name] NVARCHAR(150),
    [valid_from] DATETIME,
    [valid_to] DATETIME);

    CREATE TABLE [order_loyalty_earn](
                  [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                  [order_code] NVARCHAR(50),
                  [customer_code] NVARCHAR(50),
                  [earn_point] INTEGER(11),
                  [earn_value] DECIMAL(18, 6),
                  [redeem_point] DECIMAL(18, 6)
                  );

    CREATE TABLE [coupon_detail](
        [id] INTEGER PRIMARY KEY AUTOINCREMENT,
        [card_id] INTEGER(11),
        [card_no] NVARCHAR(50),
        [is_used] BOOLEAN,
        CONSTRAINT [card_no_unique] UNIQUE([card_no]));

CREATE TABLE [ticket_setup](
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [menufacture_id] NVARCHAR(50),
    [tck_from] NVARCHAR(150),
    [tck_to] NVARCHAR(150),
    [price] DECIMAL(18, 6),
    [departure] DATETIME,
    [arrival] DATETIME,
    [is_inclusive_tax] BOOLEAN,
    [new_price] DECIMAL(18, 6),
    [bus_number] NVARCHAR(150),
    [is_active] BOOLEAN);

    CREATE TABLE [ticket_setup_category](
        [id] INTEGER PRIMARY KEY AUTOINCREMENT,
        [ref_id] NVARCHAR(50),
        [category_id] NVARCHAR(50));

    CREATE TABLE [ticket_setup_days](
        [id] INTEGER PRIMARY KEY AUTOINCREMENT,
        [ref_id] NVARCHAR(50),
        [days] NVARCHAR(50));


    CREATE TABLE [ticket_setup_tax](
         [id] INTEGER PRIMARY KEY AUTOINCREMENT,
         [ref_id] NVARCHAR(50),
         [tax_id] NVARCHAR(50));

CREATE TABLE [acc_customer](
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [contact_code] NVARCHAR(50),
    [amount] DECIMAL(18, 6));

CREATE TABLE [purchase](
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [contact_code] NVARCHAR(50) REFERENCES contact([contact_code]),
    [voucher_no] NVARCHAR(50),
    [ref_voucher_code] NVARCHAR(50),
    [date] DATETIME,
    [remarks] NVARCHAR(50),
    [total] DECIMAL(18, 6),
    [is_post] BOOLEAN,
    [is_cancel] BOOLEAN,
    [is_active] BOOLEAN,
    [is_push] BOOLEAN,
    [modified_by] NVARCHAR(50),
    [modified_date] DATETIME);

	CREATE TABLE [purchase_detail](
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [ref_voucher_no] NVARCHAR(50),
    [s_no] INTEGER(11),
    [item_code] Varchar(50) REFERENCES item([item_code]),
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
    [item_code] Varchar(50) REFERENCES item([item_code]),
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
    [item_code] Varchar(50) REFERENCES item([item_code]),
    [qty] Decimal(18, 6),
    [in_out_flag] Varchar(1));

    CREATE TABLE [customer_price_book](
                           [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                           [contact_code] NVARCHAR(50),
                           [item_code] NVARCHAR(50),
                           [sale_price] DECIMAL(18, 6),
                           [modified_date] [DATETIME]);

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
                    [modified_date] [DATETIME]);

    CREATE TABLE [Sys_Tax_Group](
                   [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                   [tax_master_id] INTEGER(11),
                   [tax_id] INTEGER(11));

      CREATE TABLE [Sys_Support](
                  [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                  [name] NVARCHAR(50),
                  [vedio_url] NVARCHAR(150));

      CREATE TABLE [Sys_Sycntime](
                 [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                 [table_name] NVARCHAR(50),
                 [datetime] DATETIME);

      CREATE TABLE [Sys_Tax_Type](
                [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                [type] NVARCHAR(20),
                [is_active] [BOOLEAN]);

       CREATE TABLE [Tax_Detail](
                 [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                 [tax_id] INTEGER(11),
                 [tax_type_id] INTEGER(11));

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

        CREATE TABLE [Bank](
            	[bank_id] INTEGER PRIMARY KEY AUTOINCREMENT,
            	[device_code] NVARCHAR(50),
            	[bank_code] NVARCHAR(50),
            	[bank_name] NVARCHAR(50),
            	[email] 	NVARCHAR(50),
            	[mobile] NVARCHAR(50),
                [address] NVARCHAR(50),
                [bank_ref_code] NVARCHAR(50),
                [is_active][BOOLEAN],
                [modified_by] NVARCHAR(50),
                [modified_date] [DATETIME],
                [is_push] 		 [BOOLEAN]);

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

CREATE TABLE [Customer_Image](
[id] INTEGER PRIMARY KEY AUTOINCREMENT,
[image_Path] [NVARCHAR(255)]);

CREATE TABLE [Reservation](
    	[_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    	[date_time] NVARCHAR(50) ,
    	[customer_code] NVARCHAR(50) ,
    	[user_code] NVARCHAR(50) ,
    	[table_code] 	NVARCHAR(50));

CREATE TABLE [Reservation_detail](
    [_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [ref_id] NVARCHAR(50) REFERENCES Reservation([_id]),
    [item_code] NVARCHAR(50));

CREATE TABLE [address](
    	[address_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    	[device_code] NVARCHAR(50) ,
    	[address_code] NVARCHAR(50) ,
    	[address_category_code] NVARCHAR(50) ,
    	[area_id] 	 INTEGER(11) ,
    	[address] 	[TEXT] ,
    	[landmark] 	[TEXT] ,
    	[latitude] 	[DECIMAL(15,6)] ,
    	[longitude] 	[DECIMAL(15,6)] ,
    	[contact_person] 	NVARCHAR(100) ,
    	[contact] 	 NVARCHAR(15) ,
    	[is_active] 	[BOOLEAN] ,
        [modified_by] 	INTEGER(11) ,
        [modified_date] 	[DATETIME],
        [is_push] 		 [BOOLEAN]);

CREATE TABLE [address_category](
         [address_category_id] INTEGER PRIMARY KEY AUTOINCREMENT,
         [device_code] NVARCHAR(50) ,
         [address_category_code] NVARCHAR(50) ,
         [name] NVARCHAR(50) ,
         [is_active] 	[BOOLEAN] ,
         [modified_by] 	INTEGER(11) ,
         [modified_date] 	[DATETIME] ,
         [is_push] 		 [BOOLEAN]);

CREATE TABLE [address_lookup](
         [address_lookup_id] INTEGER PRIMARY KEY AUTOINCREMENT,
         [device_code] NVARCHAR(50),
         [address_code] NVARCHAR(50),
         [refrence_type] INTEGER(11),
         [refrence_code] 	NVARCHAR(50),
         [is_push] 		 [BOOLEAN]);

CREATE TABLE [address_type](
         [address_type] INTEGER PRIMARY KEY AUTOINCREMENT,
         [name] NVARCHAR(50) );

CREATE TABLE [business_group](
    [business_group_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [device_code] NVARCHAR(50),
    [business_group_code] NVARCHAR(50),
    [parent_code] NVARCHAR(50),
    [name] NVARCHAR(50),
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN,
    CONSTRAINT [business_group_code_unique] UNIQUE([business_group_code]));

CREATE TABLE [contact](
    [contact_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [device_code] NVARCHAR(50),
    [contact_code] NVARCHAR(50),
    [title] NVARCHAR(10),
    [name] NVARCHAR(100),
    [gender] NVARCHAR(50),
    [dob] NVARCHAR(50),
    [company_name] NVARCHAR(50),
    [description] TEXT,
    [contact_1] NVARCHAR(15),
    [contact_2] NVARCHAR(15),
    [email_1] NVARCHAR(50),
    [email_2] NVARCHAR(50),
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [is_push] BOOLEAN,
    [address] TEXT,
    [modified_date] DATETIME,
    [credit_limit] DECIMAL(15, 6),
    [gstin] VARCHAR(25),
    [country_id] INT,
    [zone_id] INT,
    CONSTRAINT [contact_code_unique] UNIQUE([contact_code]));

CREATE TABLE [contact_business_group](
    [contact_code] NVARCHAR(50),
    [business_group_code] NVARCHAR(50));

CREATE TABLE [country](
    [country_id] INTEGER(11) PRIMARY KEY,
    [name] NVARCHAR(128),
    [isd_code] int(5),
    [iso_code_2] NVARCHAR(2),
    [iso_code_3] NVARCHAR(3),
    [currency_symbol] TEXT,
    [currency_place] TEXT,
    [decimal_place] INTEGER(11),
    [postcode_required] BOOLEAN,
    [status] BOOLEAN);

CREATE TABLE [item](
    [item_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [device_code] NVARCHAR(50),
    [item_code] NVARCHAR(50),
    [parent_code] NVARCHAR(50),
    [item_group_code] NVARCHAR(50),
    [manufacture_code] NVARCHAR(50),
    [item_name] NVARCHAR(50),
    [description] TEXT,
    [sku] NVARCHAR(50),
    [barcode] NVARCHAR(50),
    [hsn_sac_code] VARCHAR(25),
    [image] TEXT,
    [item_type] NVARCHAR(50),
    [unit_id] INTEGER,
    [is_return_stockable] BOOLEAN,
    [is_service] BOOLEAN,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN,
    [is_inclusive_tax] BOOLEAN,
    [item_image] NVARCHAR(50),
    CONSTRAINT [item_code_unique] UNIQUE([item_code]));

CREATE TABLE [item_group](
    [item_group_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [device_code] NVARCHAR(50),
    [item_group_code] NVARCHAR(50),
    [parent_code] NVARCHAR(50),
    [item_group_name] NVARCHAR(50),
    [image] TEXT,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN,
    CONSTRAINT [item_group_code_unique] UNIQUE([item_group_code]));

CREATE TABLE [item_group_tax](
    [location_id] INTEGER(11),
    [tax_id] INTEGER(11),
    [item_group_code] NVARCHAR(50),
    FOREIGN KEY(tax_id) REFERENCES tax(tax_id));

CREATE TABLE [item_location](
    [item_location_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [location_id] INTEGER(11),
    [item_code] NVARCHAR(50),
    [cost_price] [DECIMAL(15,3)],
    [markup] decimal(18, 2),
    [selling_price] [DECIMAL(15,3)],
    [quantity] [DECIMAL(18,3)],
    [loyalty_point] INTEGER(11),
    [reorder_point] [DECIMAL(15,6)],
    [reorder_amount] [DECIMAL(15,6)],
    [is_inventory_tracking] BOOLEAN,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [new_sell_price] [DECIMAL(15,3)]);

CREATE TABLE [item_supplier](
    [item_supplier_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [item_code] NVARCHAR(50),
    [contact_code] NVARCHAR(50));

CREATE TABLE [last_code](
               [id] INTEGER PRIMARY KEY AUTOINCREMENT,
               [last_order_code] NVARCHAR(50),
               [last_pos_balance_code] NVARCHAR(50),
               [last_z_close_code] NVARCHAR(50),
               [last_order_return_code] NVARCHAR(50));

CREATE TABLE [Lite_POS_Device](
    	[Id] INTEGER PRIMARY KEY AUTOINCREMENT,
    	[Device_Id] [TEXT],
    	[App_Type] [TEXT],
    	[Device_Code] [TEXT],
    	[Device_Name] [TEXT],
    	[Expiry_Date] [TEXT],
    	[Device_Symbol] [TEXT],
        [Location_Code] [TEXT],
        [Currency_Symbol] [TEXT],
        [Decimal_Place] [TEXT],
        [Currency_Place] [TEXT],
        [lic_customer_license_id] [TEXT],
        [lic_code] [TEXT],
        [license_key] [TEXT],
        [license_type] [TEXT],
        [Status] [TEXT]
    	);

 CREATE TABLE [Lite_POS_Registration](
	 [Id] INTEGER PRIMARY KEY AUTOINCREMENT,
	 [Company_Name] [TEXT],
	 [Contact_Person] [TEXT],
	 [Mobile_No] [TEXT],
	 [Country_Id] [TEXT],
	 [Zone_Id] 	 [TEXT],
	 [Password] 	[TEXT],
	 [License_No] 	[TEXT],
	 [Email] 	[TEXT],
	 [Address] 	[TEXT],
	 [Company_Id] 	[TEXT],
	 [Project_Id] 	 [TEXT],
	 [Registration_Code] 	 [TEXT],
	 [service_code_tariff] 	 NVARCHAR(25),
	 [Industry_Type] 	 [TEXT]);

 CREATE TABLE [location](
        	[location_id] INTEGER PRIMARY KEY AUTOINCREMENT,
        	[location_type_id] INTEGER(11) ,
            [location_name] NVARCHAR(100) ,
            [parent_id] INTEGER(11) ,
            [contact_1] NVARCHAR(15) ,
            [contact_2] NVARCHAR(15) ,
            [email_1] NVARCHAR(50) ,
            [email_2] NVARCHAR(50) ,
            [comment] [TEXT] ,
            [is_active] BOOLEAN ,
            [modified_by] INTEGER(11) ,
            [modified_date] [DATETIME],
            [is_push] 		 [BOOLEAN]);

CREATE TABLE [location_type](
        	[location_type_id] INTEGER PRIMARY KEY AUTOINCREMENT,
        	[name] NVARCHAR(50) ,
            [is_active] BOOLEAN ,
            [modified_by] INTEGER(11) ,
            [modified_date] [DATETIME],
            [is_push] 		 [BOOLEAN]);

CREATE TABLE [manufacture](
    [manufacture_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [device_code] NVARCHAR(50),
    [manufacture_code] NVARCHAR(50),
    [manufacture_name] NVARCHAR(50),
    [image] TEXT,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN,
    CONSTRAINT [manufacture_code_unique] UNIQUE([manufacture_code]));

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
    [item_code] NVARCHAR(50) REFERENCES item([item_code]),
    [sr_no] INTEGER(11),
    [cost_price] [DECIMAL(15,3)],
    [sale_price] [DECIMAL(15,3)],
    [tax] decimal(15, 2),
    [quantity] [DECIMAL(18,3)],
    [return_quantity] INTEGER(11),
    [discount] [DECIMAL(18,3)],
    [line_total] [DECIMAL(15,3)],
    [is_combo] BOOLEAN);

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

CREATE TABLE [order_tax](
    [order_tax_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [order_code] NVARCHAR(50) REFERENCES orders([order_code]),
    [tax_id] INTEGER(11),
    [tax_type] NVARCHAR(50),
    [rate] [DECIMAL(15,2)],
    [tax_value] [DECIMAL(15,3)],
    UNIQUE([order_code], [tax_id]));

CREATE TABLE [order_type](
    [order_type_id] INTEGER(11) PRIMARY KEY,
    [name] NVARCHAR(50),
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN);

CREATE TABLE [order_type_tax](
    [location_id] INTEGER(11),
    [tax_id] INTEGER(11),
    [order_type_id] INTEGER(11));

CREATE TABLE [payments](
    [payment_id] INTEGER(11) PRIMARY KEY,
    [parent_id] INTEGER(11),
    [payment_name] NVARCHAR(50),
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN);

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

CREATE TABLE [Settings](
                [_Id] INTEGER PRIMARY KEY AUTOINCREMENT,
                [IsOnline] [BOOLEAN] NULL,
                [printerId] TEXT,
                [printerIp] TEXT,
                [Scale] TEXT,
                [Email] NVARCHAR(100),
                [Password] NVARCHAR(50),
                [Logo] BLOB,
                [Manager_Email] NVARCHAR(100),
                [Is_sms] 	[BOOLEAN],
                [Is_email] 	[BOOLEAN],
                [URL] TEXT,
                [Auth_Key] NVARCHAR(100),
                [Sender_Id] NVARCHAR(100),
                [Print_Lang] NVARCHAR(20),
                [Is_Customer_Display] [BOOLEAN],
                [HSN_print] NVARCHAR(20),
                [ItemTax] NVARCHAR(20),
                [Copy_Right] NVARCHAR(100),
                [Qty_Decimal] NVARCHAR(20),
                [Footer_Text] TEXT,
                [CustomerDisplay] NVARCHAR(20),
                [Is_Denomination] BOOLEAN,
                [Is_BarcodePrint] BOOLEAN,
                [Is_Discount] BOOLEAN,
                [Gst_No] NVARCHAR(20),
                [Print_Order] NVARCHAR(20),
                [Print_Cashier] NVARCHAR(20),
                [Print_InvNo] NVARCHAR(20),
                [Print_InvDate] NVARCHAR(20),
                [Print_DeviceID] NVARCHAR(20),
                [Is_KOT_Print] BOOLEAN,
                [Is_Print_Invoice] BOOLEAN,
                [Is_File_Share] BOOLEAN,
                [Host] NVARCHAR(50),
                [Port] NVARCHAR(50),
                [Change_Parameter] NVARCHAR(50),
                [Is_Accounts] BOOLEAN,
                [Is_Stock_Manager] BOOLEAN,
                [Home_Layout] NVARCHAR(50),
                [Is_Cash_Drawer] BOOLEAN,
                [Is_Zero_Stock] BOOLEAN,
                [Is_Change_Price] BOOLEAN,
                [No_Of_Print] NVARCHAR(50),
                [GST_Label] NVARCHAR(50),
                [Is_ZDetail_InPrint] BOOLEAN,
                [Is_Device_Customer_Show] BOOLEAN,
                [Is_Print_Dialog_Show] BOOLEAN,
                [Is_BR_Scanner_Show] BOOLEAN,
                [Default_Ordertype] NVARCHAR(2),
                [Print_Memo] NVARCHAR(2),
                [Is_Cost_Show] BOOLEAN,
                [QR_Type] NVARCHAR(2));

CREATE TABLE [tables](
    [table_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [table_code] NVARCHAR(50),
    [table_name] NVARCHAR(50),
    CONSTRAINT [table_code_unique] UNIQUE([table_code]));

CREATE TABLE [tax](
    [tax_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [location_id] INTEGER(11),
    [tax_name] NVARCHAR(50),
    [tax_type] NVARCHAR(50),
    [rate] decimal(15, 2),
    [comment] TEXT,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN);

CREATE TABLE [unit](
    [unit_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] NVARCHAR(50),
    [code] NVARCHAR(50),
    [description] TEXT,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN);

CREATE TABLE [user](
    [user_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [user_group_id] INTEGER(11),
    [user_code] NVARCHAR(50),
    [name] NVARCHAR(50),
    [email] NVARCHAR(100),
    [password] NVARCHAR(50),
    [max_discount] [DECIMAL(15,3)],
    [image] TEXT,
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN,
    [app_user_permission] NVARCHAR(150));

CREATE TABLE [zone](
    [zone_id] INTEGER(11) PRIMARY KEY,
    [country_id] INTEGER(11),
    [name] NVARCHAR(128),
    [code] NVARCHAR(32),
    [status] BOOLEAN);

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
    [z_code] NVARCHAR(50) REFERENCES z_close([z_code]),
    [device_code] NVARCHAR(50),
    [sr_no] INTEGER(11),
    [type] NVARCHAR(50),
    [amount] [DECIMAL(15,6)],
    [is_active] BOOLEAN,
    [modified_by] INTEGER(11),
    [modified_date] DATETIME,
    [is_push] BOOLEAN);

    INSERT into Sys_Sycntime (table_name,datetime) values ('item','1990-01-01');
    INSERT into Sys_Sycntime (table_name,datetime) values ('item_group','1990-01-01');
    INSERT into Sys_Sycntime (table_name,datetime) values ('contact','1990-01-01');
    INSERT into Sys_Sycntime (table_name,datetime) values ('business_group','1990-01-01');
    INSERT into Sys_Sycntime (table_name,datetime) values ('tax','1990-01-01');
    INSERT into Sys_Sycntime (table_name,datetime) values ('manufacture','1990-01-01');

    INSERT into Sys_Support (name,vedio_url) values ('Phomello LitePOS Manager','https://www.youtube.com/watch?v=GlAFWX3VSzQ');
    INSERT into Sys_Support (name,vedio_url) values ('Business Group Creation and Modification','https://www.youtube.com/watch?v=StBQ2n3_Lyo');
    INSERT into Sys_Support (name,vedio_url) values ('Contact creation and Modification','https://www.youtube.com/watch?v=wTSi5-lG7Og');
    INSERT into Sys_Support (name,vedio_url) values ('Tax Creation and Modification','https://www.youtube.com/watch?v=CWziXsSHPPI');
    INSERT into Sys_Support (name,vedio_url) values ('Creating an order','https://www.youtube.com/watch?v=dPkSh8lVLIQ');
    INSERT into Sys_Support (name,vedio_url) values ('Item Category Creation and Modication','https://www.youtube.com/watch?v=qlDRadfOBPQ');
    INSERT into Sys_Support (name,vedio_url) values ('Profile and License Update','https://www.youtube.com/watch?v=T7OV10m8SZI');
    INSERT into Sys_Support (name,vedio_url) values ('Receipt reprint cancel and save','https://www.youtube.com/watch?v=hGoYroSyoxc');
    INSERT into Sys_Support (name,vedio_url) values ('Reports Details','https://www.youtube.com/watch?v=LtoXWqUsVws');
    INSERT into Sys_Support (name,vedio_url) values ('Phomello LitePOS Settings','https://www.youtube.com/watch?v=i3wAQ-K7vTA');

	CREATE INDEX `IX_ACC_CUSTOMER_CREDIT` ON `Acc_Customer_Credit` (
	`contact_code`	ASC,
	`z_no`	ASC
	 );

CREATE INDEX [ix_acc_customer_debit]
ON [acc_customer_dedit](
    [z_no] ASC,
    [order_code] ASC);

CREATE INDEX [ix_z_detail]
ON [z_detail](
    [z_code] ASC);

	CREATE INDEX [ix_zone]
ON [zone](
    [country_id] ASC);

CREATE INDEX [ix_tax_detail]
ON [Tax_Detail](
    [tax_type_id] ASC,
    [tax_id] ASC);

CREATE INDEX [ix_sys_support]
ON [Sys_Support](
    [id] ASC);


	CREATE INDEX [ix_sys_synchtime]
ON [Sys_Sycntime](
    [id] ASC,
    [table_name] ASC);


CREATE UNIQUE INDEX [ux_stock_adjustmenr_header]
ON [stock_adjustment_header](
    [voucher_no] ASC);

CREATE INDEX [ix_stock_adjustment_header]
ON [stock_adjustment_header](
    [is_post] ASC,
    [is_active] ASC);


CREATE UNIQUE INDEX [uk_stock_adjustment_detail]
ON [stock_adjustment_detail](
    [ref_voucher_no] ASC,
    [s_no] ASC);

	CREATE INDEX [ix_returns]
ON [returns](
    [contact_code] ASC,
    [date] ASC,
    [z_code] ASC,
    [is_post] ASC);

CREATE UNIQUE INDEX [uk_returns]
ON [returns](
    [voucher_no] ASC);


CREATE INDEX [ix_stock_adjustment_detail]
ON [stock_adjustment_detail](
    [item_code] ASC,
    [in_out_flag] ASC);


	CREATE INDEX [ix_return_detail]
ON [return_detail](
    [item_code] ASC);

CREATE UNIQUE INDEX [uk_return_detail]
ON [return_detail](
    [ref_voucher_no] ASC,
    [s_no] ASC);

CREATE INDEX [ix_reservation]
ON [Reservation](
    [date_time] ASC,
    [customer_code] ASC,
    [user_code] ASC,
    [table_code] ASC);

CREATE UNIQUE INDEX [uk_purchase]
ON [purchase](
    [voucher_no] ASC);

CREATE INDEX [ix_purchase]
ON [purchase](
    [contact_code] ASC,
    [date] ASC,
    [is_post] ASC);

CREATE UNIQUE INDEX [uk_purcahse_detail]
ON [purchase_detail](
    [ref_voucher_no] ASC,
    [s_no] ASC);

CREATE INDEX [ix_purchase_detail]
ON [purchase_detail](
    [item_code] ASC);



CREATE UNIQUE INDEX [uk_purchase_payment]
ON [purchase_payment](
    [ref_voucher_no] ASC,
    [sr_no] ASC);

CREATE INDEX [ix_purchase_payment]
ON [purchase_payment](
    [payment_id] ASC);


CREATE INDEX [ix_order_type_tax]
ON [order_type_tax](
    [location_id] ASC,
    [order_type_id] ASC,
    [tax_id] ASC);


CREATE INDEX [ux_order_payment]
ON [order_payment](
    [order_code] ASC,
    [sr_no] ASC);


CREATE UNIQUE INDEX [uk_customer_price_book]
ON [customer_price_book](
    [contact_code] ASC,
    [item_code] ASC);

CREATE INDEX [ix_business_group]
ON [business_group](
    [is_active],
    [is_push]);

CREATE INDEX [ix_contact]
ON [contact](
    [name],
    [email_1],
    [contact_1]);

CREATE INDEX [ix_item]
ON [item](
    [item_group_code],
    [item_name],
    [sku],
    [barcode],
    [hsn_sac_code],
    [is_active],
    [is_push]);

CREATE UNIQUE INDEX [ix_item_location]
ON [item_location](
    [item_code],
    [location_id]);

CREATE INDEX [ix_orders]
ON [orders](
    [order_type_id],
    [order_date],
    [contact_code],
    [emp_code],
    [z_code],
    [is_post],
    [is_active]);

CREATE INDEX [ix_order_detail]
ON [order_detail](
    [item_code]);

CREATE INDEX [ix_order_detail_tax]
ON [order_detail_tax](
    [order_code] ASC,
    [item_code] ASC,
    [tax_id] ASC);

CREATE INDEX [ix_order_payment]
ON [order_payment](
    [device_code],
    [order_code]);

CREATE INDEX [ix_order_tax]
ON [order_tax](
    [order_code],
    [tax_id]);

CREATE INDEX [ix_pos_balance]
ON [pos_balance](
    [z_code],
    [is_active],
    [is_push]);

CREATE INDEX [ix_tax]
ON [tax](
    [location_id] ASC,
    [tax_id] ASC,
    [is_active],
    [is_push]);

CREATE INDEX [ix_unit]
ON [unit](
    [is_active]);

CREATE INDEX [ix_user_id]
ON [user](
    [user_code] ASC,
    [name] ASC,
    [password],
    [is_active],
    [is_push]);

CREATE INDEX [IX_z_close]
ON [z_close](
    [z_no],
    [is_active],
    [is_push]);

CREATE UNIQUE INDEX [uk_business_group]
ON [business_group](
    [device_code],
    [business_group_code]);

CREATE UNIQUE INDEX [uk_contact]
ON [contact](
    [device_code],
    [contact_code]);

CREATE UNIQUE INDEX [uk_contact_business_group]
ON [contact_business_group](
    [contact_code],
    [business_group_code]);

CREATE UNIQUE INDEX [uk_item]
ON [item](
    [device_code],
    [item_code]);

CREATE UNIQUE INDEX [uk_item_group]
ON [item_group](
    [device_code],
    [item_group_code]);

CREATE UNIQUE INDEX [uk_item_group_tax]
ON [item_group_tax](
    [location_id],
    [tax_id],
    [item_group_code]);

CREATE UNIQUE INDEX [uk_item_supplier]
ON [item_supplier](
    [item_code],
    [contact_code]);

CREATE UNIQUE INDEX [uk_manufacture]
ON [manufacture](
    [device_code],
    [manufacture_code]);

CREATE UNIQUE INDEX [uk_orders]
ON [orders](
    [device_code],
    [order_code]);

CREATE UNIQUE INDEX [uk_order_detail]
ON [order_detail](
    [device_code],
    [order_code],
    [sr_no]);

CREATE UNIQUE INDEX [uk_order_type_tax]
ON [order_type_tax](
    [location_id],
    [tax_id],
    [order_type_id]);

CREATE UNIQUE INDEX [uk_pos_balance]
ON [pos_balance](
    [pos_balance_code],
    [device_code],
    [type]);

CREATE UNIQUE INDEX [uk_tables]
ON [tables](
    [table_code]);

CREATE UNIQUE INDEX [uk_unit]
ON [unit](
    [code]);

CREATE UNIQUE INDEX [uk_user_id]
ON [user](
    [user_code]);

CREATE INDEX [uk_zone]
ON [zone](
    [country_id],
    [code],
    [status]);

CREATE UNIQUE INDEX [UK_Z_Close]
ON [z_close](
    [z_code],
    [device_code]);

CREATE UNIQUE INDEX [UK_z_detail]
ON [z_detail](
    [z_code],
    [device_code],
    [sr_no],
    [type]);

