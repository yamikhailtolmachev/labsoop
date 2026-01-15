function showSnackbar(message, type = 'error') {
  const snackbar = document.getElementById('snackbar');
  if (snackbar) {
    snackbar.textContent = message;
    snackbar.className = `snackbar ${type}`;
    snackbar.classList.add('show');
    setTimeout(() => snackbar.classList.remove('show'), 3000);
  } else {
    alert(message);
  }
}