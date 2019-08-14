package com.example.myofilament;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


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
    Button startsave;
    Button devicepause;
    View view;
    SharedPrefManager msharedPrefs;
    TextView reset;
    private BluetoothSPP bt;




    TextView sample;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Integer> mainData;

    private OnFragmentInteractionListener mListener;
    private RecyclerView.LayoutManager mLayoutManager;

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

        bt = new BluetoothSPP(getActivity());


        msharedPrefs = SharedPrefManager.getInstance(getActivity());

        mainData = new ArrayList<>(msharedPrefs.getList());

        view = inflater.inflate(R.layout.fragment_list, container, false);

        buttonsetting();

        ViewInit();

        loadSpinnerNum(msharedPrefs.getStand());

        setSpinner();

        calculation();

        setRecyclerView();

        setMainscroll();

        final TextView test = (TextView)view.findViewById(R.id.test);

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getActivity()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                test.setText(data.toString());
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getActivity()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getActivity()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getActivity()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = view.findViewById(R.id.btnConnect); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getActivity(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        setup();
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
        msharedPrefs.setStand(spinner.getSelectedItemPosition());
        msharedPrefs.setList(mainData);
        mListener = null;
        bt.stopService();
        ((MainActivity)getActivity()).resetmedia();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = view.findViewById(R.id.btnSend); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getActivity()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }


    public void buttonsetting(){
        reset = (TextView)view.findViewById(R.id.resetButton);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainData.clear();
                calculation();
                setRecyclerView();
                setMainscroll();
            }
        });

        startsave = (Button)view.findViewById(R.id.startSave);

        startsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int input = (int)((double)Math.random()*100);
                if(input>50 + spinner.getSelectedItemPosition()*5){
                    ((MainActivity)getActivity()).startmedia();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());

                    // 제목셋팅
                    alertDialogBuilder.setTitle("달성완료");
                    alertDialogBuilder
                            .setMessage("축하드립니다")
                            .setCancelable(false)
                            .setPositiveButton("완료",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            ((MainActivity)getActivity()).stopmedia();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                mainData.add(input);
                calculation();
                setRecyclerView();
                setMainscroll();
            }
        });


    }

    public void ViewInit(){
        mainscroll = (NestedScrollView) view.findViewById(R.id.mainScroll);
        percent = (TextView)view.findViewById(R.id.successPercent);
        success = (TextView)view.findViewById(R.id.numberOfSuccess);
        spinner = (Spinner)view.findViewById(R.id.spinner) ;
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView)view.findViewById(R.id.graphList);

        ArrayList<String> value = new ArrayList<>();
        for(int i = 50; i<100; i+=5) {
            value.add(Integer.toString(i)+"%");
        }
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, value);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sadapter);
    }

    public void calculation(){
        int selectitem = 50 + spinner.getSelectedItemPosition()*5;
        int active = 0;
        for(int compare : mainData){
            if(compare>=selectitem){active++;}
        }
        success.setText(active+" / "+mainData.size());
        int percentInt = (int)((double)active/(double) mainData.size()*100);
        percent.setText(Integer.toString(percentInt)+" %");
    }

    public void setRecyclerView(){
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,true);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new graphAdapter(mainData);
        recyclerView.setAdapter(adapter);
    }

    public void setMainscroll(){
        mainscroll.post(new Runnable() {
            public void run() {
                mainscroll.scrollTo(0, 0);
            }
        });
    }

    public void setSpinner(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectitem = 50 + position*5;
                int active = 0;
                for(int compare : mainData){
                    if(compare>selectitem){active++;}
                }
                success.setText(active+" / "+mainData.size());
                int percentInt = (int)((double)active/(double) mainData.size()*100);
                percent.setText(Integer.toString(percentInt)+" %");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void loadSpinnerNum(int spinnerPosition){
        spinner.setSelection(spinnerPosition);
    }
}
