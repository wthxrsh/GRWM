import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

function initials(name) {
  if (!name) return 'G';
  return name.trim()[0].toUpperCase();
}

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar glass">
      <Link to="/" className="navbar-brand">
        <span className="brand-mark">✦</span> GRWM
      </Link>
      <div className="navbar-links">
        <Link to="/" className="nav-link">
          Home
        </Link>
        {user && (
          <Link to="/dashboard" className="nav-link">
            Dashboard
          </Link>
        )}
        {user ? (
          <>
            <span className="nav-user">
              <span className="avatar">{initials(user.firstName || user.username)}</span>
              <span>{user.username}</span>
            </span>
            <button className="btn btn-ghost" onClick={handleLogout} type="button">
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="btn btn-ghost">
              Log in
            </Link>
            <Link to="/signup" className="btn btn-primary">
              Sign Up
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}