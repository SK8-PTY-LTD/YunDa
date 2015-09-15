package asia.sk8.yunda;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

import android.app.Activity;  
import android.content.Intent;
import android.os.Bundle;  
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {  
    
      
    public void onCreate(Bundle savedInstanceState) {  
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_login);
       
       //Check currentUser 
       if (AVUser.getCurrentUser() != null) {
    	   Intent intent = new Intent(this, TabActivity.class);
    	   this.startActivity(intent);
    	   this.finish();
       }
       
       final EditText userId = (EditText)findViewById(R.id.userEditText);
       final EditText passWord = (EditText)findViewById(R.id.passWordEditText);
       Button LoginBtn = (Button)findViewById(R.id.loginButton);
       LoginBtn.setOnClickListener(new Button.OnClickListener(){ 
           @SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
           public void onClick(View v) {
        	   
               // TODO Auto-generated method stub
        	   String id = userId.getText().toString().trim();
        	   String pass = passWord.getText().toString().trim();

        	   if (id.matches("nqw0129@126.com")) {
        		   Toast.makeText(LoginActivity.this, "欢迎回来，超级管理员", Toast.LENGTH_LONG).show();
        	   } else if (id.matches("2300145467@qq.com")) {
        		   Toast.makeText(LoginActivity.this, "欢迎回来，超级管理员", Toast.LENGTH_LONG).show();
        	   } else if (id.matches("2973508779@qq.com")) {
        		   Toast.makeText(LoginActivity.this, "欢迎回来，管理员", Toast.LENGTH_LONG).show();
        	   } else if (id.matches("3327945015@qq.com")) {
        		   Toast.makeText(LoginActivity.this, "欢迎回来，管理员", Toast.LENGTH_LONG).show();
        	   } else if (id.matches("3060002587@qq.com")) {
        		   Toast.makeText(LoginActivity.this, "欢迎回来，管理员", Toast.LENGTH_LONG).show();
        	   } else {
        		   Toast.makeText(LoginActivity.this, "非管理员邮箱，无权登录", Toast.LENGTH_LONG).show();
        		   return;
        	   }
        	   
        	   AVUser.logInInBackground(id, pass, new LogInCallback() {
        		    public void done(AVUser user, AVException e) {
        		        if (user != null) {
        		            // µ«¬º≥…π¶
        		        	lanuchMainActivity();
        		        } else {
        		            // µ«¬º ß∞‹
        		        	Toast.makeText(getBaseContext(), "用户名或密码错误！", Toast.LENGTH_SHORT).show();
        		        }
        		    }
        		});
        	   
//        	   AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
//        	   query.whereEqualTo("username", id);
//        	   query.whereEqualTo("phone", pass);
//        	   query.findInBackground(new FindCallback<AVObject>() {/Users/XujieSong/Documents/Android projects/Shelf_Android/Shelf_Android/src/is/shelf/activities/LoginActivity.java
//        		   @Override
//        	       public void done(List<AVObject> avObjects, AVException e) {
//        	           if (e == null) {
//        	               if(avObjects.size()!=0)
//        	               {
//        	            	   
//        	            	   lanuchMainActivity();
//        	            	   Log.d("hehehe", ((AVObject) avObjects).getObjectId());
//        	               }
//        	               else
//        	               {
//        	            	   Toast.makeText(getBaseContext(), "User Id or Password wrong!", Toast.LENGTH_SHORT).show();
//        	               }
//        	           } else {
//        	               Log.d("fail", "error: " + e.getMessage());
//        	           }
//        	       }
//
//
//        	   });

        	   
           }         

       });     

        
    }  
      private void lanuchMainActivity(){
    	  Intent intent = new Intent(this, TabActivity.class);
    	  startActivity(intent);
    	  finish();
      }

      
}  


