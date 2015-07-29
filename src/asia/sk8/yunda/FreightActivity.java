package asia.sk8.yunda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import asia.sk8.Yunda;
import asia.sk8.yunda.objects.YDFreight;
import asia.sk8.yunda.objects.YDFreightIn;
import asia.sk8.yunda.objects.YDTransaction;
import asia.sk8.yunda.objects.YDUser;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.RefreshCallback;

public class FreightActivity extends Activity {

	private YDFreight freight;

	private TextView idTextView;
	private EditText weightEditText;
	private EditText exceedWeightEditText;
	private EditText noteEditText;

	private Button giveUpButton;
	private Button confirmButton;

	private Button failButton;
	private Button realNameButton;
	private Button calculateButton;
	
	private YDUser user;

	private TextView channelTextView;

	private TextView totalTextView;
	private float totalPrice = 0;

	private TextView insuranceTextView;

	private TextView RKNumberTextView;

	private TextView YDNumberTextView;

	private List<AVObject> transactionList  = new ArrayList<AVObject>();;


	private float deliveryPrice;
	private float additionalPrice;
	private float insurance;
	private float extraPackageCost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_freight);
		
		freight = (YDFreight) Yunda.tempObject;
		user = freight.getUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject u, AVException e) {
				if (e != null) {
					Toast.makeText(FreightActivity.this, "寻找用户错误：" + e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					realNameButton.setText(user.getRealName() + "(点击发送邮件)");
				}
			}
			
		});

		idTextView = (TextView) this.findViewById(R.id.freightTrackingNumberTextView);
		RKNumberTextView = (TextView) this.findViewById(R.id.freightRKNumberTextView);
		YDNumberTextView = (TextView) this.findViewById(R.id.freightYDNumberTextView);
		channelTextView = (TextView) this.findViewById(R.id.freightChannelTextView);
		totalTextView = (TextView) this.findViewById(R.id.freightTotalTextView);
		insuranceTextView = (TextView) this.findViewById(R.id.freightInsuranceTextView);
		weightEditText = (EditText) this.findViewById(R.id.freightWeightEditText);
		exceedWeightEditText = (EditText) this.findViewById(R.id.freightExceedWeightEditText);
		noteEditText = (EditText) this.findViewById(R.id.freightNoteEditText);
		giveUpButton = (Button) this.findViewById(R.id.freightGiveUpButton);
		failButton = (Button) this.findViewById(R.id.freightFailButton);
		confirmButton = (Button) this.findViewById(R.id.freightConfirmButton);
		realNameButton = (Button) this.findViewById(R.id.freightRealNameButton);
		calculateButton = (Button) this.findViewById(R.id.freightCalculateButton);

		JSONArray statusGroup = freight.getJSONArray("statusGroup");
		
		final TextView splitTextView = (TextView) this.findViewById(R.id.freightSplitTextView);
		if (statusGroup.toString().contains("210")) {
			splitTextView.setText("是");
		} else {
			splitTextView.setText("否");
		}
		final TextView extraWeightTextView = (TextView) this.findViewById(R.id.freightExtraWeightTextView);
		if (statusGroup.toString().contains("230")) {
			extraWeightTextView.setText("是");
		} else {
			extraWeightTextView.setText("否");
		}
		
		insuranceTextView.setText(freight.getString("insurance"));
		
		final JSONObject channel = freight.getJSONObject("channel");
		try {
			channelTextView.setText(channel.getString("name"));
		} catch (JSONException e1) {
			Toast.makeText(FreightActivity.this, "未找到发货渠道！", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		idTextView.setText(freight.getString("trackingNumber"));
		RKNumberTextView.setText(freight.getString("RKNumber"));
		YDNumberTextView.setText(freight.getString("YDNumber"));
		weightEditText.setText(Float.toString(freight.getWeight()));
		exceedWeightEditText.setText(Float.toString(freight.getExceedWeight()));
		noteEditText.setText(freight.getString("notes"));
		
		realNameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String email = user.getEmail();
				
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
			            "mailto",email, null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "关于运单：" + freight.getString("packageComments"));
				emailIntent.putExtra(Intent.EXTRA_TEXT, "你好，这里是Yunda。关于您的快递，订单号："+freight.getString("YDNumber")+"，");
				try {
				    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(FreightActivity.this, "未找到邮件客户端！请确保设备上有装邮件App。", Toast.LENGTH_LONG).show();
				}
			}
		});
		calculateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
					
					String w = weightEditText.getText().toString();
					if (w != null && w.length() > 0) {
						float weight = Float.parseFloat(w);
						if (weight != 0) {
							freight.setWeight(weight);
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(FreightActivity.this, "重量不得为0", Toast.LENGTH_LONG).show();
								}
							});
							return;
						}
						String ew = exceedWeightEditText.getText().toString();
						if (Float.parseFloat(ew) < Float.parseFloat(w)) {
							Toast.makeText(FreightActivity.this, "体积重不得小于重量",
									Toast.LENGTH_LONG).show(); 
							return;
						} else {
							if (ew != null && ew.length() > 0 && Float.parseFloat(ew) != 0) {
								freight.setExceedWeight(Float.parseFloat(ew));
							}
						}
						freight.setNote(noteEditText.getText().toString());
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(FreightActivity.this, "重量不得为空", Toast.LENGTH_LONG).show();
							}
						});
						return;
					}
					
					//Calculate freight pricing.
					float weight = freight.getWeight();
					//Check 0.1 抹零
					weight = Math.round(weight + 0.4);
					//Check 起运磅数
					try {
						double startAt = channel.getDouble("startAt");
						double initialPrice = channel.getDouble("initialPrice");
						double continuePrice = channel.getDouble("continuePrice");
						if (weight < startAt) {
							weight = (float) startAt;
						}
						//Calculate Price
						deliveryPrice = (float) (initialPrice + (weight - 1) * continuePrice);

						//若有体积重
						//Check 0.1 抹零
						float exceedWeight = freight.getExceedWeight();
						exceedWeight = Math.round(exceedWeight + 0.4);
						
						//Additional Price
						additionalPrice = (exceedWeight - weight) * 1;
						
						insurance = 0;
						int index = freight.getString("insurance").indexOf("(");
						if (index != -1) {
							String subString= freight.getString("insurance").substring(0 , index);
							insurance = Float.parseFloat(subString);
						}
						extraPackageCost = 0;
						if (extraWeightTextView.getText().toString() == "是") {
							extraPackageCost = Yunda.setting.getNumber("addPackageCharge").floatValue();
						}
						
						totalPrice = deliveryPrice + additionalPrice + insurance + extraPackageCost;
						totalPrice = round(totalPrice,2);
						
						totalTextView.setText("USD$" + totalPrice);
					} catch (JSONException e1) {
						Toast.makeText(FreightActivity.this, "未找到发货渠道！", Toast.LENGTH_LONG).show();
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		});
		giveUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
					
					String w = weightEditText.getText().toString();
					if (w != null && w.length() > 0) {
						float weight = Float.parseFloat(w);
						if (weight != 0) {
							freight.setWeight(weight);
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(FreightActivity.this, "重量不得为0", Toast.LENGTH_LONG).show();
								}
							});
							return;
						}
						String ew = exceedWeightEditText.getText().toString();
						if (Float.parseFloat(ew) < Float.parseFloat(w)) {
							Toast.makeText(FreightActivity.this, "体积重不得小于重量",
									Toast.LENGTH_LONG).show();
							return;
						} else {
							if (ew != null && ew.length() > 0 && Float.parseFloat(ew) != 0) {
								freight.setExceedWeight(Float.parseFloat(ew));
							}
						}
						freight.setNote(noteEditText.getText().toString());
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(FreightActivity.this, "重量不得为空", Toast.LENGTH_LONG).show();
							}
						});
						return;
					}

				freight.setStatus(YDFreight.STATUS_PENDING_FINISHED);
				new SaveFreightTask().execute();
			}
		});
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
					
					String w = weightEditText.getText().toString();
					if (w != null && w.length() > 0) {
						float weight = Float.parseFloat(w);
						if (weight != 0) {
							freight.setWeight(weight);
							freight.setExceedWeight(Float.parseFloat(exceedWeightEditText.getText().toString()));
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(FreightActivity.this, "重量不得为0", Toast.LENGTH_LONG).show();
								}
							});
							return;
						}
						freight.setNote(noteEditText.getText().toString());
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(FreightActivity.this, "重量不得为空", Toast.LENGTH_LONG).show();
							}
						});
						return;
					}
				
				if (totalPrice == 0) {
					Toast.makeText(FreightActivity.this, "请先“计算运费”！", Toast.LENGTH_LONG).show();
					return;
				} else if (totalPrice * 100 > user.getBalance()) {
					Toast.makeText(FreightActivity.this, "用户余额不足！请点击“发货失败”", Toast.LENGTH_LONG).show();
					return;
				} else {					
					user.setBalance((int) (user.getBalance() - totalPrice * 100));
					
					freight.setStatus(YDFreight.STATUS_PENDING_DELIVERY);
					YDTransaction transaction0 = new YDTransaction();
					transaction0.setAmount(deliveryPrice + additionalPrice + insurance);
					transaction0.put("status", 300);
					transaction0.put("notes", "运单处理，运单号：" + freight.getString("YDNumber"));
					transaction0.put("YDNumber", freight.getString("YDNumber"));
					transactionList.add(transaction0);

					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("userId", user.getObjectId());
					params.put("amount", deliveryPrice + additionalPrice + insurance);
					AVCloud.callFunctionInBackground("chargingUser", params,
							new FunctionCallback<String>() {
								@Override
								public void done(String message, AVException e) {
									
								}
							});
					if (splitTextView.getText().toString() == "是") {
						YDTransaction transaction1 = new YDTransaction();
						transaction1.setAmount(Yunda.setting.getNumber("splitPackageCharge").floatValue());
						transaction1.put("status", 310);
						transaction1.put("notes", "分包收费，运单号：" + freight.getString("YDNumber"));
						transaction1.put("YDNumber", freight.getString("YDNumber"));
						transactionList.add(transaction1);

						HashMap<String, Object> params1 = new HashMap<String, Object>();
						params1.put("userId", user.getObjectId());
						params1.put("amount", Yunda.setting.getNumber("splitPackageCharge"));
						AVCloud.callFunctionInBackground("chargingUser", params,
								new FunctionCallback<String>() {
									@Override
									public void done(String message, AVException e) {
										
									}
								});
					}
					// 合包免费
//					if (freight.getBoolean("isMerged")) {
//						YDTransaction transaction2 = new YDTransaction();
//						transaction2.setAmount(Yunda.setting.getNumber("addPackageCharge").floatValue());
//						transaction2.put("status", 350);
//						transaction2.put("notes", "合包收费，运单号：" + freight.getString("YDNumber"));
//						transaction2.put("YDNumber", freight.getString("YDNumber"));
//						transaction2.put("user", user);
//						transactionList.add(transaction2);
//						
//						HashMap<String, Object> params1 = new HashMap<String, Object>();
//						params1.put("userId", user.getObjectId());
//						params1.put("amount", Yunda.setting.getNumber("addPackageCharge"));
//						AVCloud.callFunctionInBackground("chargingUser", params,
//								new FunctionCallback<String>() {
//									@Override
//									public void done(String message, AVException e) {
//										
//									}
//								});
//					}
					if (extraWeightTextView.getText().toString() == "是") {
						YDTransaction transaction2 = new YDTransaction();
						transaction2.setAmount(extraPackageCost);
						transaction2.put("status", 350);
						transaction2.put("notes", "加固收费，运单号：" + freight.getString("YDNumber"));
						transaction2.put("YDNumber", freight.getString("YDNumber"));
						transactionList.add(transaction2);
						
						HashMap<String, Object> params1 = new HashMap<String, Object>();
						params1.put("userId", user.getObjectId());
						params1.put("amount", Yunda.setting.getNumber("addPackageCharge"));
						AVCloud.callFunctionInBackground("chargingUser", params,
								new FunctionCallback<String>() {
									@Override
									public void done(String message, AVException e) {
										
									}
								});
					}
					new SaveFreightTask().execute();
				}

			}
		});
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
        return bd.floatValue();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.freight, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class SaveFreightTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog mProgressDialog;
		private boolean saveSuccessful;

		// Before anything is excuted, we need to create and show a
		// ProgressDialog.
		// ProgressDialog is the spinning circle covering the page
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a ProgressDialog
			mProgressDialog = new ProgressDialog(FreightActivity.this);
			// Set ProgressDialog title
			mProgressDialog.setTitle("保存中...");
			// Set ProgressDialog message
			mProgressDialog.setMessage(freight.getObjectId());
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			// Show ProgressDialog
			mProgressDialog.show();
		}

		// Now we're really doing things.
		// It's OK to hand over whatever heavy task to Android here.
		@Override
		protected Void doInBackground(Void... param) {
			try {
				transactionList.add(freight);
				AVObject.saveAll(transactionList);
				saveSuccessful = true;
			} catch (final AVException e1) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(FreightActivity.this, "修改失败：网络错误： " + e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					}
				});
				return null;
			}
			return null;
		}

		// Method called after doInBackground is done.
		@Override
		protected void onPostExecute(Void result) {
			// Close the ProgressDialog
			mProgressDialog.dismiss();
			if (saveSuccessful) {
				Toast.makeText(FreightActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}
