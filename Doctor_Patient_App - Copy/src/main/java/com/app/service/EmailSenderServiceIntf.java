package com.app.service;

public interface EmailSenderServiceIntf {

	void sendEmailOnAppointmentBooking(Long patientId);
	
	void sendEmailOnCancelAppointment(Long appointmentId);
	
	void sendEmailTokenToResetPassword(String userEmail, Long token); 
}
