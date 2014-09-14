package com.yixianqian.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixianqian.R;
import com.yixianqian.base.BaseActivity;
import com.yixianqian.config.Constants;
import com.yixianqian.db.CityDbService;
import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.db.SchoolDbService;
import com.yixianqian.jsonobject.JsonUser;
import com.yixianqian.utils.DateTimeTools;

/**
 * 类名称：PersonMoreDetailActivity
 * 类描述：PersonDetailActivity点击详细资料进入该页面
 * 创建人： 张帅
 * 创建时间：2014年9月9日 下午9:48:50
 *
 */
public class PersonMoreDetailActivity extends BaseActivity {
	private JsonUser jsonUser;
	private TextView topNavigation;//导航栏文字
	private View leftImageButton;//导航栏左侧按钮
	private View rightImageButton;//导航栏右侧按钮
	private TextView nickNameTextView;
	private TextView genderText;
	private TextView birthdayTextView;
	private ImageView vertifyView;
	private TextView ageTextView;
	private TextView heightTextView;
	private TextView weightTextView;
	private TextView constellTextView;
	private TextView introTextView;
	private TextView provinceTextView;
	private TextView cityTextView;
	private TextView schoolTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_more_detail);
		jsonUser = (JsonUser) getIntent().getSerializableExtra("user");

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		genderText = (TextView) findViewById(R.id.gender);
		ageTextView = (TextView) findViewById(R.id.age);
		heightTextView = (TextView) findViewById(R.id.height);
		weightTextView = (TextView) findViewById(R.id.weight);
		constellTextView = (TextView) findViewById(R.id.constell);
		introTextView = (TextView) findViewById(R.id.personIntro);
		birthdayTextView = (TextView) findViewById(R.id.birthday);
		vertifyView = (ImageView) findViewById(R.id.vertify);
		nickNameTextView = (TextView) findViewById(R.id.nickname);
		provinceTextView = (TextView) findViewById(R.id.province);
		cityTextView = (TextView) findViewById(R.id.city);
		schoolTextView = (TextView) findViewById(R.id.school);
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText(jsonUser.getU_nickname());
		rightImageButton.setVisibility(View.GONE);

		if (jsonUser != null) {
			//如果通过认证
			if (jsonUser.getU_vertify_image_pass() == Constants.VertifyState.PASSED) {
				vertifyView.setImageResource(R.drawable.already_vertify);
			} else if (jsonUser.getU_vertify_image_pass() == Constants.VertifyState.VERTIFING) {
				vertifyView.setImageResource(R.drawable.onvertify);
			} else {
				vertifyView.setImageResource(R.drawable.novertify);
			}

			if (jsonUser.getU_age() > 0) {
				ageTextView.setText("" + jsonUser.getU_age());
			}

			if (jsonUser.getU_height() > 0) {
				heightTextView.setText("" + jsonUser.getU_height());
			}

			if (jsonUser.getU_weight() > 0) {
				weightTextView.setText("" + jsonUser.getU_weight());
			}

			//设置生日
			if (jsonUser.getU_birthday() != null) {
				birthdayTextView.setText(DateTimeTools.getDateString(jsonUser.getU_birthday()));
			}

			//设置星座
			if (!TextUtils.isEmpty(jsonUser.getU_constell())) {
				constellTextView.setText(jsonUser.getU_constell());
			}

			if (!TextUtils.isEmpty(jsonUser.getU_introduce())) {
				introTextView.setText(jsonUser.getU_introduce());
			}

			genderText.setText(jsonUser.getU_gender());

			nickNameTextView.setText(jsonUser.getU_nickname());

			provinceTextView.setText(ProvinceDbService.getInstance(PersonMoreDetailActivity.this).getProNameById(
					jsonUser.getU_provinceid()));
			cityTextView.setText(CityDbService.getInstance(PersonMoreDetailActivity.this).getCityNameById(
					jsonUser.getU_cityid()));
			schoolTextView.setText(SchoolDbService.getInstance(PersonMoreDetailActivity.this).getSchoolNameById(
					jsonUser.getU_schoolid()));

		}

		leftImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
