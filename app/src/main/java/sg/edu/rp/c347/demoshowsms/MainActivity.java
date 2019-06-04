package sg.edu.rp.c347.demoshowsms;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    TextView tvSMS;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSMS = findViewById(R.id.tv);
        btnRetrieve = findViewById(R.id.btnRetrieve);
        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }




                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                String filter = "body LIKE ? AND body LIKE ?";
                String[] args = {"%late%", "%min"};
                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(uri, reqCols, filter, args, null);
                String smsBody = "";
                if (cursor.moveToFirst()){
                    do{
                        android.text.format.DateFormat df = new android.text.format.DateFormat();
                        long dateInMillis = cursor.getLong(0);
                        String date = (String)df.format("dd MMM yyyy h:mm:ss aa", dateInMillis);

                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")){
                            type = "Inbox:";
                        }else{
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at" + date + "\n\"" + body + "\"\n\n";
                    }while (cursor.moveToNext());
                }
                tvSMS.setText(smsBody);
            }
        });
    }
}
