(() => {
    const { api, currency, escapeHtml } = window.ExpenseApp;
    const byId = id => document.getElementById(id);
    let expenses = [];
    let categoryChart;
    let monthlyChart;
    let noticeTimer;

    function showNotice(message, type = 'error') {
        const notice = byId('notice');
        clearTimeout(noticeTimer);
        notice.textContent = message;
        notice.className = `notice show ${type}`;
        noticeTimer = setTimeout(() => notice.classList.remove('show'), 4500);
    }

    async function loadUsers() {
        const users = await api('/api/admin/users');
        const list = byId('users-list');
        if (!users.length) {
            list.innerHTML = '<tr><td colspan="5" class="empty">暂无用户</td></tr>';
            return;
        }
        list.innerHTML = users.map(user => {
            const isAdmin = user.roles.includes('ROLE_ADMIN');
            return `
            <tr>
                <td>${user.id}</td>
                <td>${escapeHtml(user.username)}</td>
                <td>${escapeHtml(user.email)}</td>
                <td>${isAdmin ? '管理员' : '普通用户'}</td>
                <td>${isAdmin ? '受保护' : `<button class="btn danger small" type="button" data-delete-user="${user.id}">删除</button>`}</td>
            </tr>`;
        }).join('');
    }

    async function loadExpenses() {
        expenses = await api('/api/admin/expenses/report');
        const list = byId('report-list');
        if (!expenses.length) {
            list.innerHTML = '<tr><td colspan="6" class="empty">暂无支出</td></tr>';
        } else {
            list.innerHTML = expenses.map(expense => `
                <tr>
                    <td>${expense.id}</td>
                    <td>${escapeHtml(expense.username)}</td>
                    <td class="amount">${currency.format(Number(expense.amount))}</td>
                    <td>${escapeHtml(expense.category)}</td>
                    <td>${escapeHtml(expense.expenseDate)}</td>
                    <td>${escapeHtml(expense.description || '—')}</td>
                </tr>`).join('');
        }
        renderCharts();
    }

    function renderCharts() {
        if (typeof Chart === 'undefined') return;
        const categoryTotals = new Map();
        const monthlyTotals = new Map();
        expenses.forEach(expense => {
            const category = expense.category || '未分类';
            const month = expense.expenseDate.slice(0, 7);
            categoryTotals.set(category, (categoryTotals.get(category) || 0) + Number(expense.amount));
            monthlyTotals.set(month, (monthlyTotals.get(month) || 0) + Number(expense.amount));
        });

        categoryChart?.destroy();
        monthlyChart?.destroy();
        categoryChart = new Chart(byId('admin-category-chart'), {
            type: 'doughnut',
            data: {
                labels: [...categoryTotals.keys()],
                datasets: [{ data: [...categoryTotals.values()], backgroundColor: ['#4f46e5','#14b8a6','#f59e0b','#e11d48','#0ea5e9','#8b5cf6'] }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
        });
        const months = [...monthlyTotals.keys()].sort();
        monthlyChart = new Chart(byId('admin-monthly-chart'), {
            type: 'bar',
            data: { labels: months, datasets: [{ data: months.map(month => monthlyTotals.get(month)), backgroundColor: '#4f46e5', borderRadius: 5 }] },
            options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true } }, plugins: { legend: { display: false } } }
        });
    }

    async function refresh() {
        const results = await Promise.allSettled([loadUsers(), loadExpenses()]);
        const failure = results.find(result => result.status === 'rejected');
        if (failure) showNotice(failure.reason.message);
    }

    async function deleteUser(id) {
        if (!window.confirm('删除用户会同时删除其支出和预算，确定继续吗？')) return;
        await api(`/api/admin/users/${id}`, { method: 'DELETE' });
        showNotice('用户已删除', 'success');
        await refresh();
    }

    function csvCell(value) {
        let text = String(value ?? '');
        if (/^[=+\-@]/.test(text)) text = `'${text}`;
        return `"${text.replace(/"/g, '""')}"`;
    }

    function exportCsv() {
        if (!expenses.length) {
            showNotice('没有可导出的支出');
            return;
        }
        const rows = expenses.map(expense => [
            expense.id, expense.username, expense.amount, expense.category, expense.expenseDate, expense.description || ''
        ].map(csvCell).join(','));
        const csv = '\ufeffID,用户,金额,分类,日期,备注\n' + rows.join('\n');
        const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }));
        const link = document.createElement('a');
        link.href = url;
        link.download = 'all-expenses.csv';
        link.click();
        URL.revokeObjectURL(url);
    }

    byId('refresh-admin').addEventListener('click', refresh);
    byId('export-admin').addEventListener('click', exportCsv);
    byId('users-list').addEventListener('click', event => {
        const button = event.target.closest('[data-delete-user]');
        if (button) deleteUser(button.dataset.deleteUser).catch(error => showNotice(error.message));
    });

    refresh();
})();
