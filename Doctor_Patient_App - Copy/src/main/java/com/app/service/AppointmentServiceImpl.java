package com.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.custome_exception.UserHandlingException;
import com.app.dto.DoctorDTO;
import com.app.entity.modal.Appointment;
import com.app.entity.modal.Doctor;
import com.app.entity.modal.DoctorTimeTable;
import com.app.entity.modal.Patient;
import com.app.repository.AppointmentRepository;
import com.app.repository.DoctorRepository;
import com.app.repository.DoctorTimeTableRepository;
import com.app.repository.PatientRepository;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentServiceIntf {

	@Autowired
	AppointmentRepository appointmentRepo;
	
	@Autowired
	DoctorTimeTableRepository doctorTimeTableRepo;
	
	@Autowired
	PatientRepository patientRepo;
	
	@Autowired
	DoctorRepository doctorRepo;
	
	@Override
	public String cancelAppointment(Long appointmentId) {
	
		Appointment appointment = appointmentRepo.findById(appointmentId).orElseThrow(()->new UserHandlingException("appointment Id not found"));
		Doctor doctor = appointment.getDoctor();
		LocalDateTime appointmentTime = appointment.getAppointmentTime();
		doctor.getTimeSlot().bookAvailableSlot(appointmentTime);
		appointmentRepo.deleteById(appointmentId);
		return "Appointment cancelled successfully(for "+appointmentId+")...!!!";
	}

	@Override
	public List<Appointment> getAllPatientCurrentAppoitments(Long patientId) {
		return appointmentRepo.getAllPatientCurrentAppoitments(patientId);
	}

	@Override
	public List<Appointment> getAllPatientAppoitmentsHistory(Long patientId) {
		return appointmentRepo.getAllPatientAppoitmentsHistory(patientId);
	}

	@Override
	public List<Appointment> getAllCurrentAppoitmentsForDoctor(Long doctorId) {
		return appointmentRepo.getAllCurrentAppoitmentsForDoctor(doctorId);
	}

	@Override
	public List<Appointment> getPatientAppoitmentsHistoryForDoctor(Long doctorId, Long patientId) {
		return appointmentRepo.getPatientAppoitmentsHistoryForDoctor(doctorId, patientId);
	}

	@Override
	public List<Appointment> getAllAppoitmentsHistoryForDoctor(Long doctorId) {
		return appointmentRepo.getAllAppoitmentsHistoryForDoctor(doctorId);
	}

	@Override
	public List<LocalDateTime> bookAppointmentForPatient(Long doctorId, Long patientId, String stime) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime time = LocalDateTime.parse(stime, formatter);
		
		Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(() -> new UserHandlingException("Doctor not found...!!!"));
		
		
		Patient patient = patientRepo.findById(patientId).orElseThrow(() -> new UserHandlingException("Patient not found...!!!"));
		
		DoctorTimeTable timeTable = doctor.getTimeSlot();
		
		Appointment appointment = new Appointment(time, doctor, patient);
		appointmentRepo.save(appointment);

		List<LocalDateTime> availableSlotList = timeTable.bookAvailableSlot(time);
		
		return availableSlotList;
	}

	@Override
	public Patient getPatientByAppointmentId(Long appointmentId) {
		
		Appointment appointment = appointmentRepo.findById(appointmentId).get();		
		Patient patient = appointment.getPatient();
		return patient;
	}

	@Override
	public List<LocalDateTime> getAllAppointmentSlots(Long doctorId) {
		
		Doctor doctor = doctorRepo.findById(doctorId).get();
		Map<LocalDateTime, Boolean> availableSlots = doctor.getTimeSlot().getAvailableSlots();
		List<LocalDateTime> list = new ArrayList<>();
		for(Map.Entry<LocalDateTime, Boolean> entry : availableSlots.entrySet()) {
			int currDate = LocalDate.now().getDayOfMonth();
			int currMonth = LocalDate.now().getMonthValue();
			int slotDate = entry.getKey().getDayOfMonth();
			int slotMonth = entry.getKey().getMonthValue();
			if(entry.getValue() == true && entry.getKey().isAfter(LocalDateTime.now()) && currDate == slotDate && currMonth == slotMonth) { //send only list whose boolean value is true (not booked slots)
				list.add(entry.getKey());
			}
		}
		Collections.sort(list);
		
		return list;
	}

	@Override
	public Doctor getDoctorByAppointmentId(Long appointmentId) {
			
		Appointment appointment = appointmentRepo.findById(appointmentId).orElseThrow(() -> new UserHandlingException("Invalid appointment id!!!"));
		return appointment.getDoctor();

	}
}