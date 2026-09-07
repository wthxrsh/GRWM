const API_BASE = import.meta.env.VITE_API_BASE || '/api';

const ACCESS_TOKEN_KEY = 'grwm_access_token';
const REFRESH_TOKEN_KEY = 'grwm_refresh_token';
const USER_KEY = 'grwm_user';

export function getToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY));
  } catch {
    return null;
  }
}

export function saveSession(data) {
  localStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
  localStorage.setItem(USER_KEY, JSON.stringify(data.user));
}

export function saveUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearSession() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

async function refreshAccessToken() {
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    clearSession();
    return false;
  }
  const res = await fetch(`${API_BASE}/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  });
  if (!res.ok) {
    clearSession();
    return false;
  }
  saveSession(await res.json());
  return true;
}

async function request(path, { method = 'GET', body, auth = true } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  const token = getToken();
  if (auth && token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (res.status === 401 && auth) {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      const freshToken = getToken();
      res = await fetch(`${API_BASE}${path}`, {
        method,
        headers: { ...headers, Authorization: `Bearer ${freshToken}` },
        body: body === undefined ? undefined : JSON.stringify(body),
      });
    }
  }

  if (res.status === 204) {
    return null;
  }
  if (!res.ok) {
    const payload = await res.json().catch(() => null);
    const error = new Error(
      payload?.message || payload?.error || `Request failed with status ${res.status}`,
    );
    error.status = res.status;
    error.fieldErrors = payload?.fieldErrors;
    throw error;
  }
  return res.json();
}

export const api = {
  register: (payload) => request('/auth/register', { method: 'POST', body: payload, auth: false }),
  login: (payload) => request('/auth/login', { method: 'POST', body: payload, auth: false }),
  reverseGeocode: (latitude, longitude) =>
    request(`/locations/reverse?latitude=${latitude}&longitude=${longitude}`, { auth: false }),
  getProfile: () => request('/users/profile'),
  updateProfile: (payload) => request('/users/profile', { method: 'PUT', body: payload }),
  suggest: (payload) => request('/recommendations/suggest', { method: 'POST', body: payload }),
  history: () => request('/recommendations/history'),
  recommendation: (id) => request(`/recommendations/${id}`),
};