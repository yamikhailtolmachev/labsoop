const API_BASE = '/api';

function getAuthHeader() {
  const creds = localStorage.getItem('auth');
  return creds ? { 'Authorization': `Basic ${creds}` } : {};
}

async function apiFetch(url, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...getAuthHeader(),
    ...options.headers
  };
  const res = await fetch(API_BASE + url, { ...options, headers });
  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(error.message || `HTTP ${res.status}`);
  }
  return res;
}