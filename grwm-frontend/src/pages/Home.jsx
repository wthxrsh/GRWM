import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import GlassCard from '../Components/GlassCard.jsx';

export default function Home() {
  const { user } = useAuth();

  return (
    <div className="home">
      <section className="hero reveal">
        <span className="badge">
          <span className="badge-dot" /> Live weather · AI styling
        </span>
        <h1 className="hero-title">
          Get Ready <span className="gradient-text">With Me</span>
        </h1>
        <p className="hero-subtitle">
          Personalized outfit recommendations built on live weather and your own style. Never
          second-guess what to wear again.
        </p>
        <div className="hero-actions">
          {user ? (
            <Link to="/dashboard" className="btn btn-primary btn-lg">
              Open My Dashboard
            </Link>
          ) : (
            <>
              <Link to="/signup" className="btn btn-primary btn-lg">
                Get Started — it’s free
              </Link>
              <Link to="/login" className="btn btn-ghost btn-lg">
                I already have an account
              </Link>
            </>
          )}
        </div>
        <div className="hero-stats">
          <div className="stat">
            <span className="stat-value">Real-time</span>
            <span className="stat-label">Weather data</span>
          </div>
          <div className="stat">
            <span className="stat-value">5 styles</span>
            <span className="stat-label">To pick from</span>
          </div>
          <div className="stat">
            <span className="stat-value">0 set-up</span>
            <span className="stat-label">Location & style adjust anytime</span>
          </div>
        </div>
      </section>

      <section className="features">
        <GlassCard className="feature-card">
          <span className="feature-icon">🌤️</span>
          <h3>Live Weather</h3>
          <p>Real-time temperature, humidity and wind for any city — or your exact location.</p>
        </GlassCard>
        <GlassCard className="feature-card">
          <span className="feature-icon">✨</span>
          <h3>AI Styling</h3>
          <p>Smart outfit suggestions tuned to the forecast and your personal style.</p>
        </GlassCard>
        <GlassCard className="feature-card">
          <span className="feature-icon">🎨</span>
          <h3>Your Style, Your Call</h3>
          <p>Pick a vibe per outfit — casual, formal, sporty or minimalist. Change it anytime.</p>
        </GlassCard>
        <GlassCard className="feature-card">
          <span className="feature-icon">🗂️</span>
          <h3>History</h3>
          <p>Every recommendation is saved so you can look back before heading out.</p>
        </GlassCard>
      </section>
    </div>
  );
}