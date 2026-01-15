function b64(str) {
  return btoa(unescape(encodeURIComponent(str)));
}

async function login() {
  const username = document.getElementById('username').value;
  const password = document.getElementById('password').value;
  const creds = b64(`${username}:${password}`);
  try {
    await fetch('/api/auth/login', { headers: { 'Authorization': `Basic ${creds}` } });
    localStorage.setItem('auth', creds);
    localStorage.setItem('username', username);
    location.href = 'index.html';
  } catch (e) {
    showSnackbar(e.message || 'Ошибка входа');
  }
}

async function register() {
  const username = document.getElementById('username').value;
  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;
  try {
    await fetch('/api/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    });
    showSnackbar('Регистрация успешна! Войдите.');
    setTimeout(() => location.href = 'login.html', 2000);
  } catch (e) {
    showSnackbar(e.message || 'Ошибка регистрации');
  }
}

function logout() {
  localStorage.removeItem('auth');
  localStorage.removeItem('username');
  location.href = 'login.html';
}

async function checkAuthAndRender() {
  const creds = localStorage.getItem('auth');
  if (!creds) {
    if (!location.pathname.endsWith('login.html') && !location.pathname.endsWith('register.html')) {
      location.href = 'login.html';
    }
    return;
  }
  try {
    await fetch('/api/auth/check', { headers: { 'Authorization': `Basic ${creds}` } });
    const username = localStorage.getItem('username');
    const userInfo = document.getElementById('user-info');
    if (userInfo) userInfo.textContent = `Привет, ${username}!`;
  } catch {
    logout();
  }
}