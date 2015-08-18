//================================================================================
//YDFreightIn is a subclass of AVObject
//Class name: Address
//Author: Xujie Song
//Copyright: SK8 PTY LTD
//================================================================================

package asia.sk8.yunda.objects;

import android.util.Log;
import asia.sk8.yunda.objects.YDUser.UserCallback;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil.log;

@AVClassName("FreightIn")
public class YDFreightIn extends AVObject {

	// ================================================================================
	// Constructors
	// ================================================================================
	
	public YDFreightIn() {
		this.setStatus(YDFreightIn.STATUS_INITIALIZED);
	}
	
	public YDFreightIn(String objectId) {
		this.setObjectId(objectId);
	}
	
	// ================================================================================
	// Class properties
	// ================================================================================

	public static int STATUS_INITIALIZED = 0;

	//Pending user action

	public static int STATUS_MANUAL = 100;

	public static int STATUS_SPEED_MANUAL = 110;

	public static int STATUS_ARRIVED = 200;

	public static int STATUS_PENDING_CHECK_PACKAGE = 210;

	public static int STATUS_FINISHED_CHECK_PACKAGE = 290;

	public static int STATUS_CONFIRMED = 300;

	public static int STATUS_FINISHED = 900;

	// ================================================================================
	// Shelf Methods
	// ================================================================================

	public void getUserWithCallBack(final UserCallback callback) {
		YDUser user = this.getUser();
		AVQuery<YDUser> query = AVUser.getUserQuery(YDUser.class);
		query.getInBackground(user.getObjectId(), new GetCallback<YDUser>() {
			@Override
			public void done(YDUser user, AVException e) {
				callback.done(user, e);
			}
		});
	}

	public static interface FreightCallback {
		public void done(YDFreight freight, AVException e);
	}

	// ================================================================================
	// Property setters and getters
	// ================================================================================

	public YDAddress getAddress() {
		String addressId = this.getString("addressId");
		if (addressId != null) {
			YDAddress address = new YDAddress(addressId);
			return address;
		} else {
			return null;
		}
	}

	public void setAddress(YDAddress address) {
		if (address != null) {
			this.put("addressId", address.getObjectId());
		} else {
			Log.e("YDUser", "Address is null");
		}
	}

	public String getNote() {
		return this.getString("notes");
	}

	public void setNote(String notes) {
		this.put("notes", notes);
	}

	public int getStatus() {
		return this.getInt("status");
	}

	public void setStatus(int status) {
		this.put("status", status);
	}

	public String getTrackingNumber() {
		return this.getString("trackingNumber");
	}

	public void setTrackingNumber(String trackingNumber) {
		this.put("trackingNumber", trackingNumber);
	}

	public YDUser getUser() {
		return this.getAVUser("user", YDUser.class);
	}

	public void setUser(YDUser user) {
		this.put("user", user);
	}

	public float getWeight() {
		return this.getNumber("weight").floatValue();
	}

	public void setWeight(float weight) {
		this.put("weight", weight);
	}

	public float getExceedWeight() {
		return this.getNumber("exceedWeight").floatValue();
	}

	public void setExceedWeight(float weight) {
		this.put("exceedWeight", weight);
	}

}
