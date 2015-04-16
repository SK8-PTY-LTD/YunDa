//================================================================================
// YDUser is a subclass of AV.Object
// Class name: _User
// Author: Xujie Song
//================================================================================

package asia.sk8.yunda.objects;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import asia.sk8.yunda.objects.YDAddress.AddressCallback;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;

@AVClassName("_User")
public class YDUser extends AVUser {

	// ================================================================================
	// Constructors
	// ================================================================================

	public YDUser() {
		
	}

	public YDUser(String userId) {
		this.setObjectId(userId);
	}

	// ================================================================================
	// Class properties
	// ================================================================================

	public static interface UserCallback {
		public void done(YDUser user, AVException e);
	}

	// ================================================================================
	// Shelf Methods
	// ================================================================================

	public boolean isUser(YDUser user) {
		if (user != null) {
			return this.getObjectId().equals(user.getObjectId());
		} else {
			return false;
		}
	}

	public boolean isNotUser(YDUser user) {
		return !this.isUser(user);
	}

	public boolean hasVerifiedEmail() {
		return this.getBoolean("emailVerified");
	}

	public boolean hasVerifiedMobileNumber() {
		String mobileNumber = this.getMobileNumber();
		return mobileNumber != null;
	}

	public void getAddressWithCallBack(final AddressCallback callback) {
		YDAddress address = this.getAddress();
		if (address != null) {
			address.fetchIfNeededInBackground(new GetCallback<AVObject>() {
				@Override
				public void done(AVObject address, AVException e) {
					callback.done((YDAddress) address, e);
				}
			});
		} else {
			AVException e = new AVException("Address is null", null);
			callback.done(null, e);
		}
	};

	public static YDUser getCurrentUser() {
		YDUser user = null;
		try {
			user = AVUser.getCurrentUser(YDUser.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
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

	public int getBalance() {
		return this.getInt("balance");
	}

	public void setBalance(int balance) {
		this.put("balance", balance);
	}

	public String getMobileNumber() {
		return this.getString("mobileNumber");
	}

	public void setMobileNumber(String number) {
		this.put("mobileNumber", number);
		this.put("mobilePhoneVerified", true);
		this.saveInBackground();
	}

	public AVFile getProfileImage() {
		return this.getAVFile("profileImage");
	}

	public void setProfileImage(Bitmap profileImage) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		profileImage.compress(CompressFormat.JPEG, 100, stream);
		AVFile Image = new AVFile("profile.jpg", stream.toByteArray());
		this.put("profileImage", Image);
		this.saveInBackground();
	}

	public String getProfileName() {
		return this.getString("profileName");
	}

	public void setProfileName(String profileName) {
		this.put("profileName", profileName);
	}

	public String getRealName() {
		return this.getString("realName");
	}

	public void setRealName(String realName) {
		this.put("realName", realName);
	}

	public String getReward() {
		return this.getString("reward");
	}

	public void setReward(String reward) {
		this.put("reward", reward);
	}

	// ================================================================================
	// Export class
	// ================================================================================

}
