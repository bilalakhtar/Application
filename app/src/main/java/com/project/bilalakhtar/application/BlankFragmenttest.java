package com.project.bilalakhtar.application;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

//two different
public class BlankFragmenttest extends android.support.v4.app.Fragment {
    private JoystickView joystick;
    private JoystickView2 joystick1;
    private TextView rollView;
    private TextView pitchView;
    private TextView yawView;
    private TextView throttleView;
    private WebView myWebView;



    private WebSocketControl webSocketController;
    private WebSocketClient mWebSocketClient;

    public BlankFragmenttest() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_fragmenttest, container,false);

        rollView = (TextView) view.findViewById(R.id.rollText);
        pitchView = (TextView) view.findViewById(R.id.pitchText);
        yawView = (TextView) view.findViewById(R.id.yawText);
        throttleView = (TextView) view.findViewById(R.id.throttleText);

        myWebView = (WebView) view.findViewById(R.id.webview);
        myWebView.loadUrl("http://192.168.10.1:9000/?action=stream");


        joystick = (JoystickView) view.findViewById(R.id.joystickView);

        joystick1 = (JoystickView2) view.findViewById(R.id.joystickView1);


        webSocketController = new WebSocketControl();

        return view;
    }

    private class WebSocketControl{
        private int roll = 1500;
        private int pitch = 1500;
        private int yaw = 1500;
        private int throttle = 1500;
        private int aux1 = 1500;
        private int aux2 = 1500;
        private int aux3 = 1500;
        private int aux4 = 1500;

        private boolean isConnected = false;

        public WebSocketControl(){
            if(joystick== null || joystick1==null)//dont mess with null
                return;
            joystick.setOnJoystickMoveListener( new JoystickView.OnJoystickMoveListener() {
                @Override
                public void onValueChanged(int joystickRadius, int currx, int curry, int centerx, int centery) {
                    int t = (int)Math.round(1500+500.0*((centery-curry)/(double)joystickRadius));
                    int y = (int)Math.round(1500+500.0*((currx-centerx)/(double)joystickRadius));
                    t = roundToTens(t);
                    y = roundToTens(y);

                    if(t!=throttle || y!=throttle){
                        sendMessage();
                    }
                    if(t!=throttle) {
                        throttle = t;
                        setUIText(throttleView, String.format(Locale.US, "Throttle: %d", throttle));
                    }
                    if(y!=yaw){
                        yaw = y;
                        setUIText(yawView, String.format(Locale.US, "Yaw: %d", yaw));
                    }
                }
            });

            joystick1.setOnJoystickMoveListener( new JoystickView2.OnJoystickMoveListener() {

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
                        setUIText(pitchView,String.format(Locale.US,"Pitch: %d",pitch));
                    }
                    if(r!=roll){
                        roll = r;
                        setUIText(rollView,String.format(Locale.US,"Roll: %d",roll));
                    }
                }
            });



            new Thread(new Runnable() {
                @Override
                public void run() {
                    getConnected();
                }
            }).start();

        }

        private void sendData(int milliseconds){
            while(isConnected) {
                sendMessage();
                Log.d("sending message","send message at millisecond time");
                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                    }
                }).start();
                try {
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */
            }
        }

        private void getConnected(){
            while(!isConnected) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isConnected = connectWebSocket();
                    }
                }).start();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //sendData(100);
        }

        private void setUIText(final TextView tv,final String text ){
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    tv.setText(text);
                }
            });
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

        private boolean connectWebSocket() {
            URI uri;
            try {
                uri = new URI("ws://192.168.10.1:80");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return false;
            }
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("Websocket", "Opened");
                    mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                }

                @Override
                public void onMessage(String s) {
                    Log.i("Websocket","Got Message: " + s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }

                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };

            try {
                return mWebSocketClient.connectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        public void sendMessage() {
            if(isConnected)
                mWebSocketClient.send(String.format("%d,%d,%d,%d,%d,%d,%d,%d",roll,pitch,yaw,throttle,aux1,aux2,aux3,aux4));
        }
    }

}
