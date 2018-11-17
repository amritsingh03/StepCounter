package in.mcph.stepcounter.stepcounter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPrefs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPrefs extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static final String DAY_STEP_RECORD = "dayStepRecord";
    TextView etpfl;
    EditText et;
    Button ok;
    Context ctx;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentPrefs() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPrefs.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPrefs newInstance(String param1, String param2) {
        FragmentPrefs fragment = new FragmentPrefs();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_prefs, container, false);
        ctx=container.getContext();
        et = v.findViewById(R.id.etdsteps);
        ok = v.findViewById(R.id.btok);
        etpfl = v.findViewById(R.id.etpfl);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        etpfl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx,SignUp.class);
                startActivity(i);
            }
        });
        return v;
      }


   private void next(){
            if(et.getText()!=null){
                try{
                    final int steps = Integer.parseInt(et.getText().toString());
                    SharedPreferences sp = ctx.getSharedPreferences("usrinfo",MODE_PRIVATE);
                    SharedPreferences.Editor et = sp.edit();
                    et.remove("DailySteps");
                    et.putInt("DailySteps",steps);
                    et.commit();
                }
                catch(NumberFormatException | NullPointerException e){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                    builder1.setMessage("Enter a valid number");
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
            }
        }
        }


