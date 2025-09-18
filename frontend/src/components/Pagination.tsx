type Props = {
  page: number
  size: number
  count: number
  onPageChange: (page: number) => void
  onSizeChange?: (size: number) => void
}

export default function Pagination({ page, size, count, onPageChange, onSizeChange }: Props) {
  const totalPages = Math.max(1, Math.ceil(count / size))

  return (
    <div className="flex items-center justify-between gap-4">
      <div className="flex items-center gap-2">
        <button
          className="btn btn-secondary"
          onClick={() => onPageChange(Math.max(0, page - 1))}
          disabled={page <= 0}
        >
          Попередня
        </button>
        <button
          className="btn btn-secondary"
          onClick={() => onPageChange(Math.min(totalPages - 1, page + 1))}
          disabled={page >= totalPages - 1}
        >
          Наступна
        </button>
      </div>
      <div className="text-sm text-gray-600">
        Сторінка {page + 1} з {totalPages}
      </div>
      {onSizeChange && (
        <div className="flex items-center gap-2">
          <label className="label mb-0" htmlFor="pageSize">На сторінку:</label>
          <select
            id="pageSize"
            className="input py-1"
            value={size}
            onChange={(e) => onSizeChange(Number(e.target.value))}
          >
            {[5, 10, 20, 50].map(s => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </div>
      )}
    </div>
  )
}
