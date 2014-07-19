package com.yixianqian.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yixianqian.R;
import com.yixianqian.base.BaseApplication;
import com.yixianqian.base.BaseV4Fragment;
import com.yixianqian.config.DefaultKeys;
import com.yixianqian.db.CityDbService;
import com.yixianqian.db.ProvinceDbService;
import com.yixianqian.db.SchoolDbService;
import com.yixianqian.entities.City;
import com.yixianqian.entities.Province;
import com.yixianqian.entities.School;
import com.yixianqian.utils.UserPreference;

/**
 * �����ƣ�RegSchoolFragment
 * ��������ѧУѡ��ҳ��
 * �����ˣ� ��˧
 * ����ʱ�䣺2014��7��6�� ����7:56:17
 *
 */
public class RegSchoolFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// ��View
	private TextView topNavigation;//����������
	private View leftImageButton;//��������ఴť
	private View rightImageButton;//�������Ҳఴť
	private Spinner mProvinceView;//ʡ
	private Spinner mCityView;//����
	private Spinner mSchoolView;//ѧУ

	public SharedPreferences locationPreferences;// ��¼�û�λ��
	private UserPreference userPreference;
	private String mProvince = "";
	private String mCity = "";
	private Province currentProvince;
	private City currentCity;
	private School currentSchool;

	private List<Province> provinceList;//ʡ���б�
	private List<String> provinceNameList;//ʡ�������б�
	private List<City> cityList;//�����б�
	private List<String> cityNameList;//���������б�
	private List<School> schoolList;//ѧУ�б�
	private List<String> schoolNameList;//ѧУ�����б�

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		
		//��ȡ�û�λ��
		locationPreferences = getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
		mProvince = locationPreferences.getString(DefaultKeys.USER_PROVINCE, "");
		mCity = locationPreferences.getString(DefaultKeys.USER_CITY, "");

		currentProvince = new Province();
		currentCity = new City();
		currentSchool = new School();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_school, container, false);
		findViewById();// ��ʼ��views
		initView();
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
		leftImageButton = (View) getActivity().findViewById(R.id.left_btn_bg);
		rightImageButton = (View) getActivity().findViewById(R.id.right_btn_bg);
		mProvinceView = (Spinner) rootView.findViewById(R.id.province);
		mCityView = (Spinner) rootView.findViewById(R.id.city);
		mSchoolView = (Spinner) rootView.findViewById(R.id.school);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("ѧУ");
		rightImageButton.setEnabled(false);

		//��ʼ��ʡ����Ϣ
		initProvinceData();

		//���ʡ��ʱ��ʼ����Ӧ����
		mProvinceView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				cityList = CityDbService.getInstance(getActivity()).getCityListByProvince(provinceList.get(position));
				cityNameList = new ArrayList<String>();
				if (cityList != null) {
					for (City city : cityList) {
						cityNameList.add(city.getCityName());
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_dropdown_item, cityNameList);
					mCityView.setAdapter(adapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		//�������ʱ��ʼ����ӦѧУ
		mCityView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				currentCity = cityList.get(position);

				schoolList = new ArrayList<School>();
				schoolNameList = new ArrayList<String>();
				schoolList = SchoolDbService.getInstance(getActivity()).getSchoolListByCity(cityList.get(position));

				if (schoolList != null) {
					for (School school : schoolList) {
						schoolNameList.add(school.getSchoolName());
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_spinner_dropdown_item, schoolNameList);
					mSchoolView.setAdapter(adapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		//ѡѧУʱ
		mSchoolView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				currentSchool = schoolList.get(position);
				rightImageButton.setEnabled(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});
		rightImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptSchool();
			}
		});

		//��ʼ��������Ϣ
		initCityData();
	}

	/**
	 * ��ʼ��ʡ����Ϣ
	 */
	private void initProvinceData() {
		int currentPostion = 0;//��ǰλ��
		provinceList = new ArrayList<Province>();
		provinceNameList = new ArrayList<String>();
		provinceList = ProvinceDbService.getInstance(getActivity()).provinceDao.loadAll();
		if (provinceList != null) {
			for (int i = 0; i < provinceList.size(); i++) {
				provinceNameList.add(provinceList.get(i).getProvinceName());
				if (provinceList.get(i).getProvinceName().contains(mProvince)
						|| mProvince.contains(provinceList.get(i).getProvinceName())) {
					currentProvince = provinceList.get(i);
					currentPostion = i;
				}
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, provinceNameList);
		mProvinceView.setAdapter(adapter);
		mProvinceView.setSelection(currentPostion, true);
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	private void initCityData() {
		int currentPostion = 0;//��ǰλ��
		if (currentProvince != null) {
			cityList = CityDbService.getInstance(getActivity()).getCityListByProvince(currentProvince);
			cityNameList = new ArrayList<String>();
			if (cityList != null) {
				for (int i = 0; i < cityList.size(); i++) {
					cityNameList.add(cityList.get(i).getCityName());
					if (cityList.get(i).getCityName().contains(mCity) || mCity.contains(cityList.get(i).getCityName())) {
						currentCity = cityList.get(i);
						currentPostion = i;
					}
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_spinner_dropdown_item, cityNameList);
				mCityView.setAdapter(adapter);
				mCityView.setSelection(currentPostion, true);
			}
		}

	}

	/**
	 * ���ѧУ
	 */
	private void attemptSchool() {

		boolean cancel = false;

		//����Ƿ�ѡ��ʡ
		if (mProvinceView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "��ѡ��ʡ", 1).show();
		}

		//����Ƿ�ѡ����
		if (mCityView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "��ѡ�����ڳ���", 1).show();
		}
		//����Ƿ�ѡѧУ
		if (mSchoolView.getSelectedItem().toString().length() == 0) {
			cancel = true;
			Toast.makeText(getActivity(), "��ѡ������ѧУ", 1).show();
		}

		if (!cancel) {
			// û�д���
			userPreference.setU_provinceid(currentProvince.getProvinceID().intValue());
			userPreference.setU_cityid(currentCity.getCityID().intValue());
			userPreference.setU_schoolid(currentSchool.getId().intValue());
			userPreference.setU_address(locationPreferences.getString(DefaultKeys.USER_DETAIL_LOCATION, ""));

			RegPhoneFragment phoneFragment = new RegPhoneFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in,
					R.anim.push_right_out);
			transaction.replace(R.id.fragment_container, phoneFragment);
			//			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
}
