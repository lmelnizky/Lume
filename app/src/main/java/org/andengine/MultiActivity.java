package org.andengine;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.andengine.manager.ResourcesManager;
import org.andengine.manager.SceneManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

public class MultiActivity extends Activity implements View.OnClickListener{

    private static final int REQUEST_ENABLE_BT = 7;
    private static final String LUME = "LUME";
    private static final String UUID = "41feb2a0-fb56-11e6-bc64-92361f002671";

    private String otherDeviceName;
    private String otherDeviceAddress;

    private Button enableButton, discoverButton, makeDiscoverableButton;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private interface MessageConstants {
        public static final int MESSAGE_READ=0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);

        Typeface typeFace = Typeface.createFromAsset(getAssets(),"font/lumefont.otf");

        //buttons
        enableButton = (Button) findViewById(R.id.enable);
        enableButton.setTypeface(typeFace);
        enableButton.setBackgroundResource(R.drawable.button_orange);
        enableButton.setOnClickListener(this);

        discoverButton = (Button) findViewById(R.id.discover);
        discoverButton.setTypeface(typeFace);
        discoverButton.setBackgroundResource(R.drawable.button_blue);
        discoverButton.setOnClickListener(this);

        makeDiscoverableButton = (Button) findViewById(R.id.makediscoverable);
        makeDiscoverableButton.setTypeface(typeFace);
        makeDiscoverableButton.setBackgroundResource(R.drawable.button_green);
        makeDiscoverableButton.setOnClickListener(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
        }

        pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) { //checks if there are paired devices
            StringBuffer strBuffer = new StringBuffer();
            for (BluetoothDevice device : pairedDevices) {
                otherDeviceName = device.getName();
                otherDeviceAddress = device.getAddress();
                strBuffer.append(otherDeviceName + ", " + otherDeviceAddress + "\n");
            }
            Toast.makeText(this, strBuffer, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No paired Devices", Toast.LENGTH_LONG).show();
        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                byte[] result;
                String readMessage;
                switch (inputMessage.what) {
                    case MessageConstants.MESSAGE_READ:
                        if (inputMessage.arg1 > 0) {
                            result = (byte[]) inputMessage.obj;
                            readMessage = new String(result, 0, inputMessage.arg1);
                            ResourcesManager.getInstance().activity.toastOnUiThread(readMessage, Toast.LENGTH_SHORT);
                        }
                        break;
                    default:
                        if (inputMessage.arg1 > 0) {
                            result = (byte[]) inputMessage.obj;
                            readMessage = new String(result, 0, inputMessage.arg1);
                        }
                }
            }
        };
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                finish();
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                } else if (resultCode == RESULT_CANCELED) {
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enable:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent btEnabledIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(btEnabledIntent, REQUEST_ENABLE_BT);
                }
                break;
            case R.id.discover:
                Toast.makeText(getApplicationContext(), "Starts discovery", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
                //check for permission
                int permissionCheck = ContextCompat.checkSelfPermission(MultiActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
                switch (permissionCheck) {
                    case PackageManager.PERMISSION_GRANTED:
                        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        registerReceiver(mReceiver, filter);
                        filter = new IntentFilter(
                                BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                        this.registerReceiver(mReceiver, filter);
                        if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
                        //start discovery
                        if (bluetoothAdapter.startDiscovery()) {
                            Toast.makeText(getApplicationContext(), "discovery successful", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case R.id.makediscoverable:
                //make discoverable
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                acceptThread = new AcceptThread();
                acceptThread.start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        try{
            if(mReceiver != null) {
                this.unregisterReceiver(mReceiver);
            }
        } catch (Exception e){
            e.printStackTrace();
            // already unregistered
        }
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { //TODO test receiver was final
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovery found device", Toast.LENGTH_SHORT).show();
                //discovery has found a device
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                otherDeviceName = device.getName();
                otherDeviceAddress = device.getAddress();

                connectThread = new ConnectThread(device);
                connectThread.start();
            }  else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bluetoothAdapter.cancelDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery finished! Wait until Device is found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Found no paired Devices\n" +
                        "Click Bluetooth Discovery to search", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                UUID uuid = java.util.UUID.fromString(UUID);
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(LUME, uuid);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
//                    manageMyConnectedSocket(socket);
                    ResourcesManager.getInstance().bluetoothSocket = socket;
                    ResourcesManager.getInstance().player = "Grume";

                    //TODO try sending messages here
//                    connectedThread = new ConnectedThread(socket);
//                    connectedThread.start();
//                    Toast.makeText(getApplicationContext(), "Connected: " + String.valueOf(socket.isConnected()), Toast.LENGTH_SHORT).show();


                    SceneManager.getInstance().loadMultiScene(ResourcesManager.getInstance().engine);
//

//                    app.setBluetoothSocket(socket);
//                    twoPlayerIntent.putExtra(TWODEVICES, true);
//                    twoPlayerIntent.putExtra(SERVER, true);

                    this.cancel();
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
//            try {
//                mmServerSocket.close();
//                unregisterReceiver(mReceiver); // error here
                finish();
//            } catch (IOException e) {
//                Log.e("LUME", "Could not close the connect socket", e);
//            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
                Toast.makeText(getApplicationContext(), "create first time", Toast.LENGTH_SHORT).show();


            } catch (IOException e) {
                try {
                    tmp =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                    Toast.makeText(getApplicationContext(), "create second time", Toast.LENGTH_SHORT).show();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
//                    Toast.makeText(getApplicationContext(), "Socket has to be closed", Toast.LENGTH_SHORT).show();
                    ResourcesManager.getInstance().activity.toastOnUiThread("Socket has to be closed", Toast.LENGTH_SHORT);
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);

            ResourcesManager.getInstance().bluetoothSocket = mmSocket;
            ResourcesManager.getInstance().bluetoothDevice = mmDevice;
            ResourcesManager.getInstance().player = "Lume";

            //TODO try sending messages here
//            connectedThread = new ConnectedThread(mmSocket);
//            connectedThread.start();
////            Toast.makeText(getApplicationContext(), "Connected: " + String.valueOf(mmSocket.isConnected()), Toast.LENGTH_SHORT).show();
//            ResourcesManager.getInstance().activity.toastOnUiThread("Connected: " + String.valueOf(mmSocket.isConnected()), Toast.LENGTH_SHORT);
//            byte[] g = new byte[]{'d', 'e'};
//            connectedThread.write(g);

            SceneManager.getInstance().loadMultiScene(ResourcesManager.getInstance().engine);
//            finish();
//

//            app.setBluetoothSocket(mmSocket);
//            app.setBluetoothDevice(mmDevice);
//            twoPlayerIntent.putExtra(TWODEVICES, true);
//            twoPlayerIntent.putExtra(CLIENT, true);

            this.cancel();

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
//            try {
                ResourcesManager.getInstance().bluetoothSocket = mmSocket;
                ResourcesManager.getInstance().bluetoothDevice = mmDevice;
//                app.setBluetoothSocket(mmSocket);
//                app.setBluetoothDevice(mmDevice);
//                mmSocket.close();
//                unregisterReceiver(mReceiver);
                finish();
//            } catch (IOException e) {
//                Log.e("LUME", "Could not close the client socket", e);
//            }
        }
    }




    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; //store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; //bytes returned from read

//            if (!mmSocket.isConnected()) {
//                try {
//                    mmSocket.connect();
//                    socketConnected = true;
//                    activity.toastOnUiThread("connecting successfully");
//                } catch (IOException e) {
//
////                    try {
////                        mmSocket =(BluetoothSocket) ResourcesManager.getInstance().bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(ResourcesManager.getInstance().bluetoothDevice,1);
////                    } catch (IllegalAccessException e1) {
////                        e1.printStackTrace();
////                    } catch (InvocationTargetException e1) {
////                        e1.printStackTrace();
////                    } catch (NoSuchMethodException e1) {
////                        e1.printStackTrace();
////                    }
//                    e.printStackTrace();
//                }
//            }

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);

                    //TODO just a test
                    ResourcesManager.getInstance().activity.toastOnUiThread(mmBuffer.toString(), Toast.LENGTH_SHORT);

                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
//                    handler.sendMessage(readMsg); //TODO ADDED new LINE
                } catch (IOException e) {
                    break;
                }
            }

        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }


    }
}
