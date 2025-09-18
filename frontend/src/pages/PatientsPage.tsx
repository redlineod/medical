import { useEffect, useMemo, useState } from 'react'
import { getPatients } from '@/api/client'
import type { PatientDto, VisitDto } from '@/api/types'
import Loader from '@/components/Loader'
import ErrorBanner from '@/components/ErrorBanner'
import Pagination from '@/components/Pagination'

export default function PatientsPage() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)
  const [search, setSearch] = useState('')
  const [doctorIdsInput, setDoctorIdsInput] = useState('')

  const [data, setData] = useState<PatientDto[]>([])
  const [count, setCount] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const doctorIds = useMemo(() =>
    doctorIdsInput
      .split(',')
      .map(s => s.trim())
      .filter(Boolean),
    [doctorIdsInput]
  )

  useEffect(() => {
    let ignore = false
    async function fetchData() {
      setLoading(true)
      setError(null)
      try {
        const resp = await getPatients({ page, size, search: search || undefined, doctorIds: doctorIds.length ? doctorIds : undefined })
        if (!ignore) {
          setData(resp.data)
          setCount(resp.count)
        }
      } catch (e: any) {
        if (!ignore) setError(e.message || 'Сталася помилка')
      } finally {
        if (!ignore) setLoading(false)
      }
    }
    fetchData()
    return () => { ignore = true }
  }, [page, size, search, doctorIdsInput])

  function onSubmitFilters(e: React.FormEvent) {
    e.preventDefault()
    setPage(0)
    // triggers effect automatically because search/doctorIdsInput state already bound
  }

  return (
    <div className="space-y-6">
      <form className="card" onSubmit={onSubmitFilters}>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className="label" htmlFor="search">Пошук по прізвищу/імені</label>
            <input id="search" className="input" placeholder="Напр., Kravchuk або Andriy" value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          <div className="sm:col-span-2">
            <label className="label" htmlFor="doctorIds">ID лікарів (через кому)</label>
            <input id="doctorIds" className="input" placeholder="Напр., 1,3,5" value={doctorIdsInput} onChange={e => setDoctorIdsInput(e.target.value)} />
            <p className="text-xs text-gray-500 mt-1">Показати останні візити лише до зазначених лікарів</p>
          </div>
          <div className="self-end">
            <button type="submit" className="btn btn-primary w-full">Застосувати</button>
          </div>
        </div>
      </form>

      {loading && <Loader />}
      {error && <ErrorBanner message={error} />}

      {!loading && !error && (
        <div className="space-y-4">
          <div className="text-sm text-gray-600">Знайдено пацієнтів: <b>{count}</b></div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {data.map((p, idx) => (
              <div key={idx} className="card">
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="text-lg font-semibold">{p.firstName} {p.lastName}</h3>
                    <p className="text-sm text-gray-500">Останні візити до кожного лікаря</p>
                  </div>
                </div>
                <div className="mt-4 space-y-3">
                  {p.lastVisits && p.lastVisits.length > 0 ? (
                    p.lastVisits.map((v: VisitDto, i: number) => (
                      <div key={i} className="rounded-lg border border-gray-100 p-3 bg-gray-50">
                        <div className="flex items-center justify-between">
                          <div className="text-sm text-gray-700">
                            <div className="font-medium">Лікар: {v.doctor.firstName} {v.doctor.lastName}</div>
                            <div className="text-gray-500">Пацієнтів всього: {v.doctor.totalPatients}</div>
                          </div>
                          <div className="text-right text-sm">
                            <div><span className="text-gray-500">Початок:</span> <span className="font-medium">{formatDate(v.start)}</span></div>
                            <div><span className="text-gray-500">Кінець:</span> <span className="font-medium">{formatDate(v.end)}</span></div>
                          </div>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="text-sm text-gray-500">Візитів не знайдено</div>
                  )}
                </div>
              </div>
            ))}
          </div>

          <Pagination
            page={page}
            size={size}
            count={count}
            onPageChange={setPage}
            onSizeChange={(s) => { setSize(s); setPage(0) }}
          />
        </div>
      )}
    </div>
  )
}

function formatDate(iso: string) {
  try {
    const d = new Date(iso)
    // If parse fails (Invalid Date), return raw
    if (isNaN(d.getTime())) return iso
    return d.toLocaleString()
  } catch {
    return iso
  }
}
