package in.mcph.stepcounter.stepcounter;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;


public class DashBoard extends AppCompatActivity implements SensorEventListener,StepListener {

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    private int target;
    private SharedPreferences sp;
    static final String DAY_STEP_RECORD = "dayStepRecord";
    private String today;
    private boolean state = false; // registered with sensor or not
    private Toolbar toolbar;
    private Button mbtn;
    Window window;
    boolean homeflag = true;
    FragmentNow fnow;
    FragmentHistory fhist;
    public static float stride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        mbtn = findViewById(R.id.mainbtn);

        sp = getSharedPreferences("StepInfo",MODE_PRIVATE);
        SharedPreferences sp0 = PreferenceManager.getDefaultSharedPreferences(this);
        target = sp0.getInt("dayStepRecord",10000)*1000;
        fnow = new FragmentNow();
        fhist = new FragmentHistory();
        loadFragment(fnow);
        changeStatusBarColor(R.color.col_active);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                homeflag = true;
                //fnow = new FragmentNow();
                loadFragment(fnow);
                FragmentNow.onStepCountUpdate(numSteps,target);
                break;
            case R.id.history:
                homeflag = false;
                loadFragment(fhist);
                FragmentHistory.setSharedPreferences(sp);
                break;
            case R.id.prefs:
                homeflag = false;
                loadFragment(new SettingsFragment());
                break;
            case R.id.edtprf:
                homeflag = false;
                Intent i = new Intent(this,SignUp.class);
                startActivity(i);
                finish();
                break;
            case R.id.exit:
                homeflag = false;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Do you want to exit?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
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
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onStart() {
        super.onStart();
        sensorManager.registerListener(DashBoard.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        state = true;

        // if data exists in shared prefs, add to numSteps, else initialise it to 0

        today = getFormattedDate();
        if(sp.contains(today+"_steps")){
            //
            numSteps = sp.getInt(today+"_steps",0);
            SharedPreferences sp2 = getSharedPreferences("usrinfo",MODE_PRIVATE);
            try{
                stride = Float.parseFloat(sp2.getString("Stride","67.5"));
            }
            catch(Exception e){
                stride = 67.5f;
            }
            FragmentNow.onStepCountUpdate(numSteps,target);
        }
        else{
            // write to sharedpreferences and initialize to default values

            SharedPreferences.Editor et = sp.edit();
            et.putInt(today+"_steps",0);
            et.putInt(today+"_target",target);
            numSteps = 0;
            et.apply();
        }
    }

    public void onStop(){
        super.onStop();
        sensorManager.unregisterListener(DashBoard.this);
        state = false;
    }

    public void onPause(){
      super.onPause();
        sp.edit().putInt(today+"_steps",numSteps).putInt(today+"_target",target).apply();
        sensorManager.unregisterListener(DashBoard.this);
        state = false;
    }

    public void onDestroy(){
        super.onDestroy();

        // write the data to sharedpreferences
        sp.edit().putInt(today+"_steps",numSteps).putInt(today+"_target",target).apply();
    }

    private void loadFragment(android.app.Fragment fragment) {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }


    private String getFormattedDate(){
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);
        return date;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void toggle(View v){
         // unregister if already listening and change icon
        if(state){
            sensorManager.unregisterListener(DashBoard.this);
            mbtn.setBackgroundResource(R.drawable.play);
            toolbar.setBackgroundResource(R.color.col_idle);
            changeStatusBarColor(R.color.col_idle);
            Toast.makeText(this, "Pausing ", Toast.LENGTH_SHORT).show();
        }
        else{
            sensorManager.registerListener(DashBoard.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            mbtn.setBackgroundResource(R.drawable.pause);
            toolbar.setBackgroundResource(R.color.col_active);
            changeStatusBarColor(R.color.col_active);
            Toast.makeText(this, "Resuming ", Toast.LENGTH_SHORT).show();
        }
        state = !state;
    }

    private void changeStatusBarColor(int colorId){
        {
            // status bar color
            window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(getResources().getColor(colorId));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    public void step(long timeNs) {
        numSteps++;
        FragmentNow.onStepCountUpdate(numSteps,target);
        if(homeflag)
            FragmentNow.onStepCountUpdate(fnow);
    }


  // inner class for preferences
    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), DAY_STEP_RECORD);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference onLostPref = findPreference(key);
            String summary = String.format("Select the number of steps as your record for today. "
                            + "Current is: %d %s. The minimum recommended is 3000 steps per day."
                    , sharedPreferences.getInt(key, 3) * 1000, "steps");
            onLostPref.setSummary(summary);
        }
    }

}
