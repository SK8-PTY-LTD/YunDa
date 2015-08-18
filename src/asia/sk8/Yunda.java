package asia.sk8;

import android.app.Application;
import android.widget.Toast;
import asia.sk8.yunda.FreightInActivity;
import asia.sk8.yunda.objects.YDAddress;
import asia.sk8.yunda.objects.YDFreight;
import asia.sk8.yunda.objects.YDFreightGroup;
import asia.sk8.yunda.objects.YDFreightIn;
import asia.sk8.yunda.objects.YDTransaction;
import asia.sk8.yunda.objects.YDUser;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.RefreshCallback;

//import com.avos.avoscloud.AVAnalytics;

public class Yunda extends Application {
	
	public static AVObject setting;
	public static AVObject tempObject;
	
	public void onCreate() {
		super.onCreate();
		AVObject.registerSubclass(YDAddress.class);
		AVObject.registerSubclass(YDFreight.class);
		AVObject.registerSubclass(YDFreightGroup.class);
		AVObject.registerSubclass(YDFreightIn.class);
		AVObject.registerSubclass(YDTransaction.class);
		AVObject.registerSubclass(YDUser.class);
		
		AVOSCloud.initialize(this,
				"umouw51mkumgpt72hhir61xemo3b7q2n5js0zce3b96by895",
				"svsw3nybfcax9ssw7czti2fk86ak9gp6ekrb00essagscyrg");
		
		setting = new AVObject("SystemSetting");
		setting.setObjectId("557a8a2fe4b0fe935ead7847");
		
		setting.refreshInBackground(new RefreshCallback<AVObject>() {

			@Override
			public void done(AVObject s, AVException e) {
				if (e != null) {
					Toast.makeText(Yunda.this,
							"网络错误，请链接网络重试： " + e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
					throw new RuntimeException("下载系统设置文件错误 " + e.getLocalizedMessage());
				} else {
					setting = s;
				}
				
			}
			
		});
		
	}
}