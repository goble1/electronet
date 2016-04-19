package com.redbear.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.CountDownTimer;
import java.util.concurrent.TimeUnit;


public class Chat extends Activity {
	private final static String TAG = Chat.class.getSimpleName();

	public static final String EXTRAS_DEVICE = "EXTRAS_DEVICE";

	Button btnStart= null,  btnStop= null, match_1_plus = null, match_1_minus= null, match_2_plus= null, match_2_minus= null, game_1_plus= null, game_1_minus= null, game_2_plus= null, game_2_minus= null, settimebtn= null;
	EditText settimemin, settimesec;
	TextView textViewTime, match_1_score, match_2_score, game_1_score, game_2_score;
	long millisIn, countDown;
	CounterClass timer;
	String mDeviceName;
	String mDeviceAddress;
	RBLService mBluetoothLeService;
	private Map <UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	public long getmillis(){
		return millisIn;
	}

	public void settimer(CounterClass c){
		timer=c;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
			} else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				getGattService(mBluetoothLeService.getSupportedGattService());
			} else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
				Toast.makeText(getApplicationContext(), "Net touched", Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);
		millisIn=180000;//3000
		countDown=1000;
		btnStart= (Button) findViewById(R.id.btnStart);
		btnStop= (Button) findViewById(R.id.btnStop);
		settimebtn= (Button) findViewById(R.id.settimebtn);
		settimemin= (EditText) findViewById(R.id.settimemin);
		settimesec= (EditText) findViewById(R.id.settimesec);
		textViewTime= (TextView) findViewById(R.id.textViewTime);
		match_1_plus= (Button) findViewById(R.id.match_1_plus);
		match_1_minus= (Button) findViewById(R.id.match_1_minus);
		match_1_score= (TextView) findViewById(R.id.match_1_score);
		match_2_plus= (Button) findViewById(R.id.match_2_plus);
		match_2_minus= (Button) findViewById(R.id.match_2_minus);
		match_2_score= (TextView) findViewById(R.id.match_2_score);
		game_1_plus= (Button) findViewById(R.id.button5);
		game_1_minus= (Button) findViewById(R.id.game_1_minus);
		game_1_score= (TextView) findViewById(R.id.game_1_score);
		game_2_plus= (Button) findViewById(R.id.game_2_plus);
		game_2_minus= (Button) findViewById(R.id.game_2_minus);
		game_2_score= (TextView) findViewById(R.id.game_2_score);

		timer = new CounterClass(millisIn, countDown);
		btnStop.setOnClickListener(new View.OnClickListener(){
									   @Override
									   public void onClick (View v){
										   //timer.cancel();
									   }
								   }
		);


		btnStart.setOnClickListener(new startc(this));
		btnStop.setOnClickListener(new stopc(this));

		match_1_plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//send function


				String s = (String) match_1_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(++Number);
				match_1_score.setText(s);

				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});

		match_1_minus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = (String) match_1_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(--Number);
				match_1_score.setText(s);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});

		match_2_plus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = (String) match_2_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(++Number);
				match_2_score.setText(s);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});

		match_2_minus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = (String) match_2_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(--Number);
				match_2_score.setText(s);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});

		game_1_plus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = (String) game_1_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(++Number);
				game_1_score.setText(s);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});

		game_1_minus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = (String) game_1_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(--Number);
				game_1_score.setText(s);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});

		game_2_plus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = (String) game_2_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(++Number);
				game_2_score.setText(s);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});

		game_2_minus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = (String) game_2_score.getText();
				int Number = Integer.valueOf(s);
				s = Integer.toString(--Number);
				game_2_score.setText(s);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);
			}
		});
		settimebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = String.valueOf(settimemin.getText());
				int setmin = Integer.valueOf(s);
				s = String.valueOf(settimesec.getText());
				int setsec = Integer.valueOf(s);
				String ms = String.format("%02d:%02d", setmin, setsec);
				settimemin.setText("");
				settimesec.setText("");
				textViewTime.setText(ms);
				//Log.d("test", "" + millisIn);
				millisIn=(setmin*60+setsec)*1000;
				//Log.d("test", ""+millisIn);
				BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
				String one = (String) match_1_score.getText();
				String two = (String) match_2_score.getText();
				String three = (String) game_1_score.getText();
				String four = (String) game_2_score.getText();
				String temp = String.valueOf(textViewTime.getText());
				String five = temp.substring(0, 2);
				String six = temp.substring(3, 5);
				String str = one+two+three+four+five+six;
				byte b = 0x00;
				byte[] tmp = str.getBytes();
				byte[] tx = new byte[tmp.length + 1];
				tx[0] = b;
				for (int i = 1; i < tmp.length + 1; i++) {
					tx[i] = tmp[i - 1];
				}
				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);

			}
		});

		Intent intent = getIntent();

		mDeviceAddress = intent.getStringExtra(Device.EXTRA_DEVICE_ADDRESS);
		mDeviceName = intent.getStringExtra(Device.EXTRA_DEVICE_NAME);

		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent gattServiceIntent = new Intent(this, RBLService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();

			System.exit(0);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mBluetoothLeService.disconnect();
		mBluetoothLeService.close();

		System.exit(0);
	}


	private void getGattService(BluetoothGattService gattService) {
		if (gattService == null)
			return;

		BluetoothGattCharacteristic characteristic = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
		map.put(characteristic.getUuid(), characteristic);

		BluetoothGattCharacteristic characteristicRx = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
		mBluetoothLeService.setCharacteristicNotification(characteristicRx,
				true);
		mBluetoothLeService.readCharacteristic(characteristicRx);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

		return intentFilter;
	}

	public class CounterClass extends CountDownTimer{

		public CounterClass(long millisInFuture, long countDownInterval){
			super(millisInFuture,countDownInterval);

		}

		//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		//@SuppressLint("NewApi")
		@Override
		public void onTick(long millisUntilFinished){

			long millis = millisUntilFinished;
			String ms = String.format("%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(millis)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
					(TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))%60);
			//System.out.println(ms);
			textViewTime.setText(ms);
			BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
			String one = (String) match_1_score.getText();
			String two = (String) match_2_score.getText();
			String three = (String) game_1_score.getText();
			String four = (String) game_2_score.getText();
			String temp = String.valueOf(textViewTime.getText());
			String five = temp.substring(0, 2);
			String six = temp.substring(3, 5);
			String str = one+two+three+four+five+six;
			byte b = 0x00;
			byte[] tmp = str.getBytes();
			byte[] tx = new byte[tmp.length + 1];
			tx[0] = b;
			for (int i = 1; i < tmp.length + 1; i++) {
				tx[i] = tmp[i - 1];
			}
			characteristic.setValue(tx);
			mBluetoothLeService.writeCharacteristic(characteristic);

		}

		@Override
		public void onFinish(){
			//TODO Auto-Generating method stub
			textViewTime.setText("Done");
		}

	}


	public class startc implements View.OnClickListener {
		Chat homer;

		public startc( Chat h){
			homer= h;
		}

		public void onClick (View v){
			long milli=homer.getmillis();
			CounterClass c = new CounterClass(milli,1000);
			homer.settimer(c);
			timer.start();
			//Log.d("test", "" + millisIn);
		}
	}

	public class stopc implements View.OnClickListener {
		Chat homer;

		public stopc( Chat h){
			homer= h;
		}

		public void onClick (View v){
			String temp = String.valueOf(textViewTime.getText());
			System.out.println(temp.substring(3, 5));
			millisIn=Integer.valueOf(temp.substring(0, 2))*60000+Integer.valueOf(temp.substring(3, 5))*1000;
			timer.cancel();
			//Log.d("test", "" + millisIn);
		}
	}
}
