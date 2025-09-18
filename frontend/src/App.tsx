import { Outlet, NavLink } from 'react-router-dom'
import Header from './components/Header'

export default function App() {
  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <nav className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-12 items-center gap-6">
            <NavLink
              to="/"
              end
              className={({ isActive }) =>
                `py-2 border-b-2 ${isActive ? 'border-brand-600 text-brand-700' : 'border-transparent text-gray-600 hover:text-gray-900 hover:border-gray-300'}`
              }
            >
              Пацієнти
            </NavLink>
            <NavLink
              to="/create-visit"
              className={({ isActive }) =>
                `py-2 border-b-2 ${isActive ? 'border-brand-600 text-brand-700' : 'border-transparent text-gray-600 hover:text-gray-900 hover:border-gray-300'}`
              }
            >
              Створити візит
            </NavLink>
          </div>
        </div>
      </nav>
      <main className="flex-1">
        <div className="mx-auto max-w-7xl p-4 sm:p-6 lg:p-8">
          <Outlet />
        </div>
      </main>
      <footer className="bg-gray-100 text-center text-sm text-gray-500 py-4">
        © {new Date().getFullYear()} Medical Visits Tracker
      </footer>
    </div>
  )
}
