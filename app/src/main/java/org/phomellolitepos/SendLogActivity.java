package org.phomellolitepos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class SendLogActivity extends AppCompatActivity {
    private AlertDialog alertDialog;
    private String logs;
    Settings settings;
    Database db;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("Error Report");
        setContentView(R.layout.activity_send_log);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        logs = getIntent().getStringExtra("logs");
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        settings = Settings.getSettings(getApplicationContext(), database, "");
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1 * 1000);

                    showConfirmation();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timerThread.start();


    }

    private void sendLogFile() {
        if (logs == null)
            return;


            send_email_manager();


        Intent intent = new Intent(SendLogActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showConfirmation() {
        // method as shown above
        runOnUiThread(new Runnable() {
            public void run() {
                alertDialog = new AlertDialog.Builder(SendLogActivity.this).create();
                alertDialog.setTitle("Report Error!");
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Ah, shoot. Seems like MyAPP faced an unhandled error.Would you like to report it to the developer team?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendLogFile();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                        finishAffinity();
                        System.exit(0);
                    }
                });
                alertDialog.show();
            }
        });


    }

    private void send_email_manager() {
        String dtt = Globals.Reportnamedate();
        try {
            String[] recipients = {"pegasusq8@gmail.com"};
            final SendLogActivity.SendEmailAsyncTask email = new SendLogActivity.SendEmailAsyncTask();
            email.activity = this;

            email.m = new GMailSender("pegasusq8@gmail.com","@Purbia@99534388","smtp.gmail.com","465");
            email.m.set_from("pegasusq8@gmail.com");
            email.m.setBody("Log file attached." + logs);
            email.m.set_to(recipients);
            email.m.set_subject("Error reported from Phomello-LitePOS");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        SendLogActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email failed to send.");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
//        showConfirmation();
    }
}
