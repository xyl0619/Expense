(() => {
    const { api, currency, escapeHtml, t } = window.ExpenseApp;
    const byId = id => document.getElementById(id);
    const state = { page: 0, totalPages: 0, expenses: [] };
    let categoryChart;
    let monthlyChart;
    let noticeTimer;

    function localDate() {
        const date = new Date();
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    function filterParams(includePage = false) {
        const params = new URLSearchParams();
        const fields = {
            from: byId('filter-from').value,
            to: byId('filter-to').value,
            category: byId('filter-category').value.trim(),
            minAmount: byId('filter-min').value,
            maxAmount: byId('filter-max').value
        };
        Object.entries(fields).forEach(([key, value]) => value && params.set(key, value));
        if (includePage) {
            params.set('page', state.page);
            params.set('size', '10');
            params.append('sort', 'expenseDate,desc');
            params.append('sort', 'id,desc');
        }
        return params;
    }

    function showNotice(message, type = 'error') {
        const notice = byId('notice');
        clearTimeout(noticeTimer);
        notice.textContent = message;
        notice.className = `notice show ${type}`;
        noticeTimer = setTimeout(() => notice.classList.remove('show'), 4500);
    }

    async function refresh() {
        const results = await Promise.allSettled([loadExpenses(), loadAnalytics(), loadBudgets()]);
        const failure = results.find(result => result.status === 'rejected');
        if (failure) showNotice(failure.reason.message);
    }

    async function loadExpenses() {
        const page = await api(`/api/expenses/search?${filterParams(true)}`);
        state.expenses = page.content;
        state.totalPages = page.totalPages;
        byId('page-summary').textContent = t('dynamic.pageSummary', {
            total: page.totalElements,
            current: page.totalPages ? page.number + 1 : 0,
            pages: page.totalPages
        });
        byId('previous-page').disabled = page.first;
        byId('next-page').disabled = page.last || page.totalPages === 0;

        const rows = byId('expense-rows');
        if (!page.content.length) {
            rows.innerHTML = `<tr><td colspan="5" class="empty">${t('dynamic.noMatchingExpenses')}</td></tr>`;
            return;
        }
        rows.innerHTML = page.content.map(expense => `
            <tr>
                <td>${escapeHtml(expense.expenseDate)}</td>
                <td>${escapeHtml(expense.category)}</td>
                <td>${escapeHtml(expense.description || '—')}</td>
                <td class="amount">${currency.format(Number(expense.amount))}</td>
                <td><div class="actions">
                    <button class="btn secondary small" type="button" data-edit-expense="${expense.id}">${t('dynamic.edit')}</button>
                    <button class="btn danger small" type="button" data-delete-expense="${expense.id}">${t('dynamic.delete')}</button>
                </div></td>
            </tr>`).join('');
    }

    async function loadAnalytics() {
        const params = filterParams();
        params.delete('category');
        params.delete('minAmount');
        params.delete('maxAmount');
        const summary = await api(`/api/analytics/summary?${params}`);
        byId('total-spend').textContent = currency.format(Number(summary.totalAmount));
        byId('average-spend').textContent = currency.format(Number(summary.averageAmount));
        byId('transaction-count').textContent = summary.transactionCount;
        byId('top-category').textContent = summary.topCategory || '—';
        renderCharts(summary);
    }

    function renderCharts(summary) {
        if (typeof Chart === 'undefined') return;
        categoryChart?.destroy();
        monthlyChart?.destroy();
        categoryChart = new Chart(byId('category-chart'), {
            type: 'doughnut',
            data: {
                labels: summary.categoryBreakdown.map(item => item.category),
                datasets: [{
                    data: summary.categoryBreakdown.map(item => Number(item.totalAmount)),
                    backgroundColor: ['#4f46e5','#14b8a6','#f59e0b','#e11d48','#0ea5e9','#8b5cf6','#84cc16']
                }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
        });
        monthlyChart = new Chart(byId('monthly-chart'), {
            type: 'bar',
            data: {
                labels: summary.monthlyTrend.map(item => item.month),
                datasets: [{ label: t('dynamic.expenseDataset'), data: summary.monthlyTrend.map(item => Number(item.totalAmount)), backgroundColor: '#4f46e5', borderRadius: 5 }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true, ticks: { callback: value => currency.format(value) } } },
                plugins: { legend: { display: false } }
            }
        });
    }

    async function loadBudgets() {
        const month = byId('budget-month').value;
        const overview = await api(`/api/budgets?month=${encodeURIComponent(month)}`);
        byId('budget-overview').textContent = `${currency.format(Number(overview.totalSpent))} / ${currency.format(Number(overview.totalLimit))}`;
        const list = byId('budget-list');
        if (!overview.budgets.length) {
            list.innerHTML = `<div class="empty">${t('dynamic.noMonthlyBudget')}</div>`;
            return;
        }
        list.innerHTML = overview.budgets.map(budget => {
            const width = Math.min(Number(budget.utilizationPercent), 100);
            return `<div class="budget-item">
                <div class="budget-row"><div><div class="budget-title">${escapeHtml(budget.category)}</div><div class="budget-detail">${currency.format(Number(budget.spentAmount))} / ${currency.format(Number(budget.limitAmount))}</div></div><button class="btn secondary small" type="button" data-delete-budget="${budget.id}">${t('dynamic.delete')}</button></div>
                <div class="progress"><span class="${budget.exceeded ? 'exceeded' : ''}" style="width:${width}%"></span></div>
            </div>`;
        }).join('');
    }

    function openExpenseForm(expense) {
        byId('expense-form-panel').classList.add('open');
        byId('expense-id').value = expense?.id || '';
        byId('amount').value = expense?.amount || '';
        byId('category').value = expense?.category || '';
        byId('expense-date').value = expense?.expenseDate || localDate();
        byId('description').value = expense?.description || '';
        byId('amount').focus();
    }

    function closeExpenseForm() {
        byId('expense-form-panel').classList.remove('open');
        byId('expense-form').reset();
        byId('expense-id').value = '';
    }

    async function deleteExpense(id) {
        if (!window.confirm(t('dynamic.deleteExpenseConfirm'))) return;
        await api(`/api/expenses/${id}`, { method: 'DELETE' });
        showNotice(t('dynamic.expenseDeleted'), 'success');
        await refresh();
    }

    async function deleteBudget(id) {
        if (!window.confirm(t('dynamic.deleteBudgetConfirm'))) return;
        await api(`/api/budgets/${id}`, { method: 'DELETE' });
        showNotice(t('dynamic.budgetDeleted'), 'success');
        await loadBudgets();
    }

    byId('apply-filters').addEventListener('click', () => {
        state.page = 0;
        refresh();
    });
    byId('clear-filters').addEventListener('click', () => {
        ['filter-from','filter-to','filter-category','filter-min','filter-max'].forEach(id => byId(id).value = '');
        state.page = 0;
        refresh();
    });
    byId('add-expense').addEventListener('click', () => openExpenseForm());
    byId('cancel-expense').addEventListener('click', closeExpenseForm);
    byId('previous-page').addEventListener('click', () => {
        if (state.page > 0) {
            state.page--;
            loadExpenses().catch(error => showNotice(error.message));
        }
    });
    byId('next-page').addEventListener('click', () => {
        if (state.page + 1 < state.totalPages) {
            state.page++;
            loadExpenses().catch(error => showNotice(error.message));
        }
    });
    byId('budget-month').addEventListener('change', () => loadBudgets().catch(error => showNotice(error.message)));

    byId('expense-rows').addEventListener('click', event => {
        const editButton = event.target.closest('[data-edit-expense]');
        const deleteButton = event.target.closest('[data-delete-expense]');
        if (editButton) {
            const expense = state.expenses.find(item => item.id === Number(editButton.dataset.editExpense));
            if (expense) openExpenseForm(expense);
        }
        if (deleteButton) deleteExpense(deleteButton.dataset.deleteExpense).catch(error => showNotice(error.message));
    });

    byId('budget-list').addEventListener('click', event => {
        const button = event.target.closest('[data-delete-budget]');
        if (button) deleteBudget(button.dataset.deleteBudget).catch(error => showNotice(error.message));
    });

    byId('expense-form').addEventListener('submit', async event => {
        event.preventDefault();
        const id = byId('expense-id').value;
        const payload = {
            amount: Number(byId('amount').value),
            category: byId('category').value.trim(),
            expenseDate: byId('expense-date').value,
            description: byId('description').value.trim() || null
        };
        try {
            await api(id ? `/api/expenses/${id}` : '/api/expenses', {
                method: id ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            closeExpenseForm();
            showNotice(t(id ? 'dynamic.expenseUpdated' : 'dynamic.expenseAdded'), 'success');
            await refresh();
        } catch (error) {
            showNotice(error.message);
        }
    });

    byId('budget-form').addEventListener('submit', async event => {
        event.preventDefault();
        const payload = {
            month: byId('budget-month').value,
            category: byId('budget-category').value.trim(),
            limitAmount: Number(byId('budget-limit').value)
        };
        try {
            await api('/api/budgets', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            byId('budget-category').value = '';
            byId('budget-limit').value = '';
            showNotice(t('dynamic.budgetSaved'), 'success');
            await loadBudgets();
        } catch (error) {
            showNotice(error.message);
        }
    });

    byId('budget-month').value = localDate().slice(0, 7);
    const pageParams = new URLSearchParams(window.location.search);
    if (pageParams.has('forbidden')) {
        showNotice(t('dynamic.forbidden'));
        window.history.replaceState({}, '', '/dashboard');
    }
    window.addEventListener('expense-language-changed', refresh);
    refresh();
})();
