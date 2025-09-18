import type { PatientsGetResponse, VisitCreateRequest, VisitCreateResponse } from './types'

const BASE = '/api/v1'

function buildQuery(params: Record<string, string | number | undefined>) {
  const usp = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== '') usp.append(k, String(v))
  })
  return usp.toString()
}

export async function getPatients(opts: { page?: number; size?: number; search?: string; doctorIds?: (number | string)[] | string }): Promise<PatientsGetResponse> {
  const { page = 0, size = 20, search, doctorIds } = opts || {}
  const doctorIdsParam = Array.isArray(doctorIds) ? doctorIds.join(',') : doctorIds
  const qs = buildQuery({ page, size, search, doctorIds: doctorIdsParam })
  const res = await fetch(`${BASE}/patients?${qs}`)
  if (!res.ok) {
    const err = await safeJson(res)
    throw new Error(err?.message || `Помилка завантаження пацієнтів (${res.status})`)
  }
  return res.json()
}

export async function createVisit(payload: VisitCreateRequest): Promise<VisitCreateResponse> {
  const res = await fetch(`${BASE}/visits`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  })
  if (!res.ok) {
    const err = await safeJson(res)
    throw new Error(err?.message || `Помилка створення візиту (${res.status})`)
  }
  return res.json()
}

async function safeJson(res: Response) {
  try {
    return await res.json()
  } catch (e) {
    return undefined
  }
}
