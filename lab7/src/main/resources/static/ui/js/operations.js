async function loadFunctionsForOperations() {
  const res = await apiFetch('/users/me');
  const user = await res.json();
  const res2 = await apiFetch(`/functions/user/${user.id}`);
  const functions = await res2.json();
  const selectA = document.getElementById('funcA');
  const selectB = document.getElementById('funcB');
  selectA.innerHTML = '';
  selectB.innerHTML = '';
  functions.forEach(f => {
    selectA.innerHTML += `<option value="${f.id}">${f.name}</option>`;
    selectB.innerHTML += `<option value="${f.id}">${f.name}</option>`;
  });
}

async function performOperation(type) {
  const funcA = document.getElementById('funcA').value;
  const funcB = document.getElementById('funcB').value;
  const res = await apiFetch('/users/me');
  const user = await res.json();

  const dto = {
    userId: user.id,
    function1Id: parseInt(funcA),
    function2Id: parseInt(funcB),
    operationType: type
  };

  try {
    const opRes = await apiFetch('/operations', { method: 'POST', body: JSON.stringify(dto) });
    const op = await opRes.json();
    showSnackbar(`Операция выполнена! Результат: ID ${op.resultFunction.id}`, 'success');
  } catch (e) {
    showSnackbar(e.message || 'Ошибка операции', 'error');
  }
}