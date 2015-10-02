package asia.sk8.yunda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import asia.sk8.Yunda;
import asia.sk8.yunda.objects.YDFreight;
import asia.sk8.yunda.objects.YDFreightIn;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

public class TabActivity extends Activity implements TabListener {
	public static int counter;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	ScanFragment scanFragment1;

	// ScanFragment scanFragment2;
	// ScanFragment scanFragment3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ActionBar actionBar = getActionBar();
		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// if (YDUser.getCurrentUser() != null) {
		// return;
		// } else {
		// Intent intent = new Intent(this, LoginActivity.class);
		// startActivity(intent);
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder backDialogBuilder = new AlertDialog.Builder(this);
		// set title
		backDialogBuilder.setTitle("提醒");
		// set dialog message
		backDialogBuilder
				.setMessage("确定要推出App嘛？")
				.setCancelable(false)
				.setPositiveButton("确认退出！",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								System.exit(0);
							}

						})
				.setNegativeButton("返回主界面",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

		AlertDialog alertDialog = backDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
	
	public void checkDeliveryId(final String deliveryId) {

		if (deliveryId != null) {
			//Check content


			Toast.makeText(TabActivity.this, "查询中...请稍后", Toast.LENGTH_LONG)
					.show();
			AVQuery<YDFreight> query0 = YDFreight.getQuery(YDFreight.class);
			query0.whereEqualTo("trackingNumber", deliveryId);
			AVQuery<YDFreight> query1 = YDFreight.getQuery(YDFreight.class);
			query1.whereEqualTo("YDNumber", deliveryId);
			
			List<AVQuery<YDFreight>> queries = new ArrayList<AVQuery<YDFreight>>();
			queries.add(query0);
			queries.add(query1);

			AVQuery<YDFreight> mainQuery = AVQuery.or(queries);
			mainQuery.include("user");
			
			mainQuery.findInBackground(new FindCallback<YDFreight>() {

				@Override
				public void done(List<YDFreight> list, AVException e) {
					if (e != null) {
						Toast.makeText(TabActivity.this,
								"错误；" + e.getLocalizedMessage(), Toast.LENGTH_LONG)
								.show();
						finish();
					} else {
						if (list.size() == 0) {
							// 自主入库/未入库 case
							Toast.makeText(TabActivity.this, "读取中...请稍后...",
									Toast.LENGTH_LONG).show();

							AVQuery<YDFreightIn> query = YDFreightIn
									.getQuery(YDFreightIn.class);
							query.whereEqualTo("trackingNumber", deliveryId);
							query.findInBackground(new FindCallback<YDFreightIn>() {
								@Override
								public void done(List<YDFreightIn> list,
										AVException e) {
									if (e != null) {
										Toast.makeText(TabActivity.this,
												"错误；" + e.getLocalizedMessage(),
												Toast.LENGTH_LONG).show();
//										finish();
									} else {
										if (list.size() == 0) {
											// 未 自主入库
											// Generate RKNumber
											long LOWER_RANGE = 1000000000L; // assign lower
																	// range value
											long UPPER_RANGE = 9999999999L; // assign
																		// upper
																		// range
																		// value
											Random random = new Random();
											long randomValue = LOWER_RANGE
													+ (long) (random.nextDouble() * (UPPER_RANGE - LOWER_RANGE));
											String RKNumber = "RK" + randomValue;
											// Generate new FreightIn
											final YDFreightIn freightIn = new YDFreightIn();
											freightIn.setStatus(YDFreightIn.STATUS_ARRIVED);
											freightIn.put("RKNumber", RKNumber);
											freightIn.setTrackingNumber(deliveryId);
											//Show option for FreightIn
											String[] array = new String[2];
											array[0] = "入库";
											array[1] = "取消";
											AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this);
											builder.setTitle("未入库快递：" + deliveryId).setItems(array,
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int position) {
															switch (position) {
															case 0:
																Intent intent = new Intent(
																		TabActivity.this,
																		FreightInActivity.class);

																//SDK Problem, use Static variable as workaround
																Yunda.tempObject = freightIn;
																//Proper way
//																intent.putExtra("freightIn", freightIn.toString());
																startActivity(intent);
																break;
															default:
																break;
															}
															}
														}).create().show();
										} else if (list.size() == 1) {
											//Do corresponding changes to FreightIn
											final YDFreightIn freightIn = list.get(0);
											if (freightIn.getStatus() == YDFreightIn.STATUS_CANCELLED) {
												// 入库后长时间未认领，取消入库状态的
												freightIn.setStatus(YDFreightIn.STATUS_ARRIVED);
												//Show option for FreightIn
												String[] array = new String[2];
												array[0] = "重新入库";
												array[1] = "取消";
												AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this);
												builder.setTitle("未认领重新入库：" + deliveryId).setItems(array,
														new DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog,
																	int position) {
																switch (position) {
																case 0:
																	Intent intent = new Intent(
																			TabActivity.this,
																			FreightInActivity.class);
																	//SDK Problem, use Static variable as workaround
																	Yunda.tempObject = freightIn;
																	//Proper way
//																	intent.putExtra("freightIn", freightIn.toString());
																	startActivity(intent);
																	break;
																default:
																	break;
																}
																}
															}).create().show();;
											} else if (freightIn.getStatus() == YDFreightIn.STATUS_MANUAL) {
												// 用户已自主入库
												freightIn.setStatus(YDFreightIn.STATUS_ARRIVED);
												//Show option for FreightIn
												String[] array = new String[2];
												array[0] = "入库";
												array[1] = "取消";
												AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this);
												builder.setTitle("已自助入库：" + deliveryId).setItems(array,
														new DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog,
																	int position) {
																switch (position) {
																case 0:
																	Intent intent = new Intent(
																			TabActivity.this,
																			FreightInActivity.class);
																	//SDK Problem, use Static variable as workaround
																	Yunda.tempObject = freightIn;
																	//Proper way
//																	intent.putExtra("freightIn", freightIn.toString());
																	startActivity(intent);
																	break;
																default:
																	break;
																}
																}
															}).create().show();;
											} else if (freightIn.getStatus() == YDFreightIn.STATUS_ARRIVED) {
												Toast.makeText(TabActivity.this,
														"包裹已入库，无需重复入库",
														Toast.LENGTH_LONG).show();
											} else if (freightIn.getStatus() == YDFreightIn.STATUS_PENDING_CHECK_PACKAGE) {
												Toast.makeText(TabActivity.this,
														"包裹已入库，等待开箱验货中",
														Toast.LENGTH_LONG).show();
											} else if (freightIn.getStatus() == YDFreightIn.STATUS_FINISHED_CHECK_PACKAGE) {
												Toast.makeText(TabActivity.this,
														"包裹已入库，验货已完成",
														Toast.LENGTH_LONG).show();
											} else if (freightIn.getStatus() == YDFreightIn.STATUS_CONFIRMED) {
												Toast.makeText(TabActivity.this,
														"包裹已入库，用户已确认入库",
														Toast.LENGTH_LONG).show();
											} else if (freightIn.getStatus() == YDFreightIn.STATUS_FINISHED) {
												Toast.makeText(TabActivity.this,
														"包裹已入库，用户已已生成运单，请 “打印运单“ ",
														Toast.LENGTH_LONG).show();
											} else {
												Toast.makeText(TabActivity.this,
														"错误；无效状态 包裹状态 详情垂询 技术",
														Toast.LENGTH_LONG).show();
//												finish();
											}
										} else {
											Toast.makeText(TabActivity.this,
													"错误；发现多个相同'转运号'，请查看后台",
													Toast.LENGTH_LONG).show();
//											finish();
										}
									}
								}
							});

						} else if (list.size() == 1) {
							final YDFreight freight = list.get(0);
							if (freight.getStatus() == YDFreight.STATUS_INITIALIZED) {
								// 普通包裹 case
								Toast.makeText(TabActivity.this, "普通包裹，待处理...",
										Toast.LENGTH_LONG).show();
								//Show option for Freight
								String[] array = new String[2];
								array[0] = "处理";
								array[1] = "取消";
								AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this);
								builder.setTitle("普通包裹：" + deliveryId).setItems(array,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int position) {
												switch (position) {
												case 0:
													Intent intent = new Intent(
															TabActivity.this,
															FreightActivity.class);
													//SDK Problem, use Static variable as workaround
													Yunda.tempObject = freight;
													//Proper way
//													intent.putExtra("freight", freight.toString());
													startActivity(intent);
													break;
												default:
													break;
												}
												}
											}).create().show();;
							} else if (freight.getStatus() == YDFreight.STATUS_SPEED_MANUAL) {
								// 原箱闪运 case
								Toast.makeText(TabActivity.this, "原箱闪运，待处理...",
										Toast.LENGTH_LONG).show();
								//Show option for Freight
								String[] array = new String[2];
								array[0] = "打印运单";
								array[1] = "取消";
								AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this);
								builder.setTitle("原箱闪运：" + deliveryId).setItems(array,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int position) {
												switch (position) {
												case 0:
													freight.setStatus(YDFreight.STATUS_INITIALIZED);
													freight.saveInBackground();
													Toast.makeText(TabActivity.this, "已入库，请至网页，打印运单...",
															Toast.LENGTH_LONG).show();
													break;
												default:
													break;
												}
												}
											}).create().show();;
							} else if (freight.getStatus() == YDFreight.STATUS_PENDING_FINISHED) {
								//先前扣款失败
								Toast.makeText(TabActivity.this, "重新发货中...",
										Toast.LENGTH_LONG).show();
								//Show option for Freight
								String[] array = new String[2];
								array[0] = "重新发货";
								array[1] = "取消";
								AlertDialog.Builder builder = new AlertDialog.Builder(TabActivity.this);
								builder.setTitle("发货失败：" + deliveryId).setItems(array,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int position) {
												switch (position) {
												case 0:
													Intent intent = new Intent(
															TabActivity.this,
															FreightActivity.class);
													//SDK Problem, use Static variable as workaround
													Yunda.tempObject = freight;
													//Proper way
//													intent.putExtra("freight", freight.toString());
													startActivity(intent);
													break;
												default:
													break;
												}
												}
											}).create().show();;
							} else if (freight.getStatus() == YDFreight.STATUS_PENDING_USER_ACTION) {
								Toast.makeText(TabActivity.this, "等待用户操作...",
										Toast.LENGTH_LONG).show();
								Intent intent = new Intent(
										TabActivity.this,
										ViewFreightActivity.class);
								//SDK Problem, use Static variable as workaround
								Yunda.tempObject = freight;
								//Proper way
//								intent.putExtra("freight", freight.toString());
								startActivity(intent);
							} else if (freight.getStatus() == YDFreight.STATUS_PENDING_DELIVERY) {
								Toast.makeText(TabActivity.this, "等待发货...",
										Toast.LENGTH_LONG).show();
								Intent intent = new Intent(
										TabActivity.this,
										ViewFreightActivity.class);
								//SDK Problem, use Static variable as workaround
								Yunda.tempObject = freight;
								//Proper way
//								intent.putExtra("freight", freight.toString());
								startActivity(intent);
							} else if (freight.getStatus() == YDFreight.STATUS_DELIVERING) {
								Toast.makeText(TabActivity.this, "已发货...",
										Toast.LENGTH_LONG).show();
								Intent intent = new Intent(
										TabActivity.this,
										ViewFreightActivity.class);
								//SDK Problem, use Static variable as workaround
								Yunda.tempObject = freight;
								//Proper way
//								intent.putExtra("freight", freight.toString());
								startActivity(intent);
							} else if (freight.getStatus() == YDFreight.STATUS_CANCELED) {
								Toast.makeText(TabActivity.this, "退货中...",
										Toast.LENGTH_LONG).show();
								Intent intent = new Intent(
										TabActivity.this,
										ViewFreightActivity.class);
								//SDK Problem, use Static variable as workaround
								Yunda.tempObject = freight;
								//Proper way
//								intent.putExtra("freight", freight.toString());
								startActivity(intent);
							} else {
								Toast.makeText(TabActivity.this, "其他状态...",
										Toast.LENGTH_LONG).show();
								Intent intent = new Intent(
										TabActivity.this,
										ViewFreightActivity.class);
								//SDK Problem, use Static variable as workaround
								Yunda.tempObject = freight;
								//Proper way
//								intent.putExtra("freight", freight.toString());
								startActivity(intent);
							}
						} else {
							Toast.makeText(TabActivity.this,
									"错误；发现相同'转运号'的多个 YD运单，请查看后台", Toast.LENGTH_LONG)
									.show();
//							finish();
						}
					}
				}

			});
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			// handle scan result
			String format = scanResult.getFormatName();
			final String deliveryId = scanResult.getContents();
			this.checkDeliveryId(deliveryId);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				if (scanFragment1 == null) {
					scanFragment1 = ScanFragment.newInstance(position);
				}
				return scanFragment1;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 1;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
	
	public void logout(MenuItem item) {
		AVUser.logOut();
		Intent intent = new Intent(this, LoginActivity.class);
		this.startActivity(intent);
		this.finish();
	}
	
	public void logOut(View view) {
		AVUser.logOut();
		Intent intent = new Intent(this, LoginActivity.class);
		this.startActivity(intent);
		this.finish();
	}

}
