(() => {
    const storageKey = 'expense.language';
    const translations = {
        zh: {
            'language.label': '选择语言',
            'brand.mark': '轻',
            'brand.name': '轻账',
            'brand.tagline': '清楚记录每一笔，轻松掌握每个月',
            'nav.account': '账户入口',
            'nav.login': '登录',
            'nav.register': '免费开始',
            'nav.admin': '管理',
            'nav.logout': '退出',
            'nav.backDashboard': '返回仪表盘',
            'nav.backHome': '← 返回首页',
            'index.title': '轻账 · 让每一笔支出都有答案',
            'index.homeAria': '轻账首页',
            'index.heroEyebrow': '简单、清楚、只保留有用信息',
            'index.heroTitleStart': '把每一笔支出，',
            'index.heroTitleEnd': '变成可行动的判断',
            'index.heroDescription': '记录日常支出、设置分类预算、查看月度趋势。无需复杂配置，注册后即可开始。',
            'index.createLedger': '创建我的账本',
            'index.existingLogin': '已有账户，去登录',
            'index.pointLocal': '✓ 本地保存',
            'index.pointBudget': '✓ 分类预算',
            'index.pointCsv': '✓ CSV 导出',
            'index.previewAria': '轻账仪表盘预览',
            'index.septemberOverview': '九月概览',
            'index.todayStatus': '今天的财务状态',
            'index.monthSpend': '本月支出',
            'index.monthComparison': '较上月 ↓ 12%',
            'index.budgetRemaining': '预算剩余',
            'index.totalBudget': '总预算 ¥7,000',
            'index.recordCount': '记录笔数',
            'index.average': '平均 ¥112.79',
            'index.expenseTrend': '支出趋势',
            'index.sixMonths': '近 6 个月',
            'index.monthApr': '4月',
            'index.monthMay': '5月',
            'index.monthJun': '6月',
            'index.monthJul': '7月',
            'index.monthAug': '8月',
            'index.monthSep': '9月',
            'index.budgetProgress': '预算进度',
            'index.used': '已使用',
            'index.remaining': '剩余',
            'index.recentExpenses': '最近支出',
            'index.viewAll': '查看全部',
            'index.foodIcon': '餐',
            'index.lunch': '午餐',
            'index.foodToday': '餐饮 · 今天',
            'index.transitIcon': '行',
            'index.metro': '地铁通勤',
            'index.transitToday': '交通 · 今天',
            'index.featureEyebrow': '少一点复杂，多一点清楚',
            'index.featureTitle': '需要的能力，一个都不少',
            'index.quickRecord': '快速记账',
            'index.quickRecordDescription': '金额、分类、日期和备注，四项信息即可完成一笔记录。',
            'index.budgetAlert': '预算提醒',
            'index.budgetAlertDescription': '按月份和分类设置额度，超支状态清楚可见。',
            'index.trendAnalysis': '趋势分析',
            'index.trendAnalysisDescription': '分类占比与月度变化自动汇总，不再手工计算。',
            'index.footerBrand': '轻账 Expense Intelligence',
            'index.footerStorage': '数据保存在你的本地环境',
            'auth.loginTitle': '登录 · 轻账',
            'auth.loginError': '用户名或密码不正确',
            'auth.loggedOut': '已安全退出',
            'auth.registered': '注册成功，请登录',
            'auth.username': '用户名',
            'auth.password': '密码',
            'auth.loginButton': '登录',
            'auth.noAccount': '还没有账户？',
            'auth.registerNow': '立即注册',
            'auth.registerTitle': '注册 · 轻账',
            'auth.createAccount': '创建账户',
            'auth.createSubtitle': '只需三项信息，不收集多余资料',
            'auth.email': '邮箱',
            'auth.hasAccount': '已有账户？',
            'auth.backLogin': '返回登录',
            'dashboard.title': '轻账 · 仪表盘',
            'dashboard.tagline': '支出、预算和趋势，一页看清',
            'dashboard.overviewAria': '支出概览',
            'dashboard.totalSpend': '总支出',
            'dashboard.averageSpend': '平均每笔',
            'dashboard.recordCount': '记录数',
            'dashboard.topCategory': '最高分类',
            'dashboard.analytics': '分析',
            'dashboard.analyticsSubtitle': '按条件查看支出变化',
            'dashboard.startDate': '开始日期',
            'dashboard.endDate': '结束日期',
            'dashboard.category': '分类',
            'dashboard.allCategories': '全部分类',
            'dashboard.minAmount': '最低金额',
            'dashboard.maxAmount': '最高金额',
            'dashboard.search': '查询',
            'dashboard.clear': '清空',
            'dashboard.categoryShare': '分类占比',
            'dashboard.monthlyTrend': '月度趋势',
            'dashboard.monthlyBudget': '月度预算',
            'dashboard.noBudget': '暂无预算',
            'dashboard.month': '月份',
            'dashboard.limit': '额度',
            'dashboard.saveBudget': '保存预算',
            'dashboard.expenseRecords': '支出记录',
            'dashboard.expenseSubtitle': '新增、修改或删除你的记录',
            'dashboard.exportCsv': '导出 CSV',
            'dashboard.addExpense': '新增支出',
            'dashboard.amount': '金额',
            'dashboard.date': '日期',
            'dashboard.note': '备注',
            'dashboard.save': '保存',
            'dashboard.cancel': '取消',
            'dashboard.action': '操作',
            'dashboard.loading': '正在加载…',
            'dashboard.zeroRecords': '0 条记录',
            'dashboard.previous': '上一页',
            'dashboard.next': '下一页',
            'admin.title': '管理 · 轻账',
            'admin.center': '管理中心',
            'admin.tagline': '用户与全局支出概览',
            'admin.users': '用户',
            'admin.usersSubtitle': '查看账户并删除不再需要的用户',
            'admin.refresh': '刷新',
            'admin.email': '邮箱',
            'admin.role': '身份',
            'admin.allExpenses': '全部支出',
            'admin.expensesSubtitle': '所有用户的汇总记录',
            'admin.user': '用户',
            'common.sessionExpired': '登录状态已失效',
            'common.requestFailed': '请求失败（{status}）',
            'common.usernameRegistered': '用户名已被注册',
            'common.emailRegistered': '邮箱已被注册',
            'common.dataConflict': '请求与现有数据冲突',
            'common.validationFailed': '提交内容未通过验证',
            'common.required': '不能为空',
            'common.emailInvalid': '邮箱格式不正确',
            'common.sizeBetween': '长度必须在 {min} 到 {max} 个字符之间',
            'common.minimumValue': '数值不能小于 {min}',
            'common.startDateAfterEnd': '开始日期不能晚于结束日期',
            'common.analyticsRange': '分析日期范围不能超过五年',
            'common.minimumNegative': '最低金额不能为负数',
            'common.maximumNegative': '最高金额不能为负数',
            'common.minimumExceedsMaximum': '最低金额不能高于最高金额',
            'common.expenseOwnership': '不能修改其他用户的支出',
            'common.budgetOwnership': '不能删除其他用户的预算',
            'common.adminProtected': '不能在此删除管理员账号',
            'common.expenseNotFound': '未找到支出 {id}',
            'common.budgetNotFound': '未找到预算 {id}',
            'dynamic.pageSummary': '{total} 条记录 · 第 {current}/{pages} 页',
            'dynamic.noMatchingExpenses': '没有符合条件的记录',
            'dynamic.edit': '编辑',
            'dynamic.delete': '删除',
            'dynamic.expenseDataset': '支出',
            'dynamic.noMonthlyBudget': '尚未设置该月预算',
            'dynamic.deleteExpenseConfirm': '确定删除这条支出吗？',
            'dynamic.expenseDeleted': '支出已删除',
            'dynamic.deleteBudgetConfirm': '确定删除这项预算吗？',
            'dynamic.budgetDeleted': '预算已删除',
            'dynamic.expenseUpdated': '支出已更新',
            'dynamic.expenseAdded': '支出已添加',
            'dynamic.budgetSaved': '预算已保存',
            'dynamic.forbidden': '当前账号没有管理员权限',
            'dynamic.noUsers': '暂无用户',
            'dynamic.administrator': '管理员',
            'dynamic.regularUser': '普通用户',
            'dynamic.protected': '受保护',
            'dynamic.noExpenses': '暂无支出',
            'dynamic.uncategorized': '未分类',
            'dynamic.deleteUserConfirm': '删除用户会同时删除其支出和预算，确定继续吗？',
            'dynamic.userDeleted': '用户已删除',
            'dynamic.noExportData': '没有可导出的支出'
        },
        en: {
            'language.label': 'Choose language',
            'brand.mark': 'EI',
            'brand.name': 'Expense Intelligence',
            'brand.tagline': 'Track clearly. Understand every month.',
            'nav.account': 'Account access',
            'nav.login': 'Sign in',
            'nav.register': 'Get started',
            'nav.admin': 'Admin',
            'nav.logout': 'Sign out',
            'nav.backDashboard': 'Back to dashboard',
            'nav.backHome': '← Back to home',
            'index.title': 'Expense Intelligence · Make every expense useful',
            'index.homeAria': 'Expense Intelligence home',
            'index.heroEyebrow': 'Simple, clear, and focused on what matters',
            'index.heroTitleStart': 'Turn every expense',
            'index.heroTitleEnd': 'into an informed decision',
            'index.heroDescription': 'Track daily expenses, set category budgets, and understand monthly trends. No complicated setup—just create an account and begin.',
            'index.createLedger': 'Create my ledger',
            'index.existingLogin': 'I have an account',
            'index.pointLocal': '✓ Local storage',
            'index.pointBudget': '✓ Category budgets',
            'index.pointCsv': '✓ CSV export',
            'index.previewAria': 'Expense Intelligence dashboard preview',
            'index.septemberOverview': 'September overview',
            'index.todayStatus': 'Your finances today',
            'index.monthSpend': 'Monthly spending',
            'index.monthComparison': '↓ 12% from last month',
            'index.budgetRemaining': 'Budget remaining',
            'index.totalBudget': 'Total budget ¥7,000',
            'index.recordCount': 'Transactions',
            'index.average': 'Average ¥112.79',
            'index.expenseTrend': 'Spending trend',
            'index.sixMonths': 'Last 6 months',
            'index.monthApr': 'Apr',
            'index.monthMay': 'May',
            'index.monthJun': 'Jun',
            'index.monthJul': 'Jul',
            'index.monthAug': 'Aug',
            'index.monthSep': 'Sep',
            'index.budgetProgress': 'Budget progress',
            'index.used': 'Used',
            'index.remaining': 'Remaining',
            'index.recentExpenses': 'Recent expenses',
            'index.viewAll': 'View all',
            'index.foodIcon': 'F',
            'index.lunch': 'Lunch',
            'index.foodToday': 'Food · Today',
            'index.transitIcon': 'T',
            'index.metro': 'Metro commute',
            'index.transitToday': 'Transport · Today',
            'index.featureEyebrow': 'Less complexity, more clarity',
            'index.featureTitle': 'Everything you need, nothing you do not',
            'index.quickRecord': 'Fast expense entry',
            'index.quickRecordDescription': 'Amount, category, date, and note are all you need to record an expense.',
            'index.budgetAlert': 'Budget awareness',
            'index.budgetAlertDescription': 'Set monthly category limits and see overspending clearly.',
            'index.trendAnalysis': 'Trend analysis',
            'index.trendAnalysisDescription': 'Category shares and monthly changes are calculated automatically.',
            'index.footerBrand': 'Expense Intelligence',
            'index.footerStorage': 'Your data stays in your local environment',
            'auth.loginTitle': 'Sign in · Expense Intelligence',
            'auth.loginError': 'Incorrect username or password',
            'auth.loggedOut': 'You have signed out safely',
            'auth.registered': 'Registration complete. Please sign in.',
            'auth.username': 'Username',
            'auth.password': 'Password',
            'auth.loginButton': 'Sign in',
            'auth.noAccount': 'New here?',
            'auth.registerNow': 'Create an account',
            'auth.registerTitle': 'Register · Expense Intelligence',
            'auth.createAccount': 'Create account',
            'auth.createSubtitle': 'Only three fields. No unnecessary personal data.',
            'auth.email': 'Email',
            'auth.hasAccount': 'Already have an account?',
            'auth.backLogin': 'Back to sign in',
            'dashboard.title': 'Expense Intelligence · Dashboard',
            'dashboard.tagline': 'Expenses, budgets, and trends in one place',
            'dashboard.overviewAria': 'Expense overview',
            'dashboard.totalSpend': 'Total spending',
            'dashboard.averageSpend': 'Average expense',
            'dashboard.recordCount': 'Transactions',
            'dashboard.topCategory': 'Top category',
            'dashboard.analytics': 'Analytics',
            'dashboard.analyticsSubtitle': 'Explore spending with flexible filters',
            'dashboard.startDate': 'Start date',
            'dashboard.endDate': 'End date',
            'dashboard.category': 'Category',
            'dashboard.allCategories': 'All categories',
            'dashboard.minAmount': 'Minimum amount',
            'dashboard.maxAmount': 'Maximum amount',
            'dashboard.search': 'Apply',
            'dashboard.clear': 'Clear',
            'dashboard.categoryShare': 'Category share',
            'dashboard.monthlyTrend': 'Monthly trend',
            'dashboard.monthlyBudget': 'Monthly budget',
            'dashboard.noBudget': 'No budget yet',
            'dashboard.month': 'Month',
            'dashboard.limit': 'Limit',
            'dashboard.saveBudget': 'Save budget',
            'dashboard.expenseRecords': 'Expense records',
            'dashboard.expenseSubtitle': 'Add, edit, or remove your records',
            'dashboard.exportCsv': 'Export CSV',
            'dashboard.addExpense': 'Add expense',
            'dashboard.amount': 'Amount',
            'dashboard.date': 'Date',
            'dashboard.note': 'Note',
            'dashboard.save': 'Save',
            'dashboard.cancel': 'Cancel',
            'dashboard.action': 'Actions',
            'dashboard.loading': 'Loading…',
            'dashboard.zeroRecords': '0 records',
            'dashboard.previous': 'Previous',
            'dashboard.next': 'Next',
            'admin.title': 'Admin · Expense Intelligence',
            'admin.center': 'Admin center',
            'admin.tagline': 'Users and global expense overview',
            'admin.users': 'Users',
            'admin.usersSubtitle': 'Review accounts and remove users no longer needed',
            'admin.refresh': 'Refresh',
            'admin.email': 'Email',
            'admin.role': 'Role',
            'admin.allExpenses': 'All expenses',
            'admin.expensesSubtitle': 'Combined records across all users',
            'admin.user': 'User',
            'common.sessionExpired': 'Your session has expired',
            'common.requestFailed': 'Request failed ({status})',
            'common.usernameRegistered': 'This username is already registered',
            'common.emailRegistered': 'This email is already registered',
            'common.dataConflict': 'The request conflicts with existing data',
            'common.validationFailed': 'Please check the submitted information',
            'common.required': 'Must not be blank',
            'common.emailInvalid': 'Enter a valid email address',
            'common.sizeBetween': 'Length must be between {min} and {max} characters',
            'common.minimumValue': 'Value must be at least {min}',
            'common.startDateAfterEnd': 'The start date must not be after the end date',
            'common.analyticsRange': 'The analytics date range cannot exceed five years',
            'common.minimumNegative': 'The minimum amount must not be negative',
            'common.maximumNegative': 'The maximum amount must not be negative',
            'common.minimumExceedsMaximum': 'The minimum amount must not exceed the maximum amount',
            'common.expenseOwnership': "You cannot modify another user's expense",
            'common.budgetOwnership': "You cannot delete another user's budget",
            'common.adminProtected': 'Administrator accounts cannot be deleted here',
            'common.expenseNotFound': 'Expense {id} was not found',
            'common.budgetNotFound': 'Budget {id} was not found',
            'dynamic.pageSummary': '{total} records · Page {current} of {pages}',
            'dynamic.noMatchingExpenses': 'No expenses match these filters',
            'dynamic.edit': 'Edit',
            'dynamic.delete': 'Delete',
            'dynamic.expenseDataset': 'Spending',
            'dynamic.noMonthlyBudget': 'No budget has been set for this month',
            'dynamic.deleteExpenseConfirm': 'Delete this expense?',
            'dynamic.expenseDeleted': 'Expense deleted',
            'dynamic.deleteBudgetConfirm': 'Delete this budget?',
            'dynamic.budgetDeleted': 'Budget deleted',
            'dynamic.expenseUpdated': 'Expense updated',
            'dynamic.expenseAdded': 'Expense added',
            'dynamic.budgetSaved': 'Budget saved',
            'dynamic.forbidden': 'Your account does not have administrator access',
            'dynamic.noUsers': 'No users found',
            'dynamic.administrator': 'Administrator',
            'dynamic.regularUser': 'User',
            'dynamic.protected': 'Protected',
            'dynamic.noExpenses': 'No expenses found',
            'dynamic.uncategorized': 'Uncategorized',
            'dynamic.deleteUserConfirm': 'Deleting this user also removes their expenses and budgets. Continue?',
            'dynamic.userDeleted': 'User deleted',
            'dynamic.noExportData': 'There are no expenses to export'
        }
    };

    function storedLanguage() {
        try {
            const value = localStorage.getItem(storageKey);
            return translations[value] ? value : null;
        } catch (error) {
            return null;
        }
    }

    const initialLanguage = storedLanguage();
    let currentLanguage = initialLanguage || 'zh';
    let languageChosen = Boolean(initialLanguage);
    const serverMessageKeys = {
        'Username is already registered': 'common.usernameRegistered',
        'Email is already registered': 'common.emailRegistered',
        'The request conflicts with existing data': 'common.dataConflict',
        'Request validation failed': 'common.validationFailed',
        'The start date must not be after the end date': 'common.startDateAfterEnd',
        'Analytics date range must not exceed five years': 'common.analyticsRange',
        'Minimum amount must not be negative': 'common.minimumNegative',
        'Maximum amount must not be negative': 'common.maximumNegative',
        'Minimum amount must not exceed maximum amount': 'common.minimumExceedsMaximum',
        "You cannot modify another user's expense": 'common.expenseOwnership',
        "You cannot delete another user's budget": 'common.budgetOwnership',
        'Administrator accounts cannot be deleted here': 'common.adminProtected'
    };

    function t(key, parameters = {}) {
        const template = translations[currentLanguage][key] ?? translations.zh[key] ?? key;
        return Object.entries(parameters).reduce(
                (text, [name, value]) => text.replaceAll(`{${name}}`, String(value)),
                template
        );
    }

    function localizeMessage(message) {
        if (!message) return message;
        if (serverMessageKeys[message]) return t(serverMessageKeys[message]);
        if (message === 'must not be blank' || message === 'must not be null') return t('common.required');
        if (message === 'must be a well-formed email address') return t('common.emailInvalid');
        const size = message.match(/^size must be between (\d+) and (\d+)$/);
        if (size) return t('common.sizeBetween', { min: size[1], max: size[2] });
        const minimum = message.match(/^must be greater than or equal to (.+)$/);
        if (minimum) return t('common.minimumValue', { min: minimum[1] });
        const notFound = message.match(/^(Expense|Budget) (\d+) was not found$/);
        if (notFound) {
            return t(notFound[1] === 'Expense' ? 'common.expenseNotFound' : 'common.budgetNotFound', {
                id: notFound[2]
            });
        }
        return message;
    }

    function apply(root = document) {
        document.documentElement.lang = currentLanguage === 'zh' ? 'zh-CN' : 'en';
        root.querySelectorAll('[data-i18n]').forEach(element => {
            element.textContent = t(element.dataset.i18n);
        });
        root.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
            element.setAttribute('placeholder', t(element.dataset.i18nPlaceholder));
        });
        root.querySelectorAll('[data-i18n-aria-label]').forEach(element => {
            element.setAttribute('aria-label', t(element.dataset.i18nAriaLabel));
        });
        root.querySelectorAll('[data-language-query]').forEach(element => {
            if (!element.dataset.baseHref) {
                element.dataset.baseHref = element.getAttribute('href');
            }
            const url = new URL(element.dataset.baseHref, window.location.origin);
            url.searchParams.set('lang', currentLanguage);
            element.setAttribute('href', `${url.pathname}${url.search}`);
        });
        root.querySelectorAll('[data-validation-message]').forEach(element => {
            if (!element.dataset.sourceMessage) {
                element.dataset.sourceMessage = element.textContent.trim();
            }
            element.textContent = localizeMessage(element.dataset.sourceMessage);
        });
        document.querySelectorAll('[data-language]').forEach(button => {
            const active = button.dataset.language === currentLanguage;
            button.classList.toggle('active', active);
            button.setAttribute('aria-pressed', String(active));
        });

        const welcome = document.querySelector('[data-language-welcome]');
        if (welcome) {
            const choosing = !languageChosen;
            welcome.hidden = !choosing;
            document.body.classList.toggle('language-choice-open', choosing);
        }
    }

    function setLanguage(language) {
        if (!translations[language]) return;
        currentLanguage = language;
        languageChosen = true;
        try {
            localStorage.setItem(storageKey, language);
        } catch (error) {
            // The page still switches language when browser storage is unavailable.
        }
        apply();
        window.dispatchEvent(new CustomEvent('expense-language-changed', {
            detail: { language }
        }));
    }

    function initialize() {
        apply();
        document.addEventListener('click', event => {
            const button = event.target.closest('[data-language]');
            if (button) setLanguage(button.dataset.language);
        });
    }

    window.ExpenseI18n = {
        apply,
        getLanguage: () => currentLanguage,
        localizeMessage,
        locale: () => currentLanguage === 'zh' ? 'zh-CN' : 'en-US',
        setLanguage,
        t
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initialize, { once: true });
    } else {
        initialize();
    }
})();
