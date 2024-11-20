package com.graduate.datn.entity.detailedMedicalExamination

data class DetailedMedicalExamination(
    val address: String,
    val delete_status: Int,
    val deleted_at: Any,
    val doctor_id: Int,
    val doctor_name: String,
    val format_date: String,
    val id: Int,
    val medical_examination_form: Int,
    val medical_examination_time: String,
    val medical_histories_id: Int,
    val phone: String,
    val position: String,
    val schedule_date: String,
    val schedule_note: String,
    val schedule_time_from: String,
    val schedule_time_to: String,
    val service_specialty_icon: String,
    val service_specialty_name: String,
    val status: Int,
    val time_status: Int,
    val user_name: String
)