package com.example.robertzzy.myotoy;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;


public class MyoToy extends ActionBarActivity {
    private Button connectionButton;
    private ImageView rotatingArrow;
    private ImageView lockStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myo_toy);
        Hub hub = Hub.getInstance();
        if(!hub.init(this)){
            Log.d(this.getClass().getName(),"Hub connection failed");
            finish();
            return;
        }
        hub.addListener(myoListenner);
        connectionButton = (Button) findViewById(R.id.connect_to_myo_button);
        rotatingArrow = (ImageView) findViewById(R.id.rotating_arrow);
        lockStatus = (ImageView) findViewById(R.id.lock_status_image);
        connectionButton.setVisibility(View.VISIBLE);
        rotatingArrow.setVisibility(View.INVISIBLE);

    }

    public void connectToHub(View view){
        Intent intent = new Intent(this, ScanActivity.class);
        this.startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_myo_toy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private DeviceListener myoListenner = new AbstractDeviceListener() {

        @Override
        public void onConnect(Myo myo, long timestamp) {
            super.onConnect(myo, timestamp);
            connectionButton.setVisibility(View.INVISIBLE);
            rotatingArrow.setVisibility(View.VISIBLE);
            Toast.makeText(getBaseContext(),"Hub Connected",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            super.onDisconnect(myo, timestamp);
            connectionButton.setVisibility(View.VISIBLE);
            rotatingArrow.setVisibility(View.INVISIBLE);
            Toast.makeText(getBaseContext(),"Hub Disconnected",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            super.onArmSync(myo, timestamp, arm, xDirection);
        }

        @Override
        public void onUnlock(Myo myo, long timestamp) {
            super.onUnlock(myo, timestamp);
            lockStatus.setImageResource(R.mipmap.unlocked_icon);
        }

        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            super.onArmUnsync(myo, timestamp);
        }

        @Override
        public void onLock(Myo myo, long timestamp) {
            super.onLock(myo, timestamp);
            lockStatus.setImageResource(R.mipmap.locked_icon);
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            super.onPose(myo, timestamp, pose);
        }

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            super.onOrientationData(myo, timestamp, rotation);
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }
            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            rotatingArrow.setRotation(roll);
            rotatingArrow.setRotationX(pitch);
            rotatingArrow.setRotationY(yaw);
        }
    };
}
