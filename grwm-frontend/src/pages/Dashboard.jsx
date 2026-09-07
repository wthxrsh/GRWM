import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/client.js';
import { useAuth } from '../context/AuthContext.jsx';
import GlassCard from '../Components/GlassCard.jsx';

const STYLES = [
  { value: '', label: 'Auto' },
  { value: 'casual', label: 'Casual' },
  { value: 'formal', label: 'Formal' },
  { value: 'sporty', label: 'Sporty' },
  { value: 'minimalist', label: 'Minimalist' },
  { value: 'boho', label: 'Boho' },
];

function weatherIcon(condition = '') {
  const c = condition.toLowerCase();
  if (c.includes('clear') || c.includes('sun')) return '☀️';
  if (c.includes('partly')) return '⛅';
  if (c.includes('thunder')) return '⛈️';
  if (c.includes('snow')) return '❄️';
  if (c.includes('drizzle') || c.includes('rain')) return c.includes('rain') ? '🌧️' : '🌦️';
  if (c.includes('fog') || c.includes('mist')) return '🌫️';
  if (c.includes('cloud') || c.includes('overcast')) return '☁️';
  return '🌤️';
}

function greeting() {
  const h = new Date().getHours();
  if (h < 5) return 'Burning the midnight oil';
  if (h < 12) return 'Good morning';
  if (h < 17) return 'Good afternoon';
  return 'Good evening';
}

export default function Dashboard() {
  const { user, updateProfile } = useAuth();
  const displayName = user?.firstName || user?.username;

  const [city, setCity] = useState(String(user?.city || ''));
  const [coords, setCoords] = useState(null);
  const [style, setStyle] = useState(String(user?.stylePreference || ''));
  const [locationHint, setLocationHint] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);
  const [history, setHistory] = useState([]);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [profileMsg, setProfileMsg] = useState(null);
  const [geolocating, setGeolocating] = useState(false);

  const loadHistory = useCallback(async () => {
    setLoadingHistory(true);
    try {
      setHistory(await api.history());
    } catch (err) {
      console.error('Failed to load history:', err);
    } finally {
      setLoadingHistory(false);
    }
  }, []);

  useEffect(() => {
    void loadHistory();
  }, [loadHistory]);

  const useMyLocation = () => {
    setGeolocating(true);
    setError(null);
    if (!navigator.geolocation) {
      setError('Geolocation is not supported by this browser.');
      setGeolocating(false);
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const { latitude, longitude } = pos.coords;
        setCoords({ latitude, longitude });
        setCity('');
        setLocationHint('Using your current location · resolving city…');
        setGeolocating(false);
        api
          .reverseGeocode(latitude, longitude)
          .then((res) => {
            if (res?.city) {
              setCity(res.city);
              setLocationHint(`Using your current location (${res.city})`);
            } else {
              setLocationHint('Using your current location');
            }
          })
          .catch(() => setLocationHint('Using your current location'));
      },
      () => {
        setError('Could not access your location. Enter a city instead.');
        setGeolocating(false);
      },
    );
  };

  const handleSuggest = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);
    const payload = {};
    if (coords) {
      payload.latitude = coords.latitude;
      payload.longitude = coords.longitude;
    } else {
      payload.city = city;
    }
    if (style) {
      payload.stylePreference = style;
    }
    try {
      const suggestion = await api.suggest(payload);
      setResult(suggestion);
      await loadHistory();
    } catch (err) {
      setError(err.message || 'Could not generate a recommendation.');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveProfile = async () => {
    setProfileMsg(null);
    setError(null);
    try {
      await updateProfile({ city, stylePreference: style });
      setProfileMsg('Defaults saved — they can change anytime');
      setTimeout(() => setProfileMsg(null), 3500);
    } catch (err) {
      setError(err.message || 'Could not save profile.');
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-welcome reveal">
        <div>
          <h1>
            {greeting()}, {displayName} 👋
          </h1>
          <p>What does the day have in store? Let’s find your outfit.</p>
        </div>
      </div>

      <div className="dashboard-grid">
        <GlassCard className="panel reveal">
          <h2 className="panel-title">Get dressed for today</h2>
          <form onSubmit={handleSuggest} className="suggest-form">
            <div className="form-row">
              <label className="field grow">
                <span>City</span>
                <input
                  value={city}
                  onChange={(e) => {
                    setCity(e.target.value);
                    setCoords(null);
                    setLocationHint(null);
                  }}
                  placeholder="e.g. Paris"
                />
              </label>
              <button
                type="button"
                className="btn btn-ghost align-end"
                onClick={useMyLocation}
                disabled={geolocating}
              >
                {geolocating ? (
                  <>
                    <span className="spinner" /> Locating…
                  </>
                ) : (
                  '📍 Use my location'
                )}
              </button>
            </div>
            {locationHint && <small className="hint">{locationHint}</small>}

            <div className="field">
              <span>Style (optional, applies to this recommendation)</span>
              <div className="range-row">
                {STYLES.map((s) => (
                  <button
                    key={s.value}
                    type="button"
                    className={`style-chip${style === s.value ? ' active' : ''}`}
                    onClick={() => setStyle(s.value)}
                  >
                    {s.label}
                  </button>
                ))}
              </div>
            </div>

            {error && <div className="form-error">{error}</div>}
            <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
              {loading ? (
                <span className="spinner-text">
                  <span className="spinner" /> Consulting the weather…
                </span>
              ) : (
                '✨ Get Recommendation'
              )}
            </button>
          </form>
        </GlassCard>

        <GlassCard className="panel reveal" style={{ animationDelay: '0.08s' }}>
          <h2 className="panel-title">Saved preferences</h2>
          <p className="muted" style={{ fontSize: '0.9rem', marginTop: -6, lineHeight: 1.5 }}>
            Your defaults pre-fill the form. They are not tied to your account sign-up, so adjust
            them whenever life changes.
          </p>
          <div className="suggest-form">
            <label className="field">
              <span>Default city</span>
              <input value={city} onChange={(e) => setCity(e.target.value)} placeholder="e.g. Tokyo" />
            </label>
            <label className="field">
              <span>Default style</span>
              <select value={style} onChange={(e) => setStyle(e.target.value)}>
                {STYLES.map((s) => (
                  <option key={s.value} value={s.value}>
                    {s.label}
                  </option>
                ))}
              </select>
            </label>
            {profileMsg && <small className="success">{profileMsg}</small>}
            <button type="button" className="btn btn-ghost btn-block" onClick={handleSaveProfile}>
              Save defaults
            </button>
          </div>
        </GlassCard>
      </div>

      {result && (
        <GlassCard className="panel result-panel reveal">
          <div className="result-header">
            <div className="result-weather">
              <span className="weather-icon" aria-hidden="true">
                {weatherIcon(result.weatherCondition)}
              </span>
              <div>
                <span className="result-eyebrow">
                  {result.city || 'Your location'} · right now
                </span>
                <h2 className="result-temp">
                  {Math.round(result.temperature)}° <small>{result.weatherCondition}</small>
                </h2>
              </div>
            </div>
            <div className="weather-stats">
              <span className="chip">Feels like {Math.round(result.feelsLike)}°</span>
              <span className="chip">💧 {Math.round(result.humidity)}%</span>
              <span className="chip">💨 {Math.round(result.windSpeed)} km/h</span>
            </div>
          </div>

          <p className="result-summary">{result.summary}</p>

          <div className="outfit-section">
            <div className="outfit-section-head">
              <span>Outfit suggestions</span>
            </div>
            <ul className="outfit-list">
              {result.recommendations.map((item, i) => (
                <li key={`${result.id}-${i}`} className="outfit-item">
                  <span className="bullet">{i + 1}</span>
                  <span>{item}</span>
                </li>
              ))}
            </ul>
          </div>
        </GlassCard>
      )}

      <GlassCard className="panel reveal">
        <h2 className="panel-title">Recent recommendations</h2>
        {loadingHistory ? (
          <p className="empty-state loading-pulse">Loading your history…</p>
        ) : history.length === 0 ? (
          <p className="empty-state">
            No recommendations yet. Hit “Get Recommendation” to create your first one!
          </p>
        ) : (
          <ul className="history-list">
            {history.map((item) => (
              <li key={item.id} className="history-item">
                <div>
                  <strong>{item.city || 'Current location'}</strong>
                  <span className="muted"> — </span>
                  <span className="history-temp">{Math.round(item.temperature)}°</span>
                  <span className="muted"> {item.weatherCondition}</span>
                </div>
                <span className="history-date">{new Date(item.createdAt).toLocaleString()}</span>
              </li>
            ))}
          </ul>
        )}
      </GlassCard>
    </div>
  );
}