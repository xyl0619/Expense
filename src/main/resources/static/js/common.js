(() => {
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
            throw new Error('登录状态已失效');
        }

        const contentType = response.headers.get('content-type') || '';
        const body = contentType.includes('application/json') ? await response.json() : null;
        if (!response.ok) {
            let message = body?.message || `请求失败（${response.status}）`;
            if (body?.fieldErrors && Object.keys(body.fieldErrors).length) {
                message += '：' + Object.values(body.fieldErrors).join('，');
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
        currency: new Intl.NumberFormat('zh-CN', { style: 'currency', currency: 'CNY' })
    };
})();
