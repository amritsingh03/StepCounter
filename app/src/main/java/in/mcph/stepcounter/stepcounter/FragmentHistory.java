package in.mcph.stepcounter.stepcounter;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHistory extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int DAYS = 30;
    private static SharedPreferences sp;

    GraphView gview;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public FragmentHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
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
        View v =  inflater.inflate(R.layout.fragment_fragment_history, container, false);
        gview = v.findViewById(R.id.gview);
        drawGraph();
        return v;
    }

    private void drawGraph() {

     // prepare data for past N-days
        LineGraphSeries<DataPoint> data = prepareData();
     // display data
        gview.addSeries(data);
        gview.setTitle("Past "+ DAYS + " day data");
        gview.getViewport().setScalable(true);
        gview.getViewport().setScalableY(true);
        gview.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(gview.getContext()));
        gview.getGridLabelRenderer().setNumHorizontalLabels(4);
    }

    private LineGraphSeries<DataPoint> prepareData() {

        LineGraphSeries<DataPoint> temp = new LineGraphSeries<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-1 * DAYS + 1);
        Date dt,d0 = null,df = null;
        String id;
        int steps;
        DataPoint dp;

        for(int i = 0; i < DAYS ; i++){
            dt = cal.getTime();
            if(i == 0) d0 = dt;
            if(i == DAYS - 1 ) df = dt;
            id = getFormattedDate(cal) + "_steps";
            steps = sp.getInt(id,0);
            dp = new DataPoint(dt,steps);
            temp.appendData(dp,false,DAYS);
            cal.add(Calendar.DATE,1);
        }
        temp.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String date = getDateFromDouble(dataPoint.getX());
                Toast.makeText(gview.getContext(),"Date: "+date+"\nSteps: "+ dataPoint.getY(),Toast.LENGTH_SHORT).show();
            }
        });
        gview.getViewport().setMinX(d0.getTime());
        gview.getViewport().setMaxX(df.getTime());
        gview.getViewport().setXAxisBoundsManual(true);
        gview.getGridLabelRenderer().setHumanRounding(false);
        return temp;
    }

    private String getFormattedDate(Calendar cal){
        String date = cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);
        return date;
    }

    private String getDateFromDouble(double dt){
        long ms = (long) dt;
        DateFormat simple = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Date result = new Date(ms);
        return simple.format(result);
    }


    public static void setSharedPreferences(SharedPreferences s){
        sp = s;
    }

}
