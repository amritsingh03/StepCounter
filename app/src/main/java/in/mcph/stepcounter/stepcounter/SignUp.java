package in.mcph.stepcounter.stepcounter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class SignUp extends AppCompatActivity {

    EditText name,age,weigth,height,stride;
    RadioGroup gender;
    Button next;
    boolean backMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.etname);
        age = findViewById(R.id.etage);
        weigth = findViewById(R.id.etweight);
        height = findViewById(R.id.etheight);
        stride = findViewById(R.id.etstride);
        gender = findViewById(R.id.rdgrp);
        next = findViewById(R.id.btnext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });
        gender.check(R.id.rdgendm);

        // if data already enterred, back button will redirect to home; else it will prompt for exit
        SharedPreferences sp  = getSharedPreferences("usrinfo",MODE_PRIVATE);
        backMode = sp.contains("isDataEnterred");
        if(backMode){
             // populate the fields

            name.setText(sp.getString("Name",""));
            age.setText(sp.getString("Age",""));
            weigth.setText(sp.getString("Weight",""));
            height.setText(sp.getString("Height",""));
            stride.setText(sp.getString("Stride",""));
            gender = findViewById(R.id.rdgrp);
            if(sp.getString("Gender","").equalsIgnoreCase("Male"))
                gender.check(R.id.rdgendm);
            else
                gender.check(R.id.rdgendf);
            }

    }

    public void onBackPressed(){
        if(backMode){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Returning to previous screen will delete all changes. Continue?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(SignUp.this,DashBoard.class);
                            startActivity(i);
                            finish();
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        else{
            // show alert
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Do you want to exit?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    });
            builder1.setNegativeButton(
                    "NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }


    private void proceed() {

        // get all details
        String str_name = name.getText().toString();
        String str_age = age.getText().toString();
        String str_weight = weigth.getText().toString();
        String str_height = height.getText().toString();
        String str_stride = stride.getText().toString();
        String str_gender;

        if (gender.getCheckedRadioButtonId() == R.id.rdgendm)
            str_gender = "Male";
        else str_gender = "Female";

        // check each detail

        String status = validate(str_name, str_age, str_weight, str_height, str_stride);
        if (!status.equalsIgnoreCase("OK")) {

            // show alert
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(status);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        else{

            // store data to sharedpreferences and proceed

            SharedPreferences sp = getSharedPreferences("usrinfo",MODE_PRIVATE);
            SharedPreferences.Editor et = sp.edit();
            if(sp.contains("isDataEnterred") && sp.getString("isDataEntered","false").equalsIgnoreCase("true"))
            {
                // remove existing data
                et.remove("Name");
                et.remove("Age");
                et.remove("Weight");
                et.remove("Height");
                et.remove("Gender");
                et.remove("Stride");
                et.remove("isDataEntered");
            }
            et.putString("Name",str_name);
            et.putString("Age",str_age);
            et.putString("Weight",str_weight);
            et.putString("Height",str_height);
            et.putString("Gender",str_gender);
            et.putString("Stride",str_stride);
            et.putString("isDataEnterred","true");
            et.putInt("DailySteps",10000);   // default value
            et.apply();

            Intent i = new Intent(this,DashBoard.class);
            startActivity(i);
            finish();

        }
    }

    private String validate(String str_name, String str_age, String str_weight, String str_height, String str_stride) {

        String status = "OK";

        if(str_name == null || str_name.equalsIgnoreCase("")){
            status = "Name not entered";
            return status;
        }

        if(str_age == null || str_age.equalsIgnoreCase("")){
            status = "Age not entered";
            return status;
        }

        if(str_weight == null || str_weight.equalsIgnoreCase("")){
            status = "Weight not entered";
            return status;
        }

        if(str_height == null || str_height.equalsIgnoreCase("")){
            status = "Height not entered";
            return status;
        }

        if(str_stride == null || str_stride.equalsIgnoreCase("")){
            status = "Stride not entered";
            return status;
        }

        return status;
    }
}
