type Props = {
  message: string
}

export default function ErrorBanner({ message }: Props) {
  return (
    <div className="rounded-md border border-red-200 bg-red-50 text-red-700 px-4 py-3">
      {message}
    </div>
  )
}
