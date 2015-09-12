package asia.sk8.yunda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
	private EditText ozEditText;
	private EditText exceedWeightEditText;
	private EditText noteEditText;

	private Button giveUpButton;
	private Button confirmButton;

	private Button failButton;
	private Button realNameButton;
	private Button calculateButton;
	
	private YDUser user;

	private Button channelButton;

	private TextView totalTextView;
	private float totalPrice = 0;

	private TextView insuranceTextView;

	private TextView RKNumberTextView;

	private TextView YDNumberTextView;


	private float deliveryPrice = 0;
	private float additionalPrice = 0;
	private float insurance = 0;
	private float extraPackageCost = 0;

	private TextView splitTextView;

	private TextView extraWeightTextView;

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
		channelButton = (Button) this.findViewById(R.id.freightChannelButton);
		totalTextView = (TextView) this.findViewById(R.id.freightTotalTextView);
		insuranceTextView = (TextView) this.findViewById(R.id.freightInsuranceTextView);
		weightEditText = (EditText) this.findViewById(R.id.freightWeightEditText);
		ozEditText = (EditText) this.findViewById(R.id.freightOzEditText);
		exceedWeightEditText = (EditText) this.findViewById(R.id.freightExceedWeightEditText);
		noteEditText = (EditText) this.findViewById(R.id.freightNoteEditText);
		giveUpButton = (Button) this.findViewById(R.id.freightGiveUpButton);
		failButton = (Button) this.findViewById(R.id.freightFailButton);
		confirmButton = (Button) this.findViewById(R.id.freightConfirmButton);
		realNameButton = (Button) this.findViewById(R.id.freightRealNameButton);
		calculateButton = (Button) this.findViewById(R.id.freightCalculateButton);

		JSONArray statusGroup = freight.getJSONArray("statusGroup");
		
		splitTextView = (TextView) this.findViewById(R.id.freightSplitTextView);
		extraWeightTextView = (TextView) this.findViewById(R.id.freightExtraWeightTextView);
		if (statusGroup.toString() != null) {
			if (statusGroup.toString().contains("210")) {
				splitTextView.setText("是");
			} else {
				splitTextView.setText("否");
			}
			if (statusGroup.toString().contains("230")) {
				extraWeightTextView.setText("是");
			} else {
				extraWeightTextView.setText("否");
			}
		}
		
		insuranceTextView.setText(freight.getString("insurance"));

		try {
			JSONObject channel = freight.getJSONObject("channel");
			channelButton.setText(channel.getString("name"));
		} catch (JSONException e1) {
			Toast.makeText(FreightActivity.this, "未找到发货渠道！", Toast.LENGTH_LONG).show();
			e1.printStackTrace();
		}
		
		channelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					final JSONArray channelArray = Yunda.setting.getJSONArray("channelList");
					CharSequence[] channelNamearray = new String[channelArray.length()];
					for (int i = 0; i < channelArray.length(); i++) {
						JSONObject channel;
							channel = (JSONObject) channelArray.get(i);
							channelNamearray[i] = channel.getString("name");
					} 
					
					new AlertDialog.Builder(FreightActivity.this)
			        .setSingleChoiceItems(channelNamearray, 0, null)
			        .setPositiveButton("确认&保存", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			                dialog.dismiss();
			                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
			                try {
								JSONObject selectedChannel = (JSONObject) channelArray.get(selectedPosition);
								channelButton.setText(selectedChannel.getString("name"));
								freight.put("channel", selectedChannel);
								freight.saveInBackground();
							    Toast.makeText(FreightActivity.this, "已保存, 请重新'计算运费'", Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {
								e.printStackTrace();
							}
			            }
			        })
			        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			                dialog.dismiss();
			            }
			        })
			        .show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		idTextView.setText(freight.getString("trackingNumber"));
		RKNumberTextView.setText(freight.getString("RKNumber"));
		YDNumberTextView.setText(freight.getString("YDNumber"));
		weightEditText.setText(Float.toString(freight.getWeight()));
		exceedWeightEditText.setText(Float.toString(freight.getExceedWeight()));
		noteEditText.setText(freight.getString("notes"));

		ozEditText.setOnFocusChangeListener(new OnFocusChangeListener() {          

		        public void onFocusChange(View v, boolean hasFocus) {
		            if (!hasFocus) {
		            	String w = weightEditText.getText().toString();
						if (w == null || w.length() == 0) { w = "0"; }
						float weight = Float.parseFloat(w);
						String o = ozEditText.getText().toString();
						if (o == null || o.length() == 0) { 
							ozEditText.setText("0");
							o = "0"; 
						}
						float oz = Float.parseFloat(o);
						float exceedWeight = weight+oz/16;
						exceedWeightEditText.setText(""+exceedWeight);
		            }
		        }
		    });
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
				if (w == null || w.length() == 0) { w = "0"; }
				float weight = Float.parseFloat(w);
				String o = ozEditText.getText().toString();
				if (o == null || o.length() == 0) { o = "0"; }
				float oz = Float.parseFloat(o);
					if (w != "0" || o != "0") {
						freight.setFinaleight(weight + oz/16);
						
						String ew = exceedWeightEditText.getText().toString();
						if (ew == null || ew.length() == 0) { ew = "0"; }
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
					weight = weight + oz/16;
					freight.setWeight(weight);
					
					//Check 0.1 抹零
					float roundedWeight = 0;
					if (weight - Math.floor(weight) <= 0.10001) {
						roundedWeight = (float) Math.floor(weight);
					} else {
						roundedWeight = weight;
					}

					Toast.makeText(FreightActivity.this, "重量：" + weight + "，(包括：" + oz +"盎司)", Toast.LENGTH_SHORT).show();
					Toast.makeText(FreightActivity.this, "抹零后：" + roundedWeight, Toast.LENGTH_SHORT).show();
					//Check 起运磅数
					try {
						JSONObject channel = freight.getJSONObject("channel");
						//Check 是否符合Channel 要求
						if (channel.getString("name").matches("小包裹A渠道") || channel.getString("name").matches("小包裹B渠道")) {
				            if (weight > 6.6) {
				            	Toast.makeText(FreightActivity.this, "小包裹A/B渠道每个包裹重量不得超过6.6磅，请使用其它渠道", Toast.LENGTH_LONG).show();;
				                return;
				            }
				        }
						if (channel.getString("name").matches("Q渠道A") || channel.getString("name").matches("Q渠道B")) {
				            if (weight > 10) {
				            	Toast.makeText(FreightActivity.this, "Q渠道A/B每个包裹重量不得超过10磅，请使用其它渠道", Toast.LENGTH_LONG).show();;
				                return;
				            }
				        }
						
						
						double startAt = channel.getDouble("startAt");
						double initialPrice = channel.getDouble("initialPrice");
						double continuePrice = channel.getDouble("continuePrice");
						if (roundedWeight < startAt) {
							roundedWeight = (float) startAt;
						}
						//Calculate Price
						float continueWeight = roundedWeight - 1;
						deliveryPrice = (float) (initialPrice + continueWeight * continuePrice);

						//若有体积重
						float exceedWeight = Float.parseFloat(exceedWeightEditText.getText().toString());
						freight.setExceedWeight(exceedWeight);
						//Check 0.1 

						float roundedExceedWeight = 0;
						if (exceedWeight - Math.floor(exceedWeight) <= 0.10001) {
							roundedExceedWeight = (float) Math.floor(exceedWeight);
						} else {
							roundedExceedWeight = exceedWeight;
						}
						Toast.makeText(FreightActivity.this, "体积重：" + exceedWeight + "，抹零后：" + roundedExceedWeight, Toast.LENGTH_SHORT).show();
						
						//Additional Price
						additionalPrice = (exceedWeight - weight) * 1;
						
						insurance = 0;
						int index = freight.getString("insurance").indexOf("(");
						if (index != -1) {
							String subString= freight.getString("insurance").substring(0 , index);
							insurance = Float.parseFloat(subString);
						}
						Toast.makeText(FreightActivity.this, "保价：" + insurance, Toast.LENGTH_SHORT).show();
						
						extraPackageCost = 0;
						JSONArray statusGroup = freight.getJSONArray("statusGroup");
						String statusString = statusGroup.toString();
//						if (statusString.contains("210")) {
//							if (statusString.contains("215")) {
//								
//							} else {
//								
//							}
//						}
						if (statusString.contains("230")) {
							extraPackageCost = Yunda.setting.getNumber("addPackageCharge").floatValue();
							if (statusString.contains("235")) {
								Toast.makeText(FreightActivity.this, "加固价格（已付款）：" + extraPackageCost, Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(FreightActivity.this, "加固价格（未付款）：" + extraPackageCost, Toast.LENGTH_SHORT).show();
							}
						}
						
						totalPrice = deliveryPrice + additionalPrice + insurance + extraPackageCost;
						totalPrice = round(totalPrice, 2);
						Toast.makeText(FreightActivity.this, 
								"总价：" + totalPrice + 
								"=（首重）" + initialPrice +
								"  +（续重）" + String.format("%.02f", continueWeight) + "x" + continuePrice + 
								"  +（体积重）"  + String.format("%.02f", additionalPrice) +
								"  +（保价）" + insurance + 
								"  +（加固）" + extraPackageCost, Toast.LENGTH_LONG).show();
						
						if (totalPrice * 100 > user.getBalance()) {
							Toast.makeText(FreightActivity.this, "用户余额不足！请点击“发货失败”", Toast.LENGTH_LONG).show();
						}
						
						totalTextView.setText("USD$" + totalPrice);
					} catch (JSONException e1) {
						Toast.makeText(FreightActivity.this, "未找到发货渠道！", Toast.LENGTH_LONG).show();
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
							freight.setFinaleight(weight);
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
							freight.setFinaleight(weight);
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
					freight.setStatus(YDFreight.STATUS_PENDING_DELIVERY);
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
			mProgressDialog.setMessage(freight.getYDNumber());
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
				
				if (freight.getStatus() == YDFreight.STATUS_PENDING_FINISHED) {
					// 发货失败
					freight.save();
				} else if (freight.getStatus() == YDFreight.STATUS_PENDING_DELIVERY) {
					// 完成运单
					// 检查并扣除运费和保价
					if (totalPrice * 100 > user.getBalance()) {
						Toast.makeText(FreightActivity.this, "用户余额不足！请点击“发货失败”", Toast.LENGTH_LONG).show();
						freight.setStatus(YDFreight.STATUS_PENDING_FINISHED);
						freight.save();
					} else {
						JSONArray statusGroup = freight.getJSONArray("statusGroup");
						String statusString = statusGroup.toString();
						if (!statusString.contains("495")) {
							//Meaning delivery is not charged yet
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("userId", user.getObjectId());
							params.put("amount", deliveryPrice + additionalPrice + insurance);
							params.put("notes", "运单号：" + freight.getYDNumber() + "，运费：" + String.format("%.02f", deliveryPrice) + "，体积重：" + String.format("%.02f", additionalPrice) + "，保价：" + String.format("%.02f", insurance));
							params.put("YDNumber", freight.getYDNumber());
							params.put("RKNumber", freight.getRKNumber());
							params.put("status", 300);
							AVCloud.callFunction("chargingUser", params);
							freight.addUnique("statusGroup", 495);
							freight.save();
						}
						// 检查并扣除分包
						if (statusString.contains("210") && !statusString.contains("215")) {
							HashMap<String, Object> params1 = new HashMap<String, Object>();
							//Check if split is already paid
							AVQuery<YDFreightIn> query = YDFreightIn.getQuery(YDFreightIn.class);
							String RawRKNumber = freight.getRKNumber().substring(0, 11);
							query.whereStartsWith("RKNumber", RawRKNumber);
							query.whereEqualTo("isChargeSplit", true);
							query.whereEqualTo("isSplitPremium", true);
							List<YDFreightIn> list = query.find();
							if (list.size() == 0) {
								//Split paid
							} else {
								//Split not paid
								params1.put("userId", user.getObjectId());
								params1.put("amount", Yunda.setting.getNumber("splitPackageCharge"));
								params1.put("notes", "分包收费，运单号：" + freight.getYDNumber());
								params1.put("YDNumber", freight.getYDNumber());
								params1.put("RKNumber", freight.getRKNumber());
								params1.put("status", 310);
								AVCloud.callFunction("chargingUserWithoutReward", params1);
								freight.addUnique("statusGroup", 215);
								freight.save();
								
								for (int i = 0; i < list.size(); i++) {
									list.get(i).put("isChargeSplit", false);
								}
								AVObject.saveAll(list);

								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(FreightActivity.this, "检测到分包，已一次性扣款成功！", Toast.LENGTH_LONG).show();
									}
								});
							}
						}
						// 检查并扣除加固费用
						if (statusString.contains("230") && !statusString.contains("235")) {

							HashMap<String, Object> params2 = new HashMap<String, Object>();
							params2.put("userId", user.getObjectId());
							params2.put("amount", extraPackageCost);
							params2.put("notes", "加固收费，运单号：" + freight.getYDNumber());
							params2.put("YDNumber", freight.getYDNumber());
							params2.put("RKNumber", freight.getRKNumber());
							params2.put("status", 350);
							AVCloud.callFunction("chargingUserWithoutReward", params2);
							freight.addUnique("statusGroup", 235);
							freight.save();
							
						}

						freight.setStatus(YDFreight.STATUS_PENDING_DELIVERY);
						freight.save();
					}
				}
			} catch (final AVException e1) {
				freight.setStatus(YDFreight.STATUS_PENDING_FINISHED);
				freight.saveInBackground();
				runOnUiThread(new Runnable() {
					public void run() {
						JSONArray statusGroup = freight.getJSONArray("statusGroup");
						String statusString = statusGroup.toString();
						if (!statusString.contains("495")) {
							Toast.makeText(FreightActivity.this, "网络错误：扣款失败！请重试“完成运单”", Toast.LENGTH_LONG).show();
						} else {
							if (statusString.contains("210") && !statusString.contains("215")) {
								Toast.makeText(FreightActivity.this, "网络错误：运费＋保价扣款成功，分包扣款失败！请重试“完成运单”", Toast.LENGTH_LONG).show();
							}
							if (statusString.contains("230") && !statusString.contains("235")) {
								Toast.makeText(FreightActivity.this, "网络错误：运费＋保价＋分包扣款成功，加固扣款失败！请重试“完成运单”", Toast.LENGTH_LONG).show();
							}
						}
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
			if (freight.getStatus() == YDFreight.STATUS_PENDING_DELIVERY) {
				Toast.makeText(FreightActivity.this, "扣款成功！等待发货...", Toast.LENGTH_LONG).show();
				finish();
			} else if (freight.getStatus() == YDFreight.STATUS_PENDING_FINISHED) {
				Toast.makeText(FreightActivity.this, "修改成功！已“发货失败”", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}
