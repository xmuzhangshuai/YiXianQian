package com.yixianqian.daogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class Generator {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Schema schema = new Schema(1, "com.yixianqian.entities");
		schema.setDefaultJavaPackageDao("com.yixianqian.dao");
		schema.enableKeepSectionsByDefault();
		addData(schema);
		new DaoGenerator().generateAll(schema, "../YiXianQian/src-models");
	}

	private static void addData(Schema schema) {
		/****添加省份*******/
		Entity provinceEntity = schema.addEntity("Province");
		provinceEntity.addIdProperty();
		provinceEntity.addLongProperty("provinceID");
		provinceEntity.addStringProperty("provinceName");

		/****添加城市*******/
		Entity cityEntity = schema.addEntity("City");
		cityEntity.addIdProperty();
		cityEntity.addLongProperty("cityID");
		cityEntity.addStringProperty("cityName");
		//省份外键
		Property city_province_id = cityEntity.addLongProperty("provinceID").notNull().getProperty();
		cityEntity.addToOne(provinceEntity, city_province_id);
		ToMany provinceToCity = provinceEntity.addToMany(cityEntity, city_province_id);
		provinceToCity.setName("cityList");

		/****添加主管部门*******/
		Entity cometentEntity = schema.addEntity("Cometent");
		cometentEntity.addIdProperty();
		cometentEntity.addStringProperty("cometentName");

		/****添加学校*******/
		Entity schoolEntity = schema.addEntity("School");
		schoolEntity.addIdProperty();
		schoolEntity.addStringProperty("schoolName");
		schoolEntity.addStringProperty("schoolInfo");
		//主管部门外键
		Property school_cometent_idProperty = schoolEntity.addLongProperty("cometentID").notNull().getProperty();
		schoolEntity.addToOne(cometentEntity, school_cometent_idProperty);
		ToMany cometentToSchool = cometentEntity.addToMany(schoolEntity, school_cometent_idProperty);
		cometentToSchool.setName("schoolList");
		//城市外键
		Property school_city_id = schoolEntity.addLongProperty("cityID").notNull().getProperty();
		schoolEntity.addToOne(cityEntity, school_city_id);
		ToMany cityToSchool = cityEntity.addToMany(schoolEntity, school_city_id);
		cityToSchool.setName("schoolList");

		/******用户状态*****/
		Entity userStateEntity = schema.addEntity("UserState");
		userStateEntity.addIdProperty();
		userStateEntity.addStringProperty("userStateName");

		/******用户职业*****/
		Entity vocationEntity = schema.addEntity("Vocation");
		vocationEntity.addIdProperty();
		vocationEntity.addStringProperty("vocationName");

		/******每日推荐*****/
		Entity todayRecommendEntity = schema.addEntity("TodayRecommend");
		todayRecommendEntity.addIdProperty().autoincrement();
		todayRecommendEntity.addIntProperty("userID");
		todayRecommendEntity.addStringProperty("userName");
		todayRecommendEntity.addStringProperty("userAvatar");
		todayRecommendEntity.addIntProperty("userAge");
		todayRecommendEntity.addStringProperty("date");
		//学校外键
		Property todayRecommend_school = todayRecommendEntity.addLongProperty("schoolID").notNull().getProperty();
		todayRecommendEntity.addToOne(schoolEntity, todayRecommend_school);
		ToMany schoolToTodayRecommend = schoolEntity.addToMany(todayRecommendEntity, todayRecommend_school);
		schoolToTodayRecommend.setName("recommendList");

		/******对话*****/
		Entity conversationEntity = schema.addEntity("Conversation");
		conversationEntity.addIdProperty().autoincrement();
		conversationEntity.addLongProperty("userID");
		conversationEntity.addStringProperty("name");
		conversationEntity.addStringProperty("smallAvatar");
		conversationEntity.addStringProperty("lastMessage");
		conversationEntity.addIntProperty("newNum");
		conversationEntity.addLongProperty("time");
		
		
		/******心动请求*****/
		Entity flipperEntity = schema.addEntity("Flipper");
		flipperEntity.addIdProperty().autoincrement();
		flipperEntity.addIntProperty("userID");
		flipperEntity.addStringProperty("bpushUserID");
		flipperEntity.addStringProperty("bpushChannelID");
		flipperEntity.addStringProperty("nickname");
		flipperEntity.addStringProperty("realname");
		flipperEntity.addStringProperty("gender");
		flipperEntity.addStringProperty("email");
		flipperEntity.addStringProperty("largeAvatar");
		flipperEntity.addStringProperty("samllAvatar");
		flipperEntity.addStringProperty("bloodType");
		flipperEntity.addStringProperty("constell");
		flipperEntity.addStringProperty("introduce");
		flipperEntity.addDateProperty("birthday");
		flipperEntity.addDateProperty("time");
		flipperEntity.addIntProperty("age");
		flipperEntity.addIntProperty("vocationID");
		flipperEntity.addIntProperty("stateID");
		flipperEntity.addIntProperty("provinceID");
		flipperEntity.addIntProperty("cityID");
		flipperEntity.addIntProperty("schoolID");
		flipperEntity.addIntProperty("height");
		flipperEntity.addIntProperty("weight");
		flipperEntity.addIntProperty("imagePass");
		flipperEntity.addDoubleProperty("salary");
		flipperEntity.addBooleanProperty("isRead");
		flipperEntity.addStringProperty("tel");
		flipperEntity.addStringProperty("status");//状态
		flipperEntity.addIntProperty("type");//类型
	}

}
