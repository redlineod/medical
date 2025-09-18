import { useState } from 'react'
import ErrorBanner from '@/components/ErrorBanner'
import { createVisit } from '@/api/client'

export default function CreateVisitPage() {
  const [patientId, setPatientId] = useState('')
  const [doctorId, setDoctorId] = useState('')
  const [start, setStart] = useState('') // datetime-local
  const [end, setEnd] = useState('') // datetime-local

  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    setSuccess(null)

    if (!patientId || !doctorId || !start || !end) {
      setError('Будь ласка, заповніть усі поля')
      return
    }

    try {
      setSubmitting(true)
      const payload = {
        patientId: Number(patientId),
        doctorId: Number(doctorId),
        start: ensureSeconds(start),
        end: ensureSeconds(end)
      }
      const resp = await createVisit(payload)
      setSuccess(`Візит створено (ID: ${resp.id}). Початок: ${resp.start}, кінець: ${resp.end}`)
      setPatientId('')
      setDoctorId('')
      setStart('')
      setEnd('')
    } catch (e: any) {
      setError(e.message || 'Не вдалося створити візит')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="max-w-2xl">
      <div className="card">
        <h2 className="text-xl font-semibold">Створення візиту</h2>
        <p className="text-sm text-gray-500 mt-1">Час потрібно вказувати в часовій зоні лікаря.</p>
        <form className="mt-4 space-y-4" onSubmit={onSubmit}>
          <div>
            <label htmlFor="patientId" className="label">ID пацієнта</label>
            <input id="patientId" type="number" className="input" value={patientId} onChange={e => setPatientId(e.target.value)} placeholder="Напр., 1" />
          </div>
          <div>
            <label htmlFor="doctorId" className="label">ID лікаря</label>
            <input id="doctorId" type="number" className="input" value={doctorId} onChange={e => setDoctorId(e.target.value)} placeholder="Напр., 3" />
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label htmlFor="start" className="label">Початок</label>
              <input id="start" type="datetime-local" className="input" value={start} onChange={e => setStart(e.target.value)} />
            </div>
            <div>
              <label htmlFor="end" className="label">Кінець</label>
              <input id="end" type="datetime-local" className="input" value={end} onChange={e => setEnd(e.target.value)} />
            </div>
          </div>
          <div className="flex items-center gap-3">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Створення…' : 'Створити візит'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => { setPatientId(''); setDoctorId(''); setStart(''); setEnd(''); }} disabled={submitting}>Очистити</button>
          </div>
        </form>
        {error && <div className="mt-4"><ErrorBanner message={error} /></div>}
        {success && <div className="mt-4 rounded-md border border-green-200 bg-green-50 text-green-700 px-4 py-3">{success}</div>}
      </div>

      <div className="mt-6 text-xs text-gray-500">
        Підказка: Для пошуку пацієнтів і лікарів скористайтеся вкладкою «Пацієнти». У тестових даних є 9 пацієнтів і кілька лікарів.
      </div>
    </div>
  )
}

function ensureSeconds(dtLocal: string) {
  // datetime-local returns like YYYY-MM-DDTHH:mm or with seconds
  if (!dtLocal) return dtLocal
  const hasSeconds = dtLocal.match(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/)
  return hasSeconds ? dtLocal : `${dtLocal}:00`
}
