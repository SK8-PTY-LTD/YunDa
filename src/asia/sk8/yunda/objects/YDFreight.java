//================================================================================
//YDFreight is a subclass of AVObject
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

@AVClassName("Freight")
public class YDFreight extends AVObject {

	// ================================================================================
	// Constructors
	// ================================================================================
	
	public YDFreight() {
		this.setStatus(YDFreight.STATUS_INITIALIZED);
	}
	
	public YDFreight(String objectId) {
		this.setObjectId(objectId);
	}

	public YDFreight(YDFreightIn freightIn) {
		if (freightIn != null) {
			this.setUser(freightIn.getUser());
			this.setAddress(freightIn.getUser().getAddress());
			this.setFreightIn(freightIn);
		}
	}
	
	// ================================================================================
	// Class properties
	// ================================================================================
	
	public static int STATUS_INITIALIZED = 0;

	//Pending user action

	public static int STATUS_PENDING_USER_ACTION = 100;

	public static int STATUS_SPEED_MANUAL = 110;

	//Pending admin action

	public static int STATUS_PENDING_SPLIT_PACKAGE = 200;

	public static int STATUS_PENDING_SPLIT_PACKAGE_PREMIUM = 210;

	public static int STATUS_PENDING_REDUCE_WEIGHT = 220;

	public static int STATUS_PENDING_EXTRA_PACKAGING = 230;

	public static int STATUS_PENDING_CHECK_PACKAGE = 240;

	public static int STATUS_PENDING_MERGE_PACKAGE = 250;

	public static int STATUS_PENDING_PAY_INSURANCE = 260;


	//Completed admin action


	public static int STATUS_CONFIRMED_SPLIT_PACKAGE = 205;

	public static int STATUS_CONFIRMED_SPLIT_PACKAGE_PREMIUM = 215;

	public static int STATUS_CONFIRMED_REDUCE_WEIGHT = 225;

	public static int STATUS_CONFIRMED_EXTRA_PACKAGING = 235;

	public static int STATUS_CONFIRMED_CHECK_PACKAGE = 245;

	public static int STATUS_CONFIRMED_MERGE_PACKAGE = 255;

	public static int STATUS_CONFIRMED_PAY_INSURANCE = 265;


	//Pending admin delivery action

	public static int STATUS_PENDING_FINISHED = 400;

	public static int STATUS_PENDING_DELIVERY = 500;

	public static int STATUS_DELIVERING = 510;

	public static int STATUS_PASSING_CUSTOM = 600;

	public static int STATUS_FINAL_DELIVERY = 610;

	public static int STATUS_DELIVERED = 690;

	//return goods; cancel

	public static int STATUS_CANCELED = 990;

	// ================================================================================
	// Yunda Methods
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
	
	public boolean hasPaidInsurance() {
		return this.getBoolean("insurance");
	}
	
	public boolean hasPaidTaxInsurance() {
		return this.getBoolean("taxInsurance");
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

	public float getExceedWeight() {
		return this.getNumber("exceedWeight").floatValue();
	}

	public void setExceedWeight(float exceedWeight) {
		this.put("exceedWeight", exceedWeight);
	}

	public YDFreightIn getFreightIn() {
		return (YDFreightIn) this.getAVObject("freightIn");
	}

	public void setFreightIn(YDFreightIn freightIn) {
		this.put("freightIn", freightIn);
	}

	public YDFreightGroup getFreightGroup() {
		return (YDFreightGroup) this.getAVObject("freightGroup");
	}

	public void setFreightGroup(YDFreightGroup freightGroup) {
		this.put("freightGroup", freightGroup);
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

	public float getFinalWeight() {
		return this.getNumber("finalWeight").floatValue();
	}

	public void setFinaleight(float weight) {
		this.put("finalWeight", weight);
	}

	public String getYDNumber() {
		return this.getString("YDNumber");
	}

	public void setYDNumber(String notes) {
		this.put("YDNumber", notes);
	}

	public String getRKNumber() {
		return this.getString("RKNumber");
	}

	public void setRKNumber(String notes) {
		this.put("RKNumber", notes);
	}

}
