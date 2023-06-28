package com.example.sdeproject2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Button button_man_input;
    private Button button_scan_qr;
    private Button button_extract;
    private ListView lv_clientList;
    private TextView tv_admin, tv_guard, cancel, login;
    private EditText et_adminUser,et_adminPass;
    ArrayAdapter clientArrayAdapter;
    DataBaseHelper dataBaseHelper;
    String name,address,number,purpose;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_man_input = findViewById(R.id.btn_man_input);
        button_scan_qr = findViewById(R.id.btn_scanQr);
        button_extract = findViewById(R.id.btn_extract_rec);
        lv_clientList = findViewById(R.id.lv_clientList);
        tv_admin = findViewById(R.id.tv_admin);
        tv_guard = findViewById(R.id.tv_guard);

        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        extracted(dataBaseHelper);

        button_scan_qr.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator= new IntentIntegrator(MainActivity.this);

            //set prompt
            intentIntegrator.setPrompt("For flash, use volume up key");
            //set beep
            intentIntegrator.setBeepEnabled(true);
            //locked orientation
            intentIntegrator.setOrientationLocked(true);
            //set capture activity
            intentIntegrator.setCaptureActivity(Capture.class);
            //initiate scan
            intentIntegrator.initiateScan();


          //  Toast.makeText(MainActivity.this, "Scan QR", Toast.LENGTH_SHORT).show();
        });

        button_man_input.setOnClickListener(view -> {
            openUserPage();
            extracted(dataBaseHelper);
        });

        button_extract.setOnClickListener(view -> {

            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        //Toast.makeText(MainActivity.this, dataBaseHelper.getEveryone().toString(), Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String dateTime = simpleDateFormat.format(calendar.getTime());
            dataBaseHelper.exportDatabase(dateTime);
        Toast.makeText(MainActivity.this, "Successfully downloaded " + dateTime + ".xlsx", Toast.LENGTH_SHORT).show();


            String stringFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator;
            File file = new File(stringFile, dateTime + ".xlsx");
            if (!file.exists()){
                Toast.makeText(this, stringFile + "File doesn't exists", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
            startActivity(Intent.createChooser(intentShare, "Share the file ..."));
            //boolean success = dataBaseHelper.deleteAll();
            //Toast.makeText(MainActivity.this, "Delete all " + success , Toast.LENGTH_SHORT).show();
            extracted(dataBaseHelper);
        });

        lv_clientList.setOnItemClickListener((parent, view, position, l) -> {
            EmployeeModel clickedClient;
            clickedClient = (EmployeeModel) parent.getItemAtPosition(position);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setTitle("Delete Record");
            mBuilder.setMessage("Are you sure to delete this record?");
            mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dataBaseHelper.deleteOne(clickedClient);
                    Toast.makeText(MainActivity.this, "Successfully delete record ", Toast.LENGTH_SHORT).show();
                    extracted(dataBaseHelper);
                }
            });

            mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = mBuilder.create();
            dialog.show();

        });


        tv_guard.setOnClickListener(view -> {
            tv_guard.setTextColor(Color.parseColor("#000000"));
            tv_admin.setTextColor(Color.parseColor("#aaaaaa"));
            button_extract.setVisibility(View.INVISIBLE);
            count--;
        });


        tv_admin.setOnClickListener(view -> {
            if (count >= 1){
            }
            else {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                AlertDialog dialog;
                final View loginPopup = getLayoutInflater().inflate(R.layout.popup, null);
                et_adminUser = loginPopup.findViewById(R.id.et_adminUser);
                et_adminPass = loginPopup.findViewById(R.id.et_adminPass);
                login = loginPopup.findViewById(R.id.tv_login);
                cancel = loginPopup.findViewById(R.id.tv_cancel);

                dialogBuilder.setView(loginPopup);
                dialog = dialogBuilder.create();
                dialog.show();

                login.setOnClickListener(view1 -> {
                    String adminUser = et_adminUser.getText().toString();
                    String adminPass = et_adminPass.getText().toString();

                    if (adminUser.equalsIgnoreCase("admin") && adminPass.matches("1452")) {
                        tv_guard.setTextColor(Color.parseColor("#aaaaaa"));
                        tv_admin.setTextColor(Color.parseColor("#000000"));
                        button_extract.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        count++;
                    } else {
                        Toast.makeText(MainActivity.this, "Wrong Password or Username ", Toast.LENGTH_SHORT).show();
                    }

                });

                cancel.setOnClickListener(view1 -> {
                    dialog.dismiss();
                });
            }
        });

    }

    private void extracted(DataBaseHelper dataBaseHelper2) {
        clientArrayAdapter = new ArrayAdapter<EmployeeModel>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper2.getEveryone());
        lv_clientList.setAdapter(clientArrayAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //initialize intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode,resultCode,data
        );

        //chexk connection
        if(intentResult.getContents() != null){
            //when result is not null,
            //initialize alert dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            //set title
            builder.setTitle("Result");
            builder.setMessage(intentResult.getContents());
            //set positive button
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Dismiss dialog
                    dialogInterface.dismiss();
                    String[] split = intentResult.getContents().split(",");
                    name = split[1];
                    address = "None";
                    selectPurpose();
                }
            });

            //show alert dialog
            builder.show();
        }
        else{
            //when result is null
            //display toast
            Toast.makeText(MainActivity.this, "Oppss.. you did not scan anything", Toast.LENGTH_SHORT).show();
        }

    }

    public void selectPurpose(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        mBuilder.setTitle("Appointment");
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner);
        final EditText et_number1 = mView.findViewById(R.id.et_number1);
        final EditText et_number2 = mView.findViewById(R.id.et_number2);
        final EditText et_otherAppoint = mView.findViewById(R.id.et_otherAppo1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.selectPurpose));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Select an appointment…")){
                    purpose = mSpinner.getSelectedItem().toString();
                    if (purpose.equalsIgnoreCase("Other, please specify")){
                        purpose = et_otherAppoint.getText().toString();
                    }
                    //Toast.makeText(MainActivity.this, name + "\n" + address + "\n"+ number + "\n" + purpose, Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                    EmployeeModel employeeModel = null;
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
                    int clientId = dataBaseHelper.scanID();
                    String time = getDateTime();
                    address = et_number2.getText().toString() ;
                    number = et_number1.getText().toString();

                    if (address.equalsIgnoreCase("") || address.equalsIgnoreCase(null)){
                        address = "None";
                    }

                    try {
                        employeeModel = new EmployeeModel(clientId, name,address,
                                Long.parseLong(number) , purpose, time) ;
                    }
                    catch (NumberFormatException e){
                        Toast.makeText(MainActivity.this, "Wrong inputs, Failed to add record", Toast.LENGTH_SHORT).show();

                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, " Failed to add record", Toast.LENGTH_SHORT).show();
                    }

                    boolean success = dataBaseHelper.addOne(employeeModel);
                    Toast.makeText(MainActivity.this, "Success "  + success, Toast.LENGTH_SHORT).show();
                    extracted(dataBaseHelper);

                }

            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    public String getDateTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE , dd-MMM-yyyy hh:mm:ss a");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        return dateTime;
    }
    public void openUserPage() {
        Intent intent = new Intent( this, CreateAccount.class);
        startActivity(intent);
    }




}