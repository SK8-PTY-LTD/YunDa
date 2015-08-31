package asia.sk8.yunda;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import asia.sk8.Yunda;
import asia.sk8.yunda.objects.YDFreight;
import asia.sk8.yunda.objects.YDUser;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;

public class ViewFreightActivity extends Activity {

	private YDFreight freight;
	
	private YDUser user;

	private TextView YDNumberTextView;

	private TextView RKNumberTextView;

	private TextView trackingNumberTextView;

	private TextView numberIdTextView;

	private TextView emailTextView;

	private TextView summaryTextView;

	private TextView requirementTextView;

	private TextView weightTextView;

	private TextView commentTextView;

	private Button confirmButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_freight);
		
		YDNumberTextView = (TextView) this.findViewById(R.id.YDNumberTextView);
		RKNumberTextView = (TextView) this.findViewById(R.id.RKNumberTextView);
		trackingNumberTextView = (TextView) this.findViewById(R.id.trackingNumberTextView);
		numberIdTextView = (TextView) this.findViewById(R.id.numberIdTextView);
		emailTextView = (TextView) this.findViewById(R.id.emailTextView);
		summaryTextView = (TextView) this.findViewById(R.id.summaryTextView);
		weightTextView = (TextView) this.findViewById(R.id.weightTextView);
		requirementTextView = (TextView) this.findViewById(R.id.requirementTextView);
		commentTextView = (TextView) this.findViewById(R.id.commentTextView);
		confirmButton = (Button) this.findViewById(R.id.confirmCheckButton);
		
		freight = (YDFreight) Yunda.tempObject;
		user = freight.getUser();
		user.fetchInBackground(new GetCallback<AVObject>() {

			@Override
			public void done(AVObject u, AVException e) {
				if (e != null) {
					Toast.makeText(ViewFreightActivity.this, "寻找用户错误：" + e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					numberIdTextView.setText(user.getString("numberId"));
					emailTextView.setText(user.getEmail());
				}
			}
			
		});

		JSONArray statusGroup = freight.getJSONArray("statusGroup");

		YDNumberTextView.setText(freight.getString("YDNumber"));
		RKNumberTextView.setText(freight.getString("RKNumber"));
		trackingNumberTextView.setText(freight.getString("trackingNumber"));
		summaryTextView.setText(freight.getString("packageComments"));
		weightTextView.setText(Float.toString(freight.getWeight()) + " lbs");
		commentTextView.setText(freight.getString("comments"));
		
		String requirement = "";
		if (statusGroup.toString().contains("200")) {
			requirement += " 分包";
		}
		if (statusGroup.toString().contains("200")) {
			requirement += " 分包";
		}
		if (statusGroup.toString().contains("210")) {
			requirement += " 精确分包";
		}
		if (statusGroup.toString().contains("220")) {
			requirement += " 取发票";
		}
		if (statusGroup.toString().contains("230")) {
			requirement += " 加固";
		}
		if (statusGroup.toString().contains("240")) {
			requirement += " 开箱验货";
		}
		if (statusGroup.toString().contains("250")) {
			requirement += " 合包";
		}
		if (statusGroup.toString().contains("260")) {
			requirement += " 保价";
		}
		requirementTextView.setText(requirement);
		
//		final JSONObject channel = freight.getJSONObject("channel");
//		try {
//			channelTextView.setText(channel.getString("name"));
//		} catch (JSONException e1) {
//			Toast.makeText(FreightActivity.this, "未找到发货渠道！", Toast.LENGTH_LONG).show();
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
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
	
//	private class SaveFreightTask extends AsyncTask<Void, Void, Void> {
//
//		private ProgressDialog mProgressDialog;
//		private boolean saveSuccessful;
//
//		// Before anything is excuted, we need to create and show a
//		// ProgressDialog.
//		// ProgressDialog is the spinning circle covering the page
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			// Create a ProgressDialog
//			mProgressDialog = new ProgressDialog(FreightActivity.this);
//			// Set ProgressDialog title
//			mProgressDialog.setTitle("保存中...");
//			// Set ProgressDialog message
//			mProgressDialog.setMessage(freight.getObjectId());
//			mProgressDialog.setIndeterminate(false);
//			mProgressDialog.setCancelable(false);
//			mProgressDialog.setCanceledOnTouchOutside(false);
//			// Show ProgressDialog
//			mProgressDialog.show();
//		}
//
//		// Now we're really doing things.
//		// It's OK to hand over whatever heavy task to Android here.
//		@Override
//		protected Void doInBackground(Void... param) {
//			try {
//				freight.save();
//				user.save();
//				saveSuccessful = true;
//			} catch (final AVException e1) {
//				runOnUiThread(new Runnable() {
//					public void run() {
//						Toast.makeText(FreightActivity.this, "修改失败：网络错误： " + e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//					}
//				});
//				return null;
//			}
//			return null;
//		}
//
//		// Method called after doInBackground is done.
//		@Override
//		protected void onPostExecute(Void result) {
//			// Close the ProgressDialog
//			mProgressDialog.dismiss();
//			if (saveSuccessful) {
//				Toast.makeText(FreightActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
//				finish();
//			}
//		}
//	}
}

