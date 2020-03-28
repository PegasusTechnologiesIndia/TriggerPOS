package org.phomellolitepos.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.phomellolitepos.PaymentActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;

/**
 * Created by LENOVO on 7/13/2018.
 */

public class CallDialog extends Dialog {
    String amount;
    Context context;

    public CallDialog(Context context, String amt) {
        super(context);
        this.context = context;
        this.amount = amt;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setTitle("Payment");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_dialog);

        EditText edt_amount = (EditText) findViewById(R.id.edt_amount);
        TextView txt_param_1 = (TextView) findViewById(R.id.txt_param_1);
        TextView txt_param_2 = (TextView) findViewById(R.id.txt_param_2);
        edt_amount.setEnabled(false);
        edt_amount.setText(amount);
        txt_param_1.setText(" "+Globals.Param1);
        txt_param_2.setText(" "+Globals.Param2);
        Button btn_pay = (Button) findViewById(R.id.btn_pay);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        /*
         * 获取对话框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
		 * 对象,这样这可以以同样的方式改变这个Activity的属性.
		 */
        Window dialogWindow = getWindow();
        // dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);
        lp.width = wm.getDefaultDisplay().getWidth();
        btn_pay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                Globals.Param1="";
//                Globals.Param2="";
//                ((PaymentActivity) context).fill_spinner_pay_method("");
                ((PaymentActivity) context).parformpayment();
                dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Globals.Param1="";
                Globals.Param2="";
                ((PaymentActivity) context).fill_spinner_pay_method("");
                dismiss();
            }
        });
    }
}
