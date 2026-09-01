(() => {
    const { getLanguage, locale, localizeMessage, t } = window.ExpenseI18n;
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    async function api(url, options = {}) {
        const method = (options.method || 'GET').toUpperCase();
        const headers = new Headers(options.headers || {});
        if (!['GET', 'HEAD', 'OPTIONS'].includes(method) && csrfToken && csrfHeader) {
            headers.set(csrfHeader, csrfToken);
        }

        const response = await fetch(url, { ...options, headers, credentials: 'same-origin' });
        if (response.status === 401 || (response.redirected && response.url.includes('/login'))) {
            window.location.assign('/login');
            throw new Error(t('common.sessionExpired'));
        }

        const contentType = response.headers.get('content-type') || '';
        const body = contentType.includes('application/json') ? await response.json() : null;
        if (!response.ok) {
            let message = localizeMessage(body?.message) || t('common.requestFailed', { status: response.status });
            if (body?.fieldErrors && Object.keys(body.fieldErrors).length) {
                const separator = getLanguage() === 'zh' ? '，' : ', ';
                message += (getLanguage() === 'zh' ? '：' : ': ') +
                        Object.values(body.fieldErrors).map(localizeMessage).join(separator);
            }
            throw new Error(message);
        }
        return response.status === 204 ? null : body;
    }

    function escapeHtml(value) {
        return String(value ?? '').replace(/[&<>"']/g, character => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'
        })[character]);
    }

    window.ExpenseApp = {
        api,
        escapeHtml,
        t,
        currency: {
            format: value => new Intl.NumberFormat(locale(), {
                style: 'currency',
                currency: 'CNY'
            }).format(value)
        }
    };
})();
