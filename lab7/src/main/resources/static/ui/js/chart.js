let chartInstance = null;

async function renderGraph() {
  const funcId = document.getElementById('graphFunc').value;
  const res = await apiFetch(`/functions/${funcId}`);
  const func = await res.json();
  const data = JSON.parse(func.pointsData);

  const ctx = document.getElementById('functionChart').getContext('2d');
  if (chartInstance) chartInstance.destroy();

  chartInstance = new Chart(ctx, {
    type: 'line',
     {
      datasets: [{
        label: func.name,
         data.x.map((x, i) => ({ x, y: data.y[i] })),
        borderColor: '#4CAF50',
        tension: 0.1
      }]
    },
    options: {
      responsive: true,
      scales: {
        x: { title: { display: true, text: 'x' } },
        y: { title: { display: true, text: 'f(x)' } }
      }
    }
  });
}