//================================================================================
//SHAddress is a subclass of AVObject
//Class name: Address
//Author: Xujie Song
//================================================================================

package asia.sk8.yunda.objects;

import asia.sk8.yunda.objects.YDUser.UserCallback;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;

@AVClassName("Address")
public class YDAddress extends AVObject {

	// ================================================================================
	// Constructors
	// ================================================================================
	
	public YDAddress() {
		
	}
	
	public YDAddress(String objectId) {
		this.setObjectId(objectId);
	}

	public YDAddress(YDUser user) {
		if (user != null) {
			this.setUser(user);
			this.setRecipient(user.getRealName());
			this.setContactNumber(user.getMobileNumber());
		}
	}
	
	// ================================================================================
	// Class properties
	// ================================================================================

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

	public static interface AddressCallback {
		public void done(YDAddress address, AVException e);
	}

	// ================================================================================
	// Property setters and getters
	// ================================================================================

	public String getContactNumber() {
		return this.getString("contactNumber");
	}

	public void setContactNumber(String contactNumber) {
		this.put("contactNumber", contactNumber);
	}

	public String getCountry() {
		return this.getString("country");
	}

	public void setCountry(String country) {
		this.put("country", country);
	}

	public String getPostalCode() {
		return this.getString("postalCode");
	}

	public void setPostalCode(String postalCode) {
		this.put("postalCode", postalCode);
	}

	public String getRecipient() {
		return this.getString("recipient");
	}

	public void setRecipient(String recipient) {
		this.put("recipient", recipient);
	}

	public String getState() {
		return this.getString("state");
	}

	public void setState(String state) {
		this.put("state", state);
	}

	public String getSuburb() {
		return this.getString("suburb");
	}

	public void setSuburb(String suburb) {
		this.put("suburb", suburb);
	}

	public String getStreet1() {
		return this.getString("street1");
	}

	public void setStreet1(String street1) {
		this.put("street1", street1);
	}

	public String getStreet2() {
		return this.getString("street2");
	}

	public void setStreet2(String street2) {
		this.put("street2", street2);
	}

	public YDUser getUser() {
		return this.getAVUser("user", YDUser.class);
	}

	public void setUser(YDUser user) {
		this.put("user", user);
	}

}
