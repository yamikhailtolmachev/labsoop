async function createFunction() {
  try {
    const res = await apiFetch('/users/me');
    const user = await res.json();
    const dto = {
      userId: user.id,
      name: document.getElementById('name').value,
      type: document.getElementById('type').value,
      expression: document.getElementById('expression').value,
      leftBound: parseFloat(document.getElementById('left').value),
      rightBound: parseFloat(document.getElementById('right').value),
      pointsCount: parseInt(document.getElementById('points').value),
      pointsData: "{}"
    };
    const created = await apiFetch('/functions', { method: 'POST', body: JSON.stringify(dto) });
    showSnackbar('Функция создана!', 'success');
    loadUserFunctions();
  } catch (e) {
    showSnackbar(e.message || 'Ошибка создания функции', 'error');
  }
}

async function loadUserFunctions() {
  try {
    const res = await apiFetch('/users/me');
    const user = await res.json();
    const res2 = await apiFetch(`/functions/user/${user.id}`);
    const functions = await res2.json();
    const listDiv = document.getElementById('functions-list');
    if (functions.length === 0) {
      listDiv.innerHTML = '<p>У вас пока нет функций.</p>';
      return;
    }
    let html = '<table class="point-table"><thead><tr><th>ID</th><th>Имя</th><th>Тип</th><th>Действия</th></tr></thead><tbody>';
    functions.forEach(f => {
      html += `
        <tr>
          <td>${f.id}</td>
          <td>${f.name}</td>
          <td>${f.type}</td>
          <td class="actions-cell">
            <button onclick="editFunction(${f.id})">Редактировать</button>
            <button onclick="deleteFunction(${f.id})">Удалить</button>
          </td>
        </tr>
      `;
    });
    html += '</tbody></table>';
    listDiv.innerHTML = html;
  } catch (e) {
    showSnackbar(e.message || 'Ошибка загрузки функций', 'error');
  }
}

async function editFunction(id) {
  try {
    const res = await apiFetch(`/functions/${id}`);
    const func = await res.json();
    showEditModal(func);
  } catch (e) {
    showSnackbar(e.message || 'Ошибка загрузки функции для редактирования', 'error');
  }
}

function showEditModal(func) {
  const modal = document.createElement('div');
  modal.className = 'modal-overlay';
  modal.style.position = 'fixed';
  modal.style.top = '0';
  modal.style.left = '0';
  modal.style.width = '100%';
  modal.style.height = '100%';
  modal.style.backgroundColor = 'rgba(0,0,0,0.5)';
  modal.style.display = 'flex';
  modal.style.alignItems = 'center';
  modal.style.justifyContent = 'center';
  modal.style.zIndex = '1000';

  const content = document.createElement('div');
  content.className = 'modal-content';
  content.style.backgroundColor = 'var(--card-bg)';
  content.style.padding = '2rem';
  content.style.borderRadius = '12px';
  content.style.maxWidth = '500px';
  content.style.width = '90%';
  content.style.boxShadow = '0 4px 12px rgba(0,0,0,0.08)';
  content.style.color = 'var(--text-primary)';

  content.innerHTML = `
    <h3 style="margin-bottom: 1.5rem;">Редактировать функцию</h3>
    <div class="form-section">
      <label>Имя: <input id="edit-name" value="${func.name}"></label>
      <label>Тип:
        <select id="edit-type">
          <option value="BASIC" ${func.type === 'BASIC' ? 'selected' : ''}>Основная</option>
          <option value="COMPOSITE" ${func.type === 'COMPOSITE' ? 'selected' : ''}>Композитная</option>
        </select>
      </label>
      <label>Выражение: <input id="edit-expression" value="${func.expression}"></label>
      <label>Левая граница: <input type="number" step="any" id="edit-left" value="${func.leftBound}"></label>
      <label>Правая граница: <input type="number" step="any" id="edit-right" value="${func.rightBound}"></label>
      <label>Точек: <input type="number" id="edit-points" value="${func.pointsCount}" min="2"></label>
    </div>
    <div style="display: flex; gap: 0.75rem; margin-top: 1.5rem; justify-content: center;">
      <button onclick="saveEditedFunction(${func.id})">Сохранить</button>
      <button onclick="closeModal()">Отмена</button>
    </div>
  `;

  modal.appendChild(content);
  document.body.appendChild(modal);
}

function closeModal() {
  const modal = document.querySelector('.modal-overlay');
  if (modal) modal.remove();
}

async function saveEditedFunction(id) {
  const name = document.getElementById('edit-name').value;
  const type = document.getElementById('edit-type').value;
  const expression = document.getElementById('edit-expression').value;
  const left = parseFloat(document.getElementById('edit-left').value);
  const right = parseFloat(document.getElementById('edit-right').value);
  const points = parseInt(document.getElementById('edit-points').value);

  const dto = {
    name,
    type,
    expression,
    leftBound: left,
    rightBound: right,
    pointsCount: points
  };

  try {
    await apiFetch(`/functions/${id}`, { method: 'PUT', body: JSON.stringify(dto) });
    showSnackbar('Функция обновлена!', 'success');
    const listDiv = document.getElementById('functions-list');
    if (listDiv) listDiv.innerHTML = '';
    loadUserFunctions();
    closeModal();
  } catch (e) {
    showSnackbar(e.message || 'Ошибка сохранения', 'error');
  }
}

async function deleteFunction(id) {
  if (!confirm('Удалить функцию?')) return;
  try {
    await apiFetch(`/functions/${id}`, { method: 'DELETE' });
    showSnackbar('Функция удалена', 'success');
    loadUserFunctions();
  } catch (e) {
    showSnackbar(e.message || 'Ошибка удаления', 'error');
  }
}