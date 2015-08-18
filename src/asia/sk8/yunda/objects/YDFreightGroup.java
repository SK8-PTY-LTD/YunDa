//================================================================================
//YDFreightGroup is a subclass of AVObject
//Class name: Address
//Author: Xujie Song
//Copyright: SK8 PTY LTD
//================================================================================

package asia.sk8.yunda.objects;

import asia.sk8.yunda.objects.YDUser.UserCallback;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;

@AVClassName("FreightGroup")
public class YDFreightGroup extends AVObject {

	// ================================================================================
	// Constructors
	// ================================================================================
	
	public YDFreightGroup() {
		
	}
	
	public YDFreightGroup(String objectId) {
		this.setObjectId(objectId);
	}
	
	// ================================================================================
	// Class properties
	// ================================================================================
	
	//Pending admin action
	public static int STATUS_DELIVERING = 500;
	//Pending chinese admin action
	public static int STATUS_PASSING_CUSTOM = 600;
	public static int STATUS_PASSED_CUSTOM = 610;

	// ================================================================================
	// Yunda Methods
	// ================================================================================

	// ================================================================================
	// Property setters and getters
	// ================================================================================

}
