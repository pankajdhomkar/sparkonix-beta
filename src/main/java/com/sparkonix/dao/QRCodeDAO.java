package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.QRCode;

import io.dropwizard.hibernate.AbstractDAO;

public class QRCodeDAO extends AbstractDAO<QRCode>{

	public QRCodeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public QRCode save(QRCode qrCode) throws Exception{
		return persist(qrCode);
	}
	
	public QRCode getById(long qrCodeId) throws Exception{
		return get(qrCodeId);
	}
	
	public List<QRCode> findAll(){
		return list(namedQuery("com.sparkonix.entity.QRCode.findAll"));
	}
	
	public QRCode getByQRCode(String qrcode) throws Exception {
		return get(qrcode);
	}
}
