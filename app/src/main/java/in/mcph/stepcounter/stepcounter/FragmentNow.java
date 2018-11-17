package in.mcph.stepcounter.stepcounter;



import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import static in.mcph.stepcounter.stepcounter.DashBoard.stride;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentNow#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNow extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static TextView tv,tv2,tv3;
    CircularProgressBar cv;
    static int psteps = 0,ptarget = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentNow() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentNow.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentNow newInstance(String param1, String param2) {
        FragmentNow fragment = new FragmentNow();
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
        View v = inflater.inflate(R.layout.fragment_fragment_now, container, false);
        tv = v.findViewById(R.id.tv1);
        tv2 = v.findViewById(R.id.tv2);
        tv3 = v.findViewById(R.id.tv3);
        cv = v.findViewById(R.id.circleview);

        return v;
    }



     private void updateCV(){
        int left;
        float prgss = psteps/((float)ptarget);
        System.out.println(prgss);
        if(psteps>=ptarget){
            // send notification
            //dont change color
            left = 0;
            prgss = 1;
            cv.setColor(Color.rgb(0,255,0));
            cv.setProgress(100);
        }
        else{
            // setting linearly scaled color
            int index = (int) (prgss*255);
            cv.setColor(Color.rgb((255-index),(index),0));
            cv.setProgress(prgss*100);
            left = ptarget - psteps;
        }
        double distance = (psteps * stride) / 100.0;
        tv2.setText(left + " more to reach goal");
        tv3.setText("Distance Covered: "+ distance + "m");
     }
    public static void onStepCountUpdate(int steps,int target){
        tv.setText("Steps: " + steps + "\n Target: " + target);
        psteps = steps;
        ptarget = target;
      }
    public static void onStepCountUpdate(FragmentNow thisfrag){
        thisfrag.updateCV();
    }
}
