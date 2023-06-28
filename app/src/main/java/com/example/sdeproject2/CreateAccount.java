package com.example.sdeproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateAccount extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int clientId;
    EditText fullname, address, number,otherAppointment;
    Button button_cancel, button_add;
    Spinner spinner;
    String addressOp, purpose1,name1,num1, purposeSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        fullname = findViewById(R.id.et_fullname);
        address = findViewById(R.id.et_address);
        number = findViewById(R.id.et_number);
        spinner = findViewById(R.id.spinner2);
        button_add = findViewById(R.id.btn_addRecord);
        button_cancel = findViewById(R.id.btn_cancel);
        otherAppointment = findViewById(R.id.et_otherAppo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource (this,
                R.array.selectPurpose, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View view) {
                EmployeeModel employeeModel = null;
                DataBaseHelper dataBaseHelper = new DataBaseHelper(CreateAccount.this);
                clientId = dataBaseHelper.scanID();
                String time = getDateTime();
                try {
                    addressOp = address.getText().toString();
                    name1 = fullname.getText().toString();
                    num1 = number.getText().toString();
                }
                catch (NumberFormatException e){
                    Toast.makeText(CreateAccount.this, "Wrong inputs, Failed to add record", Toast.LENGTH_SHORT).show();

                }
                catch (NullPointerException e){
                    Toast.makeText(CreateAccount.this, "Wrong inputs, Failed to add record", Toast.LENGTH_SHORT).show();
                }

                if (addressOp.equalsIgnoreCase("") || addressOp.equalsIgnoreCase(null)){
                    addressOp = "None";
                }
                    try {
                        employeeModel = new EmployeeModel(clientId, name1,addressOp,
                                Long.parseLong(num1) , purpose1, time) ;
                        openLoginPage();
                    }
                    catch (Exception e){
                        Toast.makeText(CreateAccount.this, "Error Creating Account", Toast.LENGTH_SHORT).show();
                    }
                    dataBaseHelper.addOne(employeeModel);
                    Toast.makeText(CreateAccount.this, "Successly add record " , Toast.LENGTH_SHORT).show();


            } 
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginPage();
            }
        });
    }

    public void openLoginPage() {
        Intent intent = new Intent( this, MainActivity.class);
        startActivity(intent);
    }

    public String getDateTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE , dd-MMM-yyyy hh:mm:ss a");
        String dateTime = simpleDateFormat.format(calendar.getTime());
        return dateTime;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        purpose1 = adapterView.getSelectedItem().toString();
        if (purpose1.equalsIgnoreCase("Other, please specify")){
            otherAppointment.setVisibility(View.VISIBLE);
            purpose1 = otherAppointment.getText().toString();
        }
        else{
            otherAppointment.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}