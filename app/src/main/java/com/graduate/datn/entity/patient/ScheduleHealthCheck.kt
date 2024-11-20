package com.graduate.datn.entity.patient

data class ScheduleHealthCheck(
    val deleted_at: String,
    val doctor_name: String,
    val format_date: String,
    val id: Int,
    val medical_examination_form: Int,
    val medical_examination_time: String,
    val service_specialty_icon: String,
    val service_specialty_name: String,
    val time_status: Int
)