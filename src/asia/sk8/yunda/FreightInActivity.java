package asia.sk8.yunda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import asia.sk8.Yunda;
import asia.sk8.yunda.objects.YDFreight;
import asia.sk8.yunda.objects.YDFreightIn;
import asia.sk8.yunda.objects.YDUser;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil.log;

public class FreightInActivity extends Activity {

	private YDFreightIn freightIn;

	private TextView idTextView;
	private TextView RKTextView;
	private EditText userIdEditText;
	private EditText weightEditText;
	private EditText exceedWeightEditText;
	private EditText noteEditText;

	private Button giveUpButton;

	private Button confirmButton;

	private EditText userNameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_freight_in);

		Intent intent = this.getIntent();
		final String data = intent.getStringExtra("freightIn");

		freightIn = (YDFreightIn) Yunda.tempObject;

		idTextView = (TextView) this
				.findViewById(R.id.freightInDeliveryIdTextView);
		RKTextView = (TextView) this
				.findViewById(R.id.freightInRKNumberTextView);
		userNameEditText = (EditText) this
				.findViewById(R.id.freightInUserNameExitText);
		userIdEditText = (EditText) this
				.findViewById(R.id.freightInUserIdExitText);
		weightEditText = (EditText) this
				.findViewById(R.id.freightInWeightEditText);
		exceedWeightEditText = (EditText) this
				.findViewById(R.id.freightInExceedWeightEditText);
		noteEditText = (EditText) this
				.findViewById(R.id.freightInNotesEditText);
		giveUpButton = (Button) this.findViewById(R.id.freightInGiveUpButton);
		confirmButton = (Button) this.findViewById(R.id.freightInConfirmButton);

		idTextView.setText(freightIn.getTrackingNumber());
		RKTextView.setText(freightIn.getString("RKNumber"));
		
		giveUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				String w = weightEditText.getText().toString();
				if (w != null && w.length() > 0 && Float.parseFloat(w) != 0) {
					freightIn.setWeight(Float.parseFloat(w));
				} else {
					Toast.makeText(FreightInActivity.this, "重量不得为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				String ew = exceedWeightEditText.getText().toString();
				if (Float.parseFloat(ew) < Float.parseFloat(w)) {
					Toast.makeText(FreightInActivity.this, "体积重不得小于重量",
							Toast.LENGTH_LONG).show();
					return;
				} else {
					if (ew != null && ew.length() > 0 && Float.parseFloat(ew) != 0) {
						freightIn.setExceedWeight(Float.parseFloat(ew));
					}
					freightIn.setNote(noteEditText.getText().toString());

					final String id = userIdEditText.getText().toString();
					final String username = userNameEditText.getText().toString();
					if (id == "" && username == "") {
						Toast.makeText(FreightInActivity.this, "数字Id／用户名 二填写一", Toast.LENGTH_LONG).show();
						return;
					}
					
					new GenerateFreightInTask().execute();
				}
			}
		});
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.user_id, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private class GenerateFreightInTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog mProgressDialog;

		// Before anything is excuted, we need to create and show a
		// ProgressDialog.
		// ProgressDialog is the spinning circle covering the page
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a ProgressDialog
			mProgressDialog = new ProgressDialog(FreightInActivity.this);
			// Set ProgressDialog title
			mProgressDialog.setTitle("入库中...");
			// Set ProgressDialog message
			mProgressDialog.setMessage(freightIn.getTrackingNumber());
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

			final String id = userIdEditText.getText().toString();
			final String username = userNameEditText.getText().toString();
			AVQuery<AVUser> query1 = AVUser.getQuery();
			query1.whereEqualTo("numberId", id);
			AVQuery<AVUser> query2 = AVUser.getQuery();
			query2.whereEqualTo("stringId", username);
			List<AVQuery<AVUser>> queries = new ArrayList<AVQuery<AVUser>>();
			queries.add(query1);
			queries.add(query2);
			AVQuery mainQuery = AVQuery.or(queries);
			try {
				final List<AVUser> userList = mainQuery.find();
				if (userList.size() == 0) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(FreightInActivity.this,
									"入库失败，未找到用户：" + id, Toast.LENGTH_LONG)
									.show();
						}
					});
				} else if (userList.size() > 1) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(FreightInActivity.this, "入库失败：数字Id／用户名 请二填写一", Toast.LENGTH_LONG).show();
						}
					});
				} else {
					AVUser user = userList.get(0);
					Log.i("FreightInActivity", "User is " + user.getUsername());
					freightIn.setUser(AVUser.cast(user, YDUser.class));
					freightIn.save();
				}
			} catch (final AVException e1) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(FreightInActivity.this,
								"入库失败：网络错误： " + e1.getLocalizedMessage(),
								Toast.LENGTH_LONG).show();
					}
				});
				log.e("TabActivity", e1);
				return null;
			}
			return null;
		}

		// Method called after doInBackground is done.
		@Override
		protected void onPostExecute(Void result) {
			// Close the ProgressDialog
			mProgressDialog.dismiss();
			if (freightIn.getCreatedAt() != null) {
				Toast.makeText(FreightInActivity.this, "入库成功！",
						Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}
