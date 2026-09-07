import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import GlassCard from '../Components/GlassCard.jsx';

export default function Signup() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setFieldErrors({});

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setSubmitting(true);
    try {
      const { confirmPassword, ...payload } = form;
      await register(payload);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Registration failed');
      setFieldErrors(err.fieldErrors || {});
    } finally {
      setSubmitting(false);
    }
  };

  const update = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  return (
    <div className="auth-page">
      <GlassCard className="auth-card reveal">
        <h2 className="auth-title">Create your account</h2>
        <p className="auth-subtitle">
          Just the essentials. Set your location and style any time from the dashboard.
        </p>
        <form onSubmit={handleSubmit} className="auth-form">
          <label className="field">
            <span>Username</span>
            <input value={form.username} onChange={update('username')} required autoFocus minLength={3} />
            {fieldErrors.username && <small className="field-error">{fieldErrors.username}</small>}
          </label>
          <label className="field">
            <span>Email</span>
            <input type="email" value={form.email} onChange={update('email')} required />
            {fieldErrors.email && <small className="field-error">{fieldErrors.email}</small>}
          </label>
          <div className="form-row">
            <label className="field">
              <span>Password</span>
              <input
                type="password"
                value={form.password}
                onChange={update('password')}
                required
                minLength={8}
              />
              {fieldErrors.password && <small className="field-error">{fieldErrors.password}</small>}
            </label>
            <label className="field">
              <span>Confirm password</span>
              <input type="password" value={form.confirmPassword} onChange={update('confirmPassword')} required />
            </label>
          </div>
          {error && <div className="form-error">{error}</div>}
          <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
            {submitting ? (
              <span className="spinner-text">
                <span className="spinner" /> Creating account…
              </span>
            ) : (
              'Create account'
            )}
          </button>
        </form>
        <p className="auth-switch">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </GlassCard>
    </div>
  );
}