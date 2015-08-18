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

@AVClassName("Transaction")
public class YDTransaction extends AVObject {

	// ================================================================================
	// Constructors
	// ================================================================================
	
	public YDTransaction() {
		
	}
	
	public YDTransaction(String objectId) {
		this.setObjectId(objectId);
	}

	public YDTransaction(YDUser user) {
		if (user != null) {
			this.setUser(user);
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

	public static interface TransactionCallback {
		public void done(YDTransaction transaction, AVException e);
	}

	// ================================================================================
	// Property setters and getters
	// ================================================================================

	public float getAmount() {
		return this.getNumber("amount").floatValue();
	}

	public void setAmount(float amount) {
		this.put("amount", amount);
	}

	public YDUser getUser() {
		return this.getAVUser("user", YDUser.class);
	}

	public void setUser(YDUser user) {
		this.put("user", user);
	}

}
