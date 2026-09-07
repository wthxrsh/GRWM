import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import GlassCard from '../Components/GlassCard.jsx';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(form);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Login failed');
    } finally {
      setSubmitting(false);
    }
  };

  const update = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  return (
    <div className="auth-page">
      <GlassCard className="auth-card reveal">
        <h2 className="auth-title">Welcome back</h2>
        <p className="auth-subtitle">Sign in to get your next outfit sorted.</p>
        <form onSubmit={handleSubmit} className="auth-form">
          <label className="field">
            <span>Username</span>
            <input value={form.username} onChange={update('username')} required autoFocus />
          </label>
          <label className="field">
            <span>Password</span>
            <input type="password" value={form.password} onChange={update('password')} required />
          </label>
          {error && <div className="form-error">{error}</div>}
          <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
            {submitting ? (
              <span className="spinner-text">
                <span className="spinner" /> Signing in…
              </span>
            ) : (
              'Log in'
            )}
          </button>
        </form>
        <p className="auth-switch">
          New here? <Link to="/signup">Create an account</Link>
        </p>
      </GlassCard>
    </div>
  );
}