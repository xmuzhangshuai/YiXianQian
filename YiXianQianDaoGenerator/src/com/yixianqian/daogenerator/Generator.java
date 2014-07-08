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
//		/****����ʡ��*******/
//		Entity provinceEntity = schema.addEntity("P_province");
//		provinceEntity.addIdProperty();
//		provinceEntity.addLongProperty("P_provinceID");
//		provinceEntity.addStringProperty("P_Name");
//
//		/****���ӳ���*******/
//		Entity cityEntity = schema.addEntity("C_city");
//		cityEntity.addIdProperty();
//		cityEntity.addLongProperty("C_cityID");
//		cityEntity.addStringProperty("C_Name");
//		//ʡ�����
//		Property city_province_id = cityEntity.addLongProperty("P_id").notNull().getProperty();
//		cityEntity.addToOne(provinceEntity, city_province_id);
//		ToMany provinceToCity = provinceEntity.addToMany(cityEntity, city_province_id);
//		provinceToCity.setName("cityList");
//
//		/****�������ܲ���*******/
//		Entity cometentEntity = schema.addEntity("C_cometent");
//		cometentEntity.addIdProperty();
//		cometentEntity.addStringProperty("C_name");
//
//		/****����ѧУ*******/
//		Entity schoolEntity = schema.addEntity("S_school");
//		schoolEntity.addIdProperty();
//		schoolEntity.addStringProperty("S_name");
//		schoolEntity.addStringProperty("S_info");
//		//���ܲ������
//		Property school_cometent_idProperty = schoolEntity.addLongProperty("S_cometentid").notNull().getProperty();
//		schoolEntity.addToOne(cometentEntity, school_cometent_idProperty);
//		ToMany cometentToSchool = cometentEntity.addToMany(schoolEntity, school_cometent_idProperty);
//		cometentToSchool.setName("schoolList");
//		//�������
//		Property school_city_id = schoolEntity.addLongProperty("S_cityd").notNull().getProperty();
//		schoolEntity.addToOne(cityEntity, school_city_id);
//		ToMany cityToSchool = cityEntity.addToMany(schoolEntity, school_city_id);
//		cityToSchool.setName("schoolList");
		/****����ʡ��*******/
		Entity provinceEntity = schema.addEntity("Province");
		provinceEntity.addIdProperty();
		provinceEntity.addLongProperty("provinceID");
		provinceEntity.addStringProperty("provinceName");

		/****���ӳ���*******/
		Entity cityEntity = schema.addEntity("City");
		cityEntity.addIdProperty();
		cityEntity.addLongProperty("cityID");
		cityEntity.addStringProperty("cityName");
		//ʡ�����
		Property city_province_id = cityEntity.addLongProperty("provinceID").notNull().getProperty();
		cityEntity.addToOne(provinceEntity, city_province_id);
		ToMany provinceToCity = provinceEntity.addToMany(cityEntity, city_province_id);
		provinceToCity.setName("cityList");

		/****�������ܲ���*******/
		Entity cometentEntity = schema.addEntity("Cometent");
		cometentEntity.addIdProperty();
		cometentEntity.addStringProperty("cometentName");

		/****����ѧУ*******/
		Entity schoolEntity = schema.addEntity("School");
		schoolEntity.addIdProperty();
		schoolEntity.addStringProperty("schoolName");
		schoolEntity.addStringProperty("schoolInfo");
		//���ܲ������
		Property school_cometent_idProperty = schoolEntity.addLongProperty("cometentID").notNull().getProperty();
		schoolEntity.addToOne(cometentEntity, school_cometent_idProperty);
		ToMany cometentToSchool = cometentEntity.addToMany(schoolEntity, school_cometent_idProperty);
		cometentToSchool.setName("schoolList");
		//�������
		Property school_city_id = schoolEntity.addLongProperty("cityID").notNull().getProperty();
		schoolEntity.addToOne(cityEntity, school_city_id);
		ToMany cityToSchool = cityEntity.addToMany(schoolEntity, school_city_id);
		cityToSchool.setName("schoolList");
	}

}