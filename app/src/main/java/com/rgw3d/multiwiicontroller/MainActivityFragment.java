package com.rgw3d.multiwiicontroller;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG= MainActivityFragment.class.getSimpleName();

    private JoystickView joystick;
    private JoystickView joystick1;
    private TextView rollView;
    private TextView pitchView;
    private TextView yawView;
    private TextView throttleView;
    private TriToggleButton aux1View;
    private TriToggleButton aux2View;
    private TriToggleButton aux3View;
    private TriToggleButton aux4View;

    private int roll = 1500;
    private int pitch = 1500;
    private int yaw = 1500;
    private int throttle = 1500;
    private int aux1 = 1500;
    private int aux2 = 1500;
    private int aux3 = 1500;
    private int aux4 = 1500;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container,false);

        rollView = (TextView) view.findViewById(R.id.rollText);
        pitchView = (TextView) view.findViewById(R.id.pitchText);
        yawView = (TextView) view.findViewById(R.id.yawText);
        throttleView = (TextView) view.findViewById(R.id.throttleText);

        aux1View = (TriToggleButton) view.findViewById(R.id.auxButton1) ;
        aux2View = (TriToggleButton) view.findViewById(R.id.auxButton2) ;
        aux3View = (TriToggleButton) view.findViewById(R.id.auxButton3) ;
        aux4View = (TriToggleButton) view.findViewById(R.id.auxButton4) ;

        joystick = (JoystickView) view.findViewById(R.id.joystickView);

        joystick1 = (JoystickView) view.findViewById(R.id.joystickView1);

        joystick.setOnJoystickMoveListener( new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int joystickRadius, int currx, int curry, int centerx, int centery) {
                int t = (int)Math.round(1500+500.0*((centery-curry)/(double)joystickRadius));
                int y = (int)Math.round(1500+500.0*((currx-centerx)/(double)joystickRadius));
                t = roundToTens(t);
                y = roundToTens(y);

                if(t!=throttle || y!=yaw){
                    sendMessage();
                }
                if(t!=throttle) {
                    throttle = t;
                    throttleView.setText(String.format(Locale.US, "Throttle: %d", throttle));
                }
                if(y!=yaw){
                    yaw = y;
                    yawView.setText(String.format(Locale.US, "Yaw: %d", yaw));
                }
            }
        });

        joystick1.setOnJoystickMoveListener( new JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int joystickRadius, int currx, int curry, int centerx, int centery) {
                //angle = Math.round(angle);
                //Log.d("angle",angle+"");
                int p = (int)Math.round(1500+500.0*((centery-curry)/((double)joystickRadius)));//(int) (1500+(500*(Math.sin(Math.toRadians(angle)) * power)));
                int r = (int)Math.round(1500+500.0*((currx-centerx)/((double)joystickRadius)));//(int) (1500+(500*(Math.cos(Math.toRadians(angle)) * power)));
                p = roundToTens(p);
                r = roundToTens(r);

                if(p!=pitch || r != roll)
                    sendMessage();
                if (p != pitch){
                    pitch = p;
                    pitchView.setText(String.format(Locale.US,"Pitch: %d",pitch));
                }
                if(r!=roll){
                    roll = r;
                    rollView.setText(String.format(Locale.US,"Roll: %d",roll));
                }
            }
        });

        aux1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriToggleButton ttb = (TriToggleButton) v;
                aux1 = ttb.getValue();
                sendMessage();
            }
        });

        aux2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriToggleButton ttb = (TriToggleButton) v;
                aux2 = ttb.getValue();
                sendMessage();
            }
        });

        aux3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriToggleButton ttb = (TriToggleButton) v;
                aux3 = ttb.getValue();
                sendMessage();
            }
        });

        aux4View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriToggleButton ttb = (TriToggleButton) v;
                aux4 = ttb.getValue();
                sendMessage();
            }
        });



        return view;
    }

    private int roundToTens(int in){
        int out = in;
        out = (out/10)*10;

        int ones = in%10;
        if(ones>=5)//round up
            out+=10;

        //Log.d("in vs out",in + "  "+out);
        return out;
    }
    /*
    public void sendMessage() {
        if(isConnected)
            mWebSocketClient.send(String.format("%d,%d,%d,%d,%d,%d,%d,%d",roll,pitch,yaw,throttle,aux1,aux2,aux3,aux4));
    }*/

    public void sendMessage(){
        if(Common.isConnected){
            try{
                JSONObject sendObject=new JSONObject();
                sendObject.put("action","rcData");

                JSONObject data= new JSONObject();
                data.put("roll",roll);
                data.put("pitch",pitch);
                data.put("yaw",yaw);
                data.put("throttle",throttle);
                data.put("aux1",aux1);
                data.put("aux2",aux2);
                data.put("aux3",aux3);
                data.put("aux4",aux4);


                sendObject.put("data",data);
                Log.d(LOG_TAG, "sendMessage: "+sendObject.toString());
                Common.sendMessage(sendObject.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}
