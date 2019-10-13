package de.kai_morich.simple_bluetooth_terminal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    graphAdapter adapter;
    Spinner spinner;
    TextView success;
    TextView percent;
    NestedScrollView mainscroll;
    Button connect;
    View view;
    SharedPrefManager msharedPrefs;
    TextView reset;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Integer> mainData;
    private ArrayList<String> Date;

    private OnFragmentInteractionListener mListener;
    private RecyclerView.LayoutManager mLayoutManager;

    long now = System.currentTimeMillis();
    java.util.Date date = new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String getTime = sdf.format(date);

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
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

        msharedPrefs = SharedPrefManager.getInstance(getActivity());

        mainData = new ArrayList<>(msharedPrefs.getList());
        Date = new ArrayList<>(msharedPrefs.getDate());

        view = inflater.inflate(R.layout.fragment_list, container, false);

        buttonsetting();

        ViewInit();

        calculation();

        setRecyclerView();

        setMainscroll();

        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        msharedPrefs.setList(mainData);
        msharedPrefs.setDate(Date);
        mListener = null;
        ((ShowActivity)getActivity()).resetmedia();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setlist(ArrayList<Integer> list) {
        int size = list.size();
        mainData.addAll(list);
        for(int i = 0; i < size; i++) {
            Date.add(getTime);
        }
        calculation();
        setRecyclerView();
        setMainscroll();
    }

    public void buttonsetting(){
        reset = (TextView)view.findViewById(R.id.resetButton);

        reset.setOnClickListener(v -> {
            mainData.clear();
            Date.clear();
            calculation();
            setRecyclerView();
            setMainscroll();
        });

        connect = (Button)view.findViewById(R.id.btnConnect);

        connect.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivityForResult(intent,1);
        });


    }

    public void ViewInit(){
        mainscroll = (NestedScrollView) view.findViewById(R.id.mainScroll);
        percent = (TextView)view.findViewById(R.id.successPercent);
        success = (TextView)view.findViewById(R.id.numberOfSuccess);
        spinner = (Spinner)view.findViewById(R.id.spinner) ;
        recyclerView = (RecyclerView)view.findViewById(R.id.graphList);

        ArrayList<String> value = new ArrayList<>();
        for(int i = 50; i<100; i+=5) {
            value.add(i +"%");
        }
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, value);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sadapter);
    }

    public void calculation(){
        int active = 0;
        for(int compare : mainData){
            if(compare==1){active++;}
        }
        success.setText(active+" / "+mainData.size());
        int percentInt = (int)((double)active/(double) mainData.size()*100);
        percent.setText(percentInt +" %");
    }

    public void setRecyclerView(){
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new graphAdapter(mainData,Date);
        recyclerView.setAdapter(adapter);
    }

    public void setMainscroll(){
        mainscroll.post(() -> mainscroll.scrollTo(0, 0));
    }
}
