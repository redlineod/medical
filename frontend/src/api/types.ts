export type DoctorDto = {
  firstName: string
  lastName: string
  totalPatients: number
}

export type VisitDto = {
  start: string
  end: string
  doctor: DoctorDto
}

export type PatientDto = {
  firstName: string
  lastName: string
  lastVisits: VisitDto[]
}

export type PatientsGetResponse = {
  data: PatientDto[]
  count: number
}

export type VisitCreateRequest = {
  start: string
  end: string
  patientId: number
  doctorId: number
}

export type VisitCreateResponse = {
  id: number
  start: string
  end: string
}
