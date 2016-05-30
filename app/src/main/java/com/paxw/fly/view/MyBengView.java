package com.paxw.fly.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.paxw.fly.Constant.ConstantInfo;
import com.paxw.fly.R;
import com.paxw.fly.model.Role;
import com.paxw.fly.model.ScreenAttribute;
import com.paxw.fly.activity.ByronleeTeeterActivity;
import com.paxw.fly.activity.mainActivity;
import com.paxw.fly.bean.Footboard;
import com.paxw.fly.model.Food;
import com.paxw.fly.model.GameUi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



/**
 *
 */

public class MyBengView extends SurfaceView implements SurfaceHolder.Callback{

	private static final String HANDLE_MESSAGE_GAME_SCORE = "1";

	private BengTeeterThread mBengTeeterThread;

	private Context mContext;

	private Handler mHandler;

	private ScreenAttribute mScreenAttribute;

	private int mActionPower;

	private boolean mVibratorFlag;
	private Vibrator mVibrator;

	private boolean mSoundsFlag;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;

	private Bitmap mBackgroundImage;

	private Bitmap mRoleStandImage;
	private Bitmap mRoleDeadmanImage;
	private Bitmap mRoleFreefallImage1;
	private Bitmap mRoleFreefallImage2;
	private Bitmap mRoleFreefallImage3;
	private Bitmap mRoleFreefallImage4;
	private Bitmap mRoleMovingLeftImage1;
	private Bitmap mRoleMovingLeftImage2;
	private Bitmap mRoleMovingLeftImage3;
	private Bitmap mRoleMovingLeftImage4;
	private Bitmap mRoleMovingRightImage1;
	private Bitmap mRoleMovingRightImage2;
	private Bitmap mRoleMovingRightImage3;
	private Bitmap mRoleMovingRightImage4;

	private Bitmap mFootboardNormalImage;
	private Bitmap mFootboardUnstableImage1;
	private Bitmap mFootboardUnstableImage2;
	private Bitmap mFootboardSpringImage;
	private Bitmap mFootboardSpikedImage;
	private Bitmap mFootboardMovingLeftImage1;
	private Bitmap mFootboardMovingLeftImage2;
	private Bitmap mFootboardMovingRightImage1;
	private Bitmap mFootboardMovingRightImage2;

	private Bitmap mFoodImage1;
	private Bitmap mFoodImage2;
	private Bitmap mFoodImage3;
	private Bitmap mFoodImage4;
	private Bitmap mFoodImage5;
	private Bitmap mFoodImage6;
	private Bitmap mFoodImage7;
	private Bitmap mFoodImage8;

	private Drawable mTopBarImage;
	//生命值
	private Drawable mHpBarTotalImage;
	//生命值剩余
	private Drawable mHpBarRemainImage;

	private Paint mGameMsgRightPaint;
	private Paint mGameMsgLeftPaint;

	
	public MyBengView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				// 更新本地记录文件    handle message game score
				int curScore = message.getData().getInt(HANDLE_MESSAGE_GAME_SCORE);
				//更新分数
				boolean recordRefreshed = updateLocalRecord(curScore);

				LayoutInflater factory = LayoutInflater.from(mContext);
				View dialogView = factory.inflate(R.layout.score_post_panel, null);

				dialogView.setFocusableInTouchMode(true);
				dialogView.requestFocus();

				final TextView scoreTextView = (TextView) dialogView
						.findViewById(R.id.scorefield);
				scoreTextView.setText("你获得的分数为:"+curScore);
				final AlertDialog dialog = new AlertDialog.Builder(mContext)
						.setView(dialogView).create();
				//完成游戏的原因给不同的结果
				if (recordRefreshed) {
					dialog.setIcon(R.drawable.tip_new_record);
					dialog.setTitle(R.string.gameover_dialog_text_newrecord);
				} else {
					if (curScore < 100) {
						dialog.setIcon(R.drawable.tip_pool_guy);
						dialog.setTitle(R.string.gameover_dialog_text_poolguy);
					} else if (curScore < 500) {
						dialog.setIcon(R.drawable.tip_not_bad);
						dialog.setTitle(R.string.gameover_dialog_text_notbad);
					} else {
						dialog.setIcon(R.drawable.tip_awesome);
						dialog.setTitle(R.string.gameover_dialog_text_awesome);
					}
				}
				dialog.show();
				dialogView.findViewById(R.id.retry).setOnClickListener(
						new OnClickListener() {
							
							public void onClick(View v) {
								dialog.dismiss();
								restartGame();
							}
						});
				dialogView.findViewById(R.id.goback).setOnClickListener(
						new OnClickListener() {
						
							public void onClick(View v) {
								dialog.dismiss();
								((mainActivity) mContext).finish();
								mContext.startActivity(new Intent(mContext,ByronleeTeeterActivity.class));
								
							}
						});
			}
		};
		// 初始化资源
		Point point = new Point();
		((Activity)mContext).getWindowManager().getDefaultDisplay().getSize(point);
		mScreenAttribute = new ScreenAttribute(0,0,point.x,point.y);
		initRes();
		initGameUi(mScreenAttribute);
		;

		setFocusable(true);
	}
 
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// TODO Auto-generated method stub
		//游戏开始但是在这之前我应该把gameui初始化完成
		mBengTeeterThread = new BengTeeterThread(holder ,mContext,mHandler);
		mBengTeeterThread.setRunning(true);
		mBengTeeterThread.start();
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mBengTeeterThread.setRunning(false);
		mBengTeeterThread = null;
		
	}
	
	
	
	
	
	
	
	
	// 处理当屏幕重力感应后传递随重力感应变化而变化的x 的值，  这个函数是在mainActivity 里面在调用
	//这是属于View的 处理动态变化的x 的值
	// view 调用 他的 handleMoving（）；view.handleMoving() 里面有调用 GameUi里面的  handleMoving();
	public void handleMoving(float data_x_value) {
		if (mBengTeeterThread != null) {
			mBengTeeterThread.handleMoving(data_x_value);
		}
	}
	
	
	 //重启游戏
	public void restartGame() {
		mBengTeeterThread = new BengTeeterThread(this.getHolder(), this.getContext(),
				mHandler);
		initGameUi(mScreenAttribute);
		mBengTeeterThread.setRunning(true);
		mBengTeeterThread.start();
	}
	
	//更新游戏记录
	public boolean updateLocalRecord(int score) {
		//游戏记录
		SharedPreferences rankingSettings = mContext.getSharedPreferences(
				ConstantInfo.PREFERENCE_RANKING_INFO, 0);
		
		if (rankingSettings.getInt(ConstantInfo.PREFERENCE_KEY_RANKING_SCORE, 0) < score) {
			//如果现在的得分大于之前的记录，则跟新文件里面的游戏得分记录
			// FIXME: 2016/5/25 这里的记录不要更新最好是做成一个排行榜
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			rankingSettings.edit().putInt(
					ConstantInfo.PREFERENCE_KEY_RANKING_SCORE, score)
					.putString(ConstantInfo.PREFERENCE_KEY_RANKING_DATE,
							formatter.format(new Date())).commit();
			return true;
		}
		return false;
	}

	
	/**
	 * 初始化资源，也就是，对外界资源的引用全部赋值给设定的变量，
	 * 这样在后面就可以，利用外面的这些资源文件了，尤其是大量的图片
	 */
	private void initRes() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		mSoundsFlag = preferences.getBoolean(
				ConstantInfo.PREFERENCE_KEY_SOUNDS, true);
		mVibratorFlag = preferences.getBoolean(
				ConstantInfo.PREFERENCE_KEY_VIBRATE, true);
		mActionPower = preferences
				.getInt(ConstantInfo.PREFERENCE_KEY_POWER, 60);

		// 初始化活动音效
		soundPool = new SoundPool(10, AudioManager.STREAM_RING, 5);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(GameUi.EFFECT_FLAG_NORMAL, soundPool.load(
				getContext(), R.raw.normal, 1));
		soundPoolMap.put(GameUi.EFFECT_FLAG_UNSTABLE, soundPool.load(
				getContext(), R.raw.unstable, 1));
		soundPoolMap.put(GameUi.EFFECT_FLAG_SPRING, soundPool.load(
				getContext(), R.raw.spring, 1));
		soundPoolMap.put(GameUi.EFFECT_FLAG_SPIKED, soundPool.load(
				getContext(), R.raw.spiked, 1));
		soundPoolMap.put(GameUi.EFFECT_FLAG_MOVING, soundPool.load(
				getContext(), R.raw.moving, 1));
		soundPoolMap.put(GameUi.EFFECT_FLAG_TOOLS, soundPool.load(
				getContext(), R.raw.tools, 1));

		mGameMsgLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGameMsgLeftPaint.setColor(Color.YELLOW);
		mGameMsgLeftPaint.setStyle(Style.FILL);
		mGameMsgLeftPaint.setTextSize(15f);
		mGameMsgLeftPaint.setTextAlign(Paint.Align.LEFT);
		mGameMsgLeftPaint.setTypeface(Typeface.DEFAULT_BOLD);

		mGameMsgRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGameMsgRightPaint.setColor(Color.YELLOW);
		mGameMsgRightPaint.setStyle(Style.FILL);
		mGameMsgRightPaint.setTextSize(15f);
		mGameMsgRightPaint.setTextAlign(Paint.Align.RIGHT);
		mGameMsgRightPaint.setTypeface(Typeface.DEFAULT_BOLD);

		Resources res = mContext.getResources();

		mTopBarImage = res.getDrawable(R.drawable.hp_bar_total);
		mHpBarTotalImage = res.getDrawable(R.drawable.hp_bar_total);
		mHpBarRemainImage = res.getDrawable(R.drawable.hp_bar_remain);

		mRoleDeadmanImage = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_deadman),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleStandImage = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_standing),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleFreefallImage1 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_freefall1),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleFreefallImage2 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_freefall2),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleFreefallImage3 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_freefall3),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleFreefallImage4 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_freefall4),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingLeftImage1 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_left1),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingLeftImage2 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_left2),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingLeftImage3 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_left3),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingLeftImage4 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_left4),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingRightImage1 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_right1),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingRightImage2 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_right2),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingRightImage3 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_right3),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);
		mRoleMovingRightImage4 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.role_moving_right4),
				GameUi.ROLE_ATTRIBUTE_WIDTH, GameUi.ROLE_ATTRIBUTE_HEITH,
				true);

		mFootboardNormalImage = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_normal),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardUnstableImage1 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_unstable1),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardUnstableImage2 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_unstable2),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardSpringImage = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_spring),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardSpikedImage = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_spiked),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardMovingLeftImage1 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_moving_left1),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardMovingLeftImage2 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_moving_left2),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardMovingRightImage1 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_moving_right1),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);
		mFootboardMovingRightImage2 = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(res, R.drawable.footboard_moving_right2),
				GameUi.BORDER_ATTRIBUTE_IMAGE_WIDTH,
				GameUi.BORDER_ATTRIBUTE_IMAGE_HEITH, true);

		mFoodImage1 = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(res, R.drawable.food_1),
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mFoodImage2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				res, R.drawable.food_2), GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mFoodImage3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				res, R.drawable.food_3), GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mFoodImage4 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				res, R.drawable.food_4), GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mFoodImage5 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				res, R.drawable.food_5), GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mFoodImage6 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				res, R.drawable.food_6), GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mFoodImage7 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				res, R.drawable.food_7), GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mFoodImage8 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				res, R.drawable.food_8), GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE,
				GameUi.FOOD_ATTRIBUTE_IMAGE_SIZE, true);
		mBackgroundImage = BitmapFactory
				.decodeResource(res, R.drawable.b_bg);

	}
	
	
	
	
	
	private GameUi mGameUi;
	//初始化，游戏的 gameUi
	public void initGameUi(ScreenAttribute screenAttribut) {
		mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
				screenAttribut.maxX, screenAttribut.maxY, true);
		if (mGameUi != null) {
			if (mBengTeeterThread != null) {
				//让游戏停下来
				mBengTeeterThread.setRunning(false);
			}
			mGameUi.destroy();
		}
		int addVelocity = 0;
		if (mActionPower < 10) {
			addVelocity = -2;
		} else if (mActionPower < 25) {
			addVelocity = -1;
		} else if (mActionPower < 50) {
			addVelocity = 0;
		} else if (mActionPower < 60) {
			addVelocity = 1;
		} else if (mActionPower < 70) {
			addVelocity = 2;
		} else if (mActionPower < 80) {
			addVelocity = 3;
		} else if (mActionPower < 90) {
			addVelocity = 4;
		} else {
			addVelocity = 5;
		}
		mGameUi = new GameUi(screenAttribut, addVelocity);
	}
	
  class BengTeeterThread extends Thread{
		
		//对sufceVIew   的控制
		private SurfaceHolder mSurfaceHolder;
		private Context mContext;
		private Handler mHandler;
		
		
		// 运行标志
		private boolean mRun = true;
		// 游戏UI模型实例
//		private GameUi mGameUi;
		// 时间记录器
		private long mTimeLogger;
		
		public BengTeeterThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			this.mSurfaceHolder = surfaceHolder;
			this.mContext = context;
			this.mHandler = handler;
		}
		//就是不停画画布
		public void run(){
			while (mRun) {
				Canvas canvas = null;
				try {
					mTimeLogger = System.currentTimeMillis();
					try {
						mGameUi.updateGameUi();
						canvas = mSurfaceHolder.lockCanvas(null);
						synchronized (mSurfaceHolder) {
							doDraw(canvas);
						}
						handleEffect(mGameUi.getEffectFlag());
					} catch (Exception e) {
						Log.v("", "Error at 'run' method", e);
					} finally {
						if (canvas != null) {
							mSurfaceHolder.unlockCanvasAndPost(canvas);
						}
					}
					mTimeLogger = System.currentTimeMillis() - mTimeLogger;
					//小于刷新的毫秒
					if (mTimeLogger < GameUi.GAME_ATTRIBUTE_FRAME_DELAY) {
						Thread.sleep(GameUi.GAME_ATTRIBUTE_FRAME_DELAY
								- mTimeLogger);
					}
					if (mGameUi.mGameStatus == GameUi.GAME_STATUS_GAMEOVER) {
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putInt(MyBengView.HANDLE_MESSAGE_GAME_SCORE,
								mGameUi.getScore());
						message.setData(bundle);
						mHandler.sendMessage(message);
						mRun = false;
					}
				} catch (Exception ex) {
					Log.d("", "Error at 'run' method", ex);
				}
			}
		}
		
		//do draw ,绘画 游戏界面
		private void doDraw(Canvas canvas) {

			Bitmap tempBitmap = null;
			canvas.drawBitmap(mBackgroundImage, 0, 0, null);
//			mTopBarImage.setBounds(0, 0,
//					MyBengView.this.mScreenAttribute.maxX,15);
//			mTopBarImage.draw(canvas);
			List<Footboard> footboards = mGameUi.getFootboardUIObjects();
			for (Footboard footboard : footboards) {
				switch (footboard.getType()) {
				case GameUi.FOOTBOARD_TYPE_UNSTABLE:
					if (!footboard.isMarked()) {
						tempBitmap = mFootboardUnstableImage1;
					} else {
						tempBitmap = mFootboardUnstableImage2;
					}
					break;
				case GameUi.FOOTBOARD_TYPE_SPRING:
					tempBitmap = mFootboardSpringImage;
					break;
				case GameUi.FOOTBOARD_TYPE_SPIKED:
					tempBitmap = mFootboardSpikedImage;
					break;
				case GameUi.FOOTBOARD_TYPE_MOVING_LEFT:
					if (footboard.nextFrame() == 0) {
						tempBitmap = mFootboardMovingLeftImage1;
					} else {
						tempBitmap = mFootboardMovingLeftImage2;
					}
					break;
				case GameUi.FOOTBOARD_TYPE_MOVING_RIGHT:
					if (footboard.nextFrame() == 0) {
						tempBitmap = mFootboardMovingRightImage1;
					} else {
						tempBitmap = mFootboardMovingRightImage2;
					}
					break;
				default:
					tempBitmap = mFootboardNormalImage;
				}
				canvas.drawBitmap(tempBitmap, footboard.getMinX(), footboard
						.getMinY(), null);
			}

			Role role = mGameUi.getRoleUIObject();
			if (mGameUi.mGameStatus == GameUi.GAME_STATUS_GAMEOVER) {
				canvas.drawBitmap(mRoleDeadmanImage, role.getMinX(), role
						.getMinY(), null);
			} else {
				switch (role.getRoleSharp()) {
				case GameUi.ROLE_SHARP_FREEFALL_NO1:
					tempBitmap = mRoleFreefallImage1;
					break;
				case GameUi.ROLE_SHARP_FREEFALL_NO2:
					tempBitmap = mRoleFreefallImage2;
					break;
				case GameUi.ROLE_SHARP_FREEFALL_NO3:
					tempBitmap = mRoleFreefallImage3;
					break;
				case GameUi.ROLE_SHARP_FREEFALL_NO4:
					tempBitmap = mRoleFreefallImage4;
					break;
				case GameUi.ROLE_SHARP_MOVE_LEFT_NO1:
					tempBitmap = mRoleMovingLeftImage1;
					break;
				case GameUi.ROLE_SHARP_MOVE_LEFT_NO2:
					tempBitmap = mRoleMovingLeftImage2;
					break;
				case GameUi.ROLE_SHARP_MOVE_LEFT_NO3:
					tempBitmap = mRoleMovingLeftImage3;
					break;
				case GameUi.ROLE_SHARP_MOVE_LEFT_NO4:
					tempBitmap = mRoleMovingLeftImage4;
					break;
				case GameUi.ROLE_SHARP_MOVE_RIGHT_NO1:
					tempBitmap = mRoleMovingRightImage1;
					break;
				case GameUi.ROLE_SHARP_MOVE_RIGHT_NO2:
					tempBitmap = mRoleMovingRightImage2;
					break;
				case GameUi.ROLE_SHARP_MOVE_RIGHT_NO3:
					tempBitmap = mRoleMovingRightImage3;
					break;
				case GameUi.ROLE_SHARP_MOVE_RIGHT_NO4:
					tempBitmap = mRoleMovingRightImage4;
					break;
				default:
					tempBitmap = mRoleStandImage;
				}
				canvas.drawBitmap(tempBitmap, role.getMinX(), role.getMinY(),
						null);
			}

			Food food = mGameUi.getFood();
			if (food.mFoodReward != GameUi.FOOD_NONE && food.mTimeCounter > 0) {
				switch (food.mFoodReward) {
				case GameUi.FOOD_1:
					tempBitmap = mFoodImage1;
					break;
				case GameUi.FOOD_2:
					tempBitmap = mFoodImage2;
					break;
				case GameUi.FOOD_3:
					tempBitmap = mFoodImage3;
					break;
				case GameUi.FOOD_4:
					tempBitmap = mFoodImage4;
					break;
				case GameUi.FOOD_5:
					tempBitmap = mFoodImage5;
					break;
				case GameUi.FOOD_6:
					tempBitmap = mFoodImage6;
					break;
				case GameUi.FOOD_7:
					tempBitmap = mFoodImage7;
					break;
				case GameUi.FOOD_8:
					tempBitmap = mFoodImage8;
					break;
				}
				canvas.drawBitmap(tempBitmap, food.mMinX, food.mMinY, null);
			}

			
            canvas.drawText(mGameUi.getScoreStr(), (float) 10,50,mGameMsgLeftPaint);
					
			
			//画生命值的总长度
			mHpBarTotalImage.setBounds(  //public void setBounds (int left, int top, int right, int bottom)表示四个顶点坐标
					0,0, MyBengView.this.mScreenAttribute.maxX,15);
			mHpBarTotalImage.draw(canvas);

			//画生命条
//			Paint paint = new Paint();
//			paint.setStyle(Paint.Style.STROKE);
//			paint.setStrokeWidth(5);
//			paint.setColor(Color.parseColor("#cc000000"));//设置画笔的颜色
//
//			canvas.drawLine(0,100,MyBengView.this.mScreenAttribute.maxX,100,paint);
//			paint.setColor(Color.parseColor("#ccFF0000"));
//			canvas.drawLine(0,100,MyBengView.this.mScreenAttribute.maxX * mGameUi.getHp(),100,paint);
                //画剩余生命的值
			mHpBarRemainImage
					.setBounds(0, 0, (int) (MyBengView.this.mScreenAttribute.maxX * mGameUi.getHp()), 15);
			mHpBarRemainImage.draw(canvas);
                 
			//右上角显示游戏等级
			canvas.drawText(mGameUi.getLevel(),					
					(float) (MyBengView.this.mScreenAttribute.maxX - 5),50,mGameMsgRightPaint);
		}
		
		

		
		
		
		
		// 处理当屏幕重力感应后传递随重力感应变化而变化的x 的值， 
		//这是线程的 处理移动
		public void handleMoving(float data_x_Value) {
			if (mGameUi != null) {
				mGameUi.handleMoving(data_x_Value);
			}
		}
		
		//处理音效文件
		private void handleEffect(int effectFlag) {
			if (effectFlag == mGameUi.EFFECT_FLAG_NO_EFFECT)
				return;
			// 处理音效
			if (mSoundsFlag) {
				playSoundEffect(effectFlag);
			}
			// 处理震动
			if (mVibratorFlag) {
				if (mVibrator == null) {
					mVibrator = (Vibrator) mContext
							.getSystemService(Context.VIBRATOR_SERVICE);
				}
				mVibrator.vibrate(25);
			}
		}
		
		
		/**
		 * 播放音效
		 * 
		 * @param soundId
		 * 根据分析的音效效果，进行播放不同的音效
		 */
		private void playSoundEffect(int soundId) {
			try {
				AudioManager mgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
				 //获取当前声音大小
				float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_RING);
				  //获取系统最大声音大小
				float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_RING);
				
				float volume = streamVolumeCurrent / streamVolumeMax;
				
				soundPool.play(soundPoolMap.get(soundId), volume, volume, 1, 0,
						1f);
			} catch (Exception e) {
				Log.d("PlaySounds", e.toString());
			}
		}
		
		
		//设置控制线程开始或结束的变量
		public void setRunning(boolean run) {
			mRun = run;
		}
	}
		
}
	
